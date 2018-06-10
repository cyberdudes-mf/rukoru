package hoshisugi.rukoru.app.services.excel;

import java.io.IOException;
import java.nio.file.Path;

import hoshisugi.rukoru.app.models.common.AsyncResult;
import javafx.scene.image.Image;

public interface ExcelService {

	int BUFF_SIZE = 50;

	AsyncResult trace(Image image, Path destination) throws IOException;
}
