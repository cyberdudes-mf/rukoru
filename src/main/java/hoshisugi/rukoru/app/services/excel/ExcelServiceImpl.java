package hoshisugi.rukoru.app.services.excel;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import hoshisugi.rukoru.app.models.common.AsyncResult;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;

public class ExcelServiceImpl implements ExcelService {

	@Override
	public AsyncResult trace(final Image image, final Path destination) throws IOException {
		final AsyncResult result = new AsyncResult();
		final Map<Integer, XSSFCellStyle> cache = new HashMap<>();

		ConcurrentUtil.run(() -> {
			try (final XSSFWorkbook book = new XSSFWorkbook()) {
				final int width = (int) image.getWidth();
				final int height = (int) image.getHeight();
				result.setTotal(width * height);

				final XSSFSheet sheet = createSheet(book, height, width);
				final PixelReader reader = image.getPixelReader();

				for (int currentY = 0; currentY < height; currentY++) {
					final int readableHeight = Math.min(BUFF_SIZE, height - currentY);
					for (int currentX = 0; currentX < width; currentX++) {
						final int readableWidth = Math.min(BUFF_SIZE, width - currentX);
						final int[] argbs = getArgbs(reader, currentX, currentY, readableWidth, readableHeight);
						for (int i = 0; i < argbs.length; i++) {
							final int argb = argbs[i];
							if (argb == 0) {
								continue;
							}

							final int rowIndex = currentY + (i / readableWidth);
							final XSSFRow row = sheet.getRow(rowIndex);

							final int colIndex = currentX + (i % readableWidth);
							final XSSFCell cell = row.getCell(colIndex);

							synchronized (sheet) {
								cell.setCellStyle(getCellStyle(book, argb, cache));
							}

							result.addCurrent(1);
						}
						currentX += readableWidth - 1;
					}
					currentY += readableHeight - 1;
				}
				saveBook(book, destination);
			} catch (final Exception e) {
				result.setThrown(e);
			}
		});
		return result;
	}

	private XSSFSheet createSheet(final XSSFWorkbook book, final int rows, final int cols) {
		final XSSFSheet sheet = book.createSheet();
		sheet.setDisplayGridlines(false);
		IntStream.range(0, cols).forEach(i -> sheet.setColumnWidth(i, 50));
		IntStream.range(0, rows).mapToObj(sheet::createRow).peek(r -> r.setHeight((short) 30))
				.forEach(r -> IntStream.range(0, cols).forEach(r::createCell));
		return sheet;
	}

	private void saveBook(final XSSFWorkbook book, final Path destination) throws IOException {
		try (OutputStream stream = Files.newOutputStream(destination, CREATE, TRUNCATE_EXISTING)) {
			book.write(stream);
		}
	}

	private int[] getArgbs(final PixelReader reader, final int x, final int y, final int w, final int h) {
		final WritablePixelFormat<IntBuffer> pixelformat = PixelFormat.getIntArgbPreInstance();
		final int[] argbs = new int[w * h];
		synchronized (reader) {
			reader.getPixels(x, y, w, h, pixelformat, argbs, 0, w);
		}
		return argbs;
	}

	private XSSFCellStyle getCellStyle(final XSSFWorkbook book, final int argb,
			final Map<Integer, XSSFCellStyle> cache) {
		if (cache.containsKey(argb)) {
			return cache.get(argb);
		}
		final XSSFCellStyle style = book.createCellStyle();
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setFillForegroundColor(createColor(argb));
		cache.put(argb, style);
		return style;
	}

	private XSSFColor createColor(final int argb) {
		final XSSFColor c = new XSSFColor();
		final String argbStr = String.format("%08x", argb);
		c.setARGBHex(argbStr);
		return c;
	}
}
