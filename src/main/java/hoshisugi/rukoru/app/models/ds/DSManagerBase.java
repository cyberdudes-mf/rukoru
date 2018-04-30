package hoshisugi.rukoru.app.models.ds;

import java.io.IOException;
import java.util.function.Consumer;

import hoshisugi.rukoru.app.services.ds.DSService;
import hoshisugi.rukoru.framework.cli.CLIState;
import hoshisugi.rukoru.framework.inject.Injector;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;
import javafx.application.Platform;

public abstract class DSManagerBase implements DSManager {

	protected final DSService service;
	protected final DSEntry entry;

	public DSManagerBase(final DSEntry entry) {
		this.entry = entry;
		service = Injector.getInstance(DSService.class);
	}

	@Override
	public void startServer() {
		startServer(this::onServerStarted);
	}

	@Override
	public void stopServer() {
		entry.setServerButtonDisable(true);
		if (entry.isServerButtonSelected()) {
			entry.setServerButtonSelected(false);
		}
		final DSSetting dsSetting = entry.getDsSetting();
		ConcurrentUtil.run(() -> stopServer(dsSetting, this::onServerStopped));
	}

	@Override
	public void startStudio() {
		entry.setStudioButtonDisable(true);
		if (!entry.isStudioButtonSelected()) {
			entry.setStudioButtonSelected(true);
		}
		final DSSetting dsSetting = entry.getDsSetting();
		final DSLogWriter logWriter = entry.getStudioLogWriter();
		ConcurrentUtil.run(() -> startStudio(dsSetting, logWriter, this::onStudioStarted));
	}

	@Override
	public void stopStudio() {
		stopStudio(this::onStudioStopped);
	}

	@Override
	public void startBoth() {
		startServer(state -> Platform.runLater(() -> onServerStarted(state, this::startStudio)));
	}

	@Override
	public void stopBoth() {
		stopStudio(state -> {
			Platform.runLater(() -> {
				onStudioStopped(state);
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
		ConcurrentUtil.run(() -> startServer(dsSetting, logWriter, callback));
	}

	public void stopStudio(final Consumer<CLIState> callback) {
		entry.setStudioButtonDisable(true);
		if (entry.isStudioButtonSelected()) {
			entry.setStudioButtonSelected(false);
		}
		final DSSetting dsSetting = entry.getDsSetting();
		ConcurrentUtil.run(() -> stopStudio(dsSetting, callback));
	}

	public abstract void startServer(DSSetting dsSetting, DSLogWriter writer, Consumer<CLIState> callback)
			throws IOException;

	public abstract void stopServer(DSSetting dsSetting, Consumer<CLIState> callback) throws IOException;

	public abstract void startStudio(DSSetting dsSetting, DSLogWriter writer, Consumer<CLIState> callback)
			throws IOException;

	public abstract void stopStudio(DSSetting dsSetting, Consumer<CLIState> callback) throws IOException;

	protected void onServerStarted(final CLIState state) {
		onServerStarted(state, null);
	}

	protected void onServerStarted(final CLIState state, final Runnable andThen) {
		if (state == null || state.isFailure()) {
			Platform.runLater(() -> {
				entry.setServerButtonDisable(false);
				entry.setServerButtonSelected(false);
			});
		}
		ConcurrentUtil.run(() -> {
			while (true) {
				if (!service.isServerRunning(entry.getDsSetting())) {
					ConcurrentUtil.sleepSilently(1000);
					continue;
				}
				Platform.runLater(() -> {
					entry.setServerButtonDisable(false);
					entry.setServerButtonSelected(state.isSuccess());
					if (andThen != null) {
						andThen.run();
					}
				});
				break;
			}
		});
	}

	protected void onServerStopped(final CLIState state) {
		onServerStopped(state, null);
	}

	protected void onServerStopped(final CLIState state, final Runnable andThen) {
		if (state == null || state.isFailure()) {
			Platform.runLater(() -> {
				entry.setServerButtonDisable(false);
				entry.setServerButtonSelected(true);
			});
		}
		ConcurrentUtil.run(() -> {
			while (true) {
				if (service.isServerRunning(entry.getDsSetting())) {
					ConcurrentUtil.sleepSilently(1000);
					continue;
				}
				Platform.runLater(() -> {
					entry.setServerButtonDisable(false);
					entry.setServerButtonSelected(!state.isSuccess());
					if (andThen != null) {
						andThen.run();
					}
				});
				break;
			}
		});
	}

	protected void onStudioStarted(final CLIState state) {
		Platform.runLater(() -> entry.setStudioButtonDisable(false));
		final boolean selected = state == null || !state.isSuccess();
		Platform.runLater(() -> entry.setStudioButtonSelected(selected));
	}

	protected void onStudioStopped(final CLIState state) {
		Platform.runLater(() -> entry.setStudioButtonDisable(false));
		final boolean selected = state != null && !state.isSuccess();
		Platform.runLater(() -> entry.setStudioButtonSelected(selected));
	}
}
