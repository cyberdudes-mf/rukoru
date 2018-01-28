package hoshisugi.rukoru.app.view.s3;

import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import hoshisugi.rukoru.app.models.s3.ExplorerSelection;
import hoshisugi.rukoru.app.models.s3.S3Item;
import hoshisugi.rukoru.app.models.s3.S3Root;
import hoshisugi.rukoru.app.models.settings.Credential;
import hoshisugi.rukoru.app.services.s3.S3Service;
import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;

public class S3ExplorerController extends BaseController {

	@FXML
	private SplitPane splitPane;

	@FXML
	private VBox bottom;

	@Inject
	private S3Service s3Service;

	private final ExplorerSelection selection = new ExplorerSelection(20);

	private final S3Root rootItem = new S3Root();

	@Override
	public void initialize(final URL url, final ResourceBundle resource) {
		selection.select(rootItem);
		reload(rootItem);
	}

	public ExplorerSelection getSelection() {
		return selection;
	}

	public S3Root getRootItem() {
		return rootItem;
	}

	public void reload(final S3Item item) {
		ConcurrentUtil.run(() -> {
			if (Credential.hasCredential()) {
				s3Service.updateItems(item);
			}
		});
	}

	public void addBottom(final Node node) {
		bottom.getChildren().add(node);
	}

	public void removeBottom(final Node node) {
		bottom.getChildren().remove(node);
	}
}
