package hoshisugi.rukoru.app.models.ds;

import java.util.function.Consumer;

import hoshisugi.rukoru.framework.cli.CLIState;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;
import javafx.application.Platform;

public class DSBatManager extends DSManagerBase {

	public DSBatManager(final DSEntry entry) {
		super(entry);
	}

	@Override
	public void startServer() {
		startServer(super::onServerStarted);
	}

	@Override
	public void stopServer() {
		entry.setServerButtonDisable(true);
		final DSSetting dsSetting = entry.getDsSetting();
		ConcurrentUtil.run(() -> service.stopServerBat(dsSetting, super::onServerStopped));
	}

	@Override
	public void startStudio() {
		entry.setStudioButtonDisable(true);
		if (!entry.isStudioButtonSelected()) {
			entry.setStudioButtonSelected(true);
		}
		final DSSetting dsSetting = entry.getDsSetting();
		final DSLogWriter logWriter = entry.getStudioLogWriter();
		ConcurrentUtil.run(() -> service.startStudioBat(dsSetting, logWriter, super::onStudioStarted));
	}

	@Override
	public void stopStudio() {
		stopStudio(super::onStudioStarted);
	}

	@Override
	public void startBoth() {
		startServer(state -> {
			Platform.runLater(() -> {
				super.onServerStarted(state);
				startStudio();
			});
		});
	}

	@Override
	public void stopBoth() {
		stopStudio(cliState -> {
			Platform.runLater(() -> {
				super.onStudioStopped(cliState);
				stopServer();
			});
		});
	}

	public void startServer(final Consumer<CLIState> callback) {
		entry.setServerButtonDisable(true);
		final DSSetting dsSetting = entry.getDsSetting();
		final DSLogWriter logWriter = entry.getServerLogWriter();
		ConcurrentUtil.run(() -> service.startServerBat(dsSetting, logWriter, callback));
	}

	public void stopStudio(final Consumer<CLIState> callback) {
		entry.setStudioButtonDisable(true);
		final DSSetting dsSetting = entry.getDsSetting();
		ConcurrentUtil.run(() -> service.stopStudioBat(dsSetting, callback));
	}

}
