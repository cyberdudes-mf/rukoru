package hoshisugi.rukoru.app.view.settings;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import hoshisugi.rukoru.framework.annotations.FXController;
import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.util.DialogUtil;
import hoshisugi.rukoru.framework.util.FXUtil;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;;

@FXController(title = "Preferences")
public class PreferencesController extends BaseController {

	@FXML
	private TreeView<String> treeView;

	@FXML
	private AnchorPane contentView;

	@FXML
	private Button applyAndCloseButton;

	private final Map<String, Class<? extends BaseController>> contents = new HashMap<>();

	private PreferenceContent content;

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {
		createTreeItems();
		treeView.getSelectionModel().selectedItemProperty().addListener(this::onSelectedItemChanged);
		applyAndCloseButton.disableProperty().bind(treeView.getSelectionModel().selectedItemProperty().isNull());
	}

	@FXML
	private void onApplyAndCloseButtonClick(final ActionEvent event) {
		if (content != null) {
			content.apply();
		}
		FXUtil.getStage(event).close();
	}

	@FXML
	private void onCancelButtonClick(final ActionEvent event) {
		FXUtil.getStage(event).close();
	}

	private void createTreeItems() {
		final TreeItem<String> root = new TreeItem<>("Preferences");
		root.setExpanded(true);
		treeView.setRoot(root);
		addTreeItems(root);
	}

	private void addTreeItems(final TreeItem<String> root) {
		registerContent(root, HomePreferenceController.class);
		registerContent(root, MicrosoftSDKPreferenceController.class);
	}

	private void registerContent(final TreeItem<String> root, final Class<? extends BaseController> controller) {
		final String title = FXUtil.getTitle(controller);
		final TreeItem<String> item = new TreeItem<>(title);
		root.getChildren().add(item);
		contents.put(title, controller);
	}

	private void onSelectedItemChanged(final ObservableValue<? extends TreeItem<String>> observable,
			final TreeItem<String> oldValue, final TreeItem<String> newValue) {
		final String value = newValue.getValue();
		if (newValue == null || !contents.containsKey(value)) {
			content = null;
			return;
		}
		try {
			final FXMLLoader fxmlLoader = new FXMLLoader(FXUtil.getURL(contents.get(value)));
			final Parent parent = fxmlLoader.load();
			content = (PreferenceContent) fxmlLoader.getController();
			showContent(parent);
		} catch (final IOException e) {
			DialogUtil.showErrorDialog(e);
		}
	}

	private void showContent(final Parent parent) {
		final ObservableList<Node> children = contentView.getChildren();
		children.clear();
		children.add(parent);
		AnchorPane.setTopAnchor(parent, 0d);
		AnchorPane.setLeftAnchor(parent, 0d);
		AnchorPane.setBottomAnchor(parent, 0d);
		AnchorPane.setRightAnchor(parent, 0d);
	}
}
