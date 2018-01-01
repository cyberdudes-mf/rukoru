package hoshisugi.rukoru.flamework.controls;

import java.util.function.Function;

import com.google.common.base.Strings;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Callback;

public class TextFillTableCell<S> extends TableCell<S, String> {

	private final Function<String, Paint> paintProvider;

	public static <S> Callback<TableColumn<S, String>, TableCell<S, String>> forTableCellFactory(
			final Function<String, Paint> paintProvider) {
		return item -> new TextFillTableCell<>(paintProvider);
	}

	private TextFillTableCell(final Function<String, Paint> paintProvider) {
		this.paintProvider = paintProvider;
	}

	@Override
	protected void updateItem(final String state, final boolean empty) {
		super.updateItem(state, empty);
		if (!empty && !Strings.isNullOrEmpty(state)) {
			setText(state);
			setTextFill(paintProvider.apply(state));
		} else {
			setText("");
			setTextFill(Color.BLACK);
		}
	}

}
