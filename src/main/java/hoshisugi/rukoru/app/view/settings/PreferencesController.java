package hoshisugi.rukoru.app.view.settings;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import hoshisugi.rukoru.app.models.hidden.HiddenManager;
import hoshisugi.rukoru.framework.annotations.FXController;
import hoshisugi.rukoru.framework.annotations.Hidden;
import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.controls.FilterableTreeItem;
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
import javafx.scene.input.KeyEvent;
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

	@FXML
	private void onKeyPressed(final KeyEvent event) {
		if (HiddenManager.canShowHidden(event.getCode())) {
			final FilterableTreeItem<String> root = (FilterableTreeItem<String>) treeView.getRoot();
			root.setPredicate(null);
		}
	}

	private void createTreeItems() {
		final FilterableTreeItem<String> root = new FilterableTreeItem<>("Preferences");
		root.setExpanded(true);
		treeView.setRoot(root);
		addTreeItems(root);
		root.setPredicate(s -> !HiddenManager.isHidden() || contents.get(s).getAnnotation(Hidden.class) == null);
	}

	private void addTreeItems(final FilterableTreeItem<String> root) {
		final ObservableList<TreeItem<String>> rootChildren = root.getSourceChildren();
		registerContent(rootChildren, HomePreferenceController.class);
		registerContent(rootChildren, DSSettingsController.class);
		{
			final TreeItem<String> child = new TreeItem<>("Authentication");
			final ObservableList<TreeItem<String>> authChildren = child.getChildren();
			registerContent(authChildren, RedminePreferenceController.class);
			registerContent(authChildren, CredentialSettingController.class);
			registerContent(authChildren, RepositoryDBSettingController.class);
			rootChildren.add(child);
		}
		registerContent(rootChildren, ScrumSettingController.class);
		registerContent(rootChildren, ModuleActivationController.class);
		registerContent(rootChildren, VideoSettingController.class);
	}

	private void registerContent(final ObservableList<TreeItem<String>> children,
			final Class<? extends BaseController> controller) {
		final String title = FXUtil.getTitle(controller);
		final TreeItem<String> item = new TreeItem<>(title);
		children.add(item);
		contents.put(title, controller);
	}

	private void onSelectedItemChanged(final ObservableValue<? extends TreeItem<String>> observable,
			final TreeItem<String> oldValue, final TreeItem<String> newValue) {
		if (newValue == null || !contents.containsKey(newValue.getValue())) {
			content = null;
			return;
		}
		try {
			final FXMLLoader fxmlLoader = new FXMLLoader(FXUtil.getURL(contents.get(newValue.getValue())));
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
