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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
				final FXMLLoader loader = new FXMLLoader(FXUtil.getURL(DSEntryController.class));
				final Accordion view = loader.load();
				final DSEntryController controller = loader.getController();
				controller.loadSetting(dsSetting);
				layoutRoot.getChildren().add(view);
			}
		});
	}

}
