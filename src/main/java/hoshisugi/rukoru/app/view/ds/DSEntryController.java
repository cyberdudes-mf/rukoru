package hoshisugi.rukoru.app.view.ds;

import static hoshisugi.rukoru.framework.util.AssetUtil.getImage;
import static javafx.beans.binding.Bindings.when;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

import hoshisugi.rukoru.app.models.ds.DSLogWriter;
import hoshisugi.rukoru.app.models.ds.DSSetting;
import hoshisugi.rukoru.app.services.ds.DSService;
import hoshisugi.rukoru.framework.base.BaseController;
import hoshisugi.rukoru.framework.cli.CLI;
import hoshisugi.rukoru.framework.cli.CLIState;
import hoshisugi.rukoru.framework.util.AssetUtil;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;
import hoshisugi.rukoru.framework.util.FXUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.stage.WindowEvent;

public class DSEntryController extends BaseController {

	@FXML
	private Accordion accordion;

	@FXML
	private TitledPane titledPane;

	@FXML
	private Button openHomeButton;

	@FXML
	private Label name;

	@FXML
	private ToggleButton controlServerButton;

	@FXML
	private ToggleButton controlStudioButton;

	@FXML
	private ToggleButton controlAllButton;

	@FXML
	private TextArea serverLogText;

	@FXML
	private TextArea studioLogText;

	@Inject
	private DSService service;

	private DSSetting dsSetting;

	private final EventHandler<WindowEvent> stopOnExit = e -> this.stopOnExit();

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {
		openHomeButton.setGraphic(new ImageView(AssetUtil.getImage("32x32/folder.png")));
		controlServerButton.graphicProperty().bind(when(controlServerButton.selectedProperty().not())
				.then(new ImageView(getImage("32x32/run.png"))).otherwise(new ImageView(getImage("32x32/stop.png"))));
		controlStudioButton.graphicProperty().bind(when(controlStudioButton.selectedProperty().not())
				.then(new ImageView(getImage("32x32/run.png"))).otherwise(new ImageView(getImage("32x32/stop.png"))));
		controlAllButton.graphicProperty().bind(when(controlAllButton.selectedProperty().not())
				.then(new ImageView(getImage("32x32/run.png"))).otherwise(new ImageView(getImage("32x32/stop.png"))));
	}

	@FXML
	private void onOpenHomeButtonClick(final ActionEvent event) {
		CLI.command("explorer").options(dsSetting.getExecutionPath()).execute();
	}

	@FXML
	private void onControlServerButtonClick(final ActionEvent event) {
		if (accordion.getExpandedPane() == null) {
			accordion.setExpandedPane(titledPane);
		}
		controlServerButton.setDisable(true);
		if (controlServerButton.isSelected()) {
			serverLogText.clear();
			ConcurrentUtil.run(
					() -> service.startServerWithExe(dsSetting, new DSLogWriter(serverLogText), this::onServerStarted));
		} else {
			controlServerButton.setDisable(true);
			ConcurrentUtil.run(() -> service.stopServerWithExe(dsSetting, this::onServerStopped));
		}
	}

	@FXML
	private void onControlStudioButtonClick(final ActionEvent event) {
		ConcurrentUtil.run(() -> {
			studioLogText.clear();
			final ProcessBuilder pb = new ProcessBuilder();
			final List<String> commands = Lists.newArrayList("cmd", "/c",
					dsSetting.getExecutionPath() + "/client/bin/DataSpiderStudio.exe");
			pb.command(commands);
			final Process p = pb.start();
			try (final BufferedInputStream bi = new BufferedInputStream(p.getInputStream());
					final BufferedReader br = new BufferedReader(new InputStreamReader(bi, "MS932"));) {
				while (true) {
					try {
						final String lines = br.readLine();
						Thread.sleep(50);
						if (lines != null) {
							studioLogText.appendText(lines + "\r\n");
						}
					} catch (final Exception e) {
					}
				}
			}
			// final CLIBuilder builder = CLI.command("DataSpiderServer.exe")
			// .directory(Paths.get(dsSetting.getExecutionPath() + "/server/bin/"));
			// cliState = builder.execute();
			// final BufferedReader br = new BufferedReader(new
			// InputStreamReader(cliState.getInputStream()));
			// br.lines().forEach(System.out::println);
		});
		;
	}

	@FXML
	private void onControlAllButtonClick(final ActionEvent event) {
		System.out.println("onControlAllButtonClick");
	}

	public void loadSetting(final DSSetting dsSetting) {
		name.setText(dsSetting.getName());
		this.dsSetting = dsSetting;
	}

	private void onServerStarted(final CLIState cliState) {
		if (controlServerButton.isDisable()) {
			Platform.runLater(() -> controlServerButton.setDisable(false));
		}
		if (cliState.isSuccess()) {
			FXUtil.getPrimaryStage().setOnCloseRequest(stopOnExit);
		}
		if (cliState.isFailure()) {
			Platform.runLater(() -> controlServerButton.setSelected(false));
		}
	}

	private void onServerStopped(final CLIState cliState) {
		if (!controlServerButton.isDisable()) {
			Platform.runLater(() -> controlServerButton.setDisable(true));
		}
		if (cliState.isSuccess()) {
			FXUtil.getPrimaryStage().removeEventHandler(new EventType<WindowEvent>("WindowEvent"), stopOnExit);
		}
		if (cliState.isFailure()) {
			Platform.runLater(() -> controlServerButton.setSelected(true));
		}
	}

	private void stopOnExit() {
		try {
			if (controlServerButton.isSelected()) {
				service.stopServerWithExe(dsSetting, this::onServerStopped);
			}
		} catch (final InterruptedException e) {
		}
	}
}
