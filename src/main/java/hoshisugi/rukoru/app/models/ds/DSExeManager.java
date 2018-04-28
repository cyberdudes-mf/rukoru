package hoshisugi.rukoru.app.models.ds;

import java.util.function.Consumer;

import hoshisugi.rukoru.framework.cli.CLIState;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;
import javafx.application.Platform;

public class DSExeManager extends DSManagerBase {

	public DSExeManager(final DSEntry entry) {
		super(entry);
	}

	@Override
	public void startServer() {
		startServer(super::onServerStarted);
	}

	@Override
	public void stopServer() {
		entry.setServerButtonDisable(true);
		if (entry.isServerButtonSelected()) {
			entry.setServerButtonSelected(false);
		}
		final DSSetting dsSetting = entry.getDsSetting();
		ConcurrentUtil.run(() -> service.stopServerExe(dsSetting, super::onServerStopped));
	}

	@Override
	public void startStudio() {
		entry.setStudioButtonDisable(true);
		if (!entry.isStudioButtonSelected()) {
			entry.setStudioButtonSelected(true);
		}
		final DSSetting dsSetting = entry.getDsSetting();
		final DSLogWriter logWriter = entry.getStudioLogWriter();
		ConcurrentUtil.run(() -> service.startStudioExe(dsSetting, logWriter, super::onStudioStarted));
	}

	@Override
	public void stopStudio() {
		stopStudio(super::onStudioStopped);
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
		stopStudio(state -> {
			Platform.runLater(() -> {
				super.onStudioStopped(state);
				stopServer();
			});
		});
	}

	public void startServer(final Consumer<CLIState> callback) {
		entry.setServerButtonDisable(true);
		if (!entry.isServerButtonSelected()) {
			entry.setServerButtonSelected(true);
		}
		final DSSetting dsSetting = entry.getDsSetting();
		final DSLogWriter logWriter = entry.getServerLogWriter();
		ConcurrentUtil.run(() -> service.startServerExe(dsSetting, logWriter, callback));
	}

	public void stopStudio(final Consumer<CLIState> callback) {
		entry.setStudioButtonDisable(true);
		if (entry.isStudioButtonSelected()) {
			entry.setStudioButtonSelected(false);
		}
		final DSSetting dsSetting = entry.getDsSetting();
		ConcurrentUtil.run(() -> service.stopStudioExe(dsSetting, callback));
	}

}
