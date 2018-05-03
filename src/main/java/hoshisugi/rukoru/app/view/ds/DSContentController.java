package hoshisugi.rukoru.app.view.ds;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import hoshisugi.rukoru.app.models.ds.DSSetting;
import hoshisugi.rukoru.app.services.settings.LocalSettingService;
import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;
import hoshisugi.rukoru.framework.util.FXUtil;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.layout.VBox;

public class DSContentController extends BaseController {

	@FXML
	private VBox layoutRoot;

	@Inject
	private LocalSettingService service;

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {
		ConcurrentUtil.run(() -> {
			final List<DSSetting> dsSettings = service.loadDSSettings();
			for (final DSSetting dsSetting : dsSettings) {
				final Accordion view = createEntry(dsSetting);
				layoutRoot.getChildren().add(view);
			}
		});
	}

	private Accordion createEntry(final DSSetting dsSetting) {
		final Accordion view = FXUtil.load(DSEntryController.class);
		final DSEntryController controller = (DSEntryController) view.getUserData();
		controller.loadSetting(dsSetting);
		return view;
	}

	public void refresh(final ObservableList<DSSetting> dsSettings) {
		final ObservableList<Node> children = layoutRoot.getChildren();
		final Node[] nodes = children.toArray(new Node[children.size()]);
		children.clear();

		for (final DSSetting setting : dsSettings) {
			final Accordion node = findNode(nodes, setting.getId());
			if (node != null) {
				final DSEntryController controller = (DSEntryController) node.getUserData();
				controller.loadSetting(setting);
				children.add(node);
			} else {
				children.add(createEntry(setting));
			}

		}
	}

	private Accordion findNode(final Node[] nodes, final Integer id) {
		for (final Node node : nodes) {
			final DSEntryController controller = (DSEntryController) node.getUserData();
			if (controller.getDSSettingId().equals(id)) {
				return (Accordion) node;
			}
		}
		return null;
	}

}
