package hoshisugi.rukoru.app.models.ds;

import static hoshisugi.rukoru.app.enums.StudioMode.Desktop;
import static java.lang.String.format;

import java.io.IOException;
import java.util.function.Consumer;

import hoshisugi.rukoru.app.enums.StudioMode;
import hoshisugi.rukoru.app.services.ds.DSService;
import hoshisugi.rukoru.framework.cli.CLIState;
import hoshisugi.rukoru.framework.event.ShutdownHandler;
import hoshisugi.rukoru.framework.inject.Injector;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;
import javafx.application.Platform;

abstract class DSManagerBase implements DSManager {

	protected final DSService service;
	protected final DSEntry entry;

	DSManagerBase(final DSEntry entry) {
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
		ConcurrentUtil.run(() -> startStudioForXxx(dsSetting, logWriter));
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

	abstract void startServer(DSSetting dsSetting, DSLogWriter writer, Consumer<CLIState> callback)
			throws IOException;

	abstract void stopServer(DSSetting dsSetting, Consumer<CLIState> callback) throws IOException;

	abstract void startStudioForDesktop(DSSetting dsSetting, DSLogWriter writer, Consumer<CLIState> callback)
			throws IOException;

	abstract void stopStudioForDesktop(DSSetting dsSetting, Consumer<CLIState> callback) throws IOException;

	void startServer(final Consumer<CLIState> callback) {
		entry.setServerButtonDisable(true);
		if (!entry.isServerButtonSelected()) {
			entry.setServerButtonSelected(true);
		}
		final DSSetting dsSetting = entry.getDsSetting();
		final DSLogWriter logWriter = entry.getServerLogWriter();
		ConcurrentUtil.run(() -> startServer(dsSetting, logWriter, callback));
	}

	void startStudioForXxx(final DSSetting dsSetting, final DSLogWriter logWriter) throws IOException {
		switch (dsSetting.getStudioMode()) {
		case Desktop:
			startStudioForDesktop(dsSetting, logWriter, this::onStudioStarted);
			break;
		case Silverlight:
			startStudioForWeb(dsSetting, logWriter, this::onStudioStarted);
			break;
		case WPF:
			startStudioWPF(dsSetting, logWriter, this::onStudioStarted);
			break;
		}
	}

	void stopStudio(final Consumer<CLIState> callback) {
		entry.setStudioButtonDisable(true);
		if (entry.isStudioButtonSelected()) {
			entry.setStudioButtonSelected(false);
		}
		final DSSetting dsSetting = entry.getDsSetting();
		ConcurrentUtil.run(() -> {
			final StudioMode studioMode = dsSetting.getStudioMode();
			if (studioMode == Desktop) {
				stopStudioForDesktop(dsSetting, callback);
			} else {
				throw new IllegalStateException(format("Studio %s の停止には対応していません。", studioMode));
			}
		});
	}

	void startStudioForWeb(final DSSetting dsSetting, final DSLogWriter writer,
			final Consumer<CLIState> callback) {
		service.startStudioForWeb(dsSetting, writer, callback);
	}

	void startStudioWPF(final DSSetting dsSetting, final DSLogWriter writer, final Consumer<CLIState> callback)
			throws IOException {
		service.startStudioWPF(dsSetting, writer, callback);
	}

	void onServerStarted(final CLIState state) {
		onServerStarted(state, null);
	}

	void onServerStarted(final CLIState state, final Runnable andThen) {
		if (state == null || state.isFailure()) {
			Platform.runLater(() -> {
				entry.setServerButtonDisable(false);
				entry.setServerButtonSelected(false);
			});
			return;
		}
		final DSSetting setting = entry.getDsSetting();
		ConcurrentUtil.run(() -> {
			while (true) {
				if (!service.isServerRunning(setting)) {
					ConcurrentUtil.sleepSilently(1000);
					continue;
				}
				Platform.runLater(() -> {
					entry.setServerButtonDisable(false);
					final boolean success = state.isSuccess();
					entry.setServerButtonSelected(success);
					if (success) {
						ShutdownHandler.addHandler(setting.getServerId(), e -> stopServer());
					}
					if (andThen != null) {
						andThen.run();
					}
				});
				break;
			}
		});
	}

	void onServerStopped(final CLIState state) {
		if (state == null || state.isFailure()) {
			Platform.runLater(() -> {
				entry.setServerButtonDisable(false);
				entry.setServerButtonSelected(true);
			});
			return;
		}
		final DSSetting setting = entry.getDsSetting();
		ConcurrentUtil.run(() -> {
			while (true) {
				if (service.isServerRunning(setting)) {
					ConcurrentUtil.sleepSilently(1000);
					continue;
				}
				Platform.runLater(() -> {
					entry.setServerButtonDisable(false);
					final boolean success = state.isSuccess();
					entry.setServerButtonSelected(!success);
					if (success) {
						ShutdownHandler.removeHandler(setting.getServerId());
					}
				});
				break;
			}
		});
	}

	void onStudioStarted(final CLIState state) {
		Platform.runLater(() -> {
			entry.setStudioButtonDisable(false);
			final boolean success = state != null && state.isRunning();
			entry.setStudioButtonSelected(success);
			if (success) {
				ShutdownHandler.addHandler(entry.getDsSetting().getStudioId(), e -> stopStudio());
			}
		});
	}

	void onStudioStopped(final CLIState state) {
		Platform.runLater(() -> {
			entry.setStudioButtonDisable(false);
			final boolean success = state == null || state.isSuccess();
			entry.setStudioButtonSelected(!success);
			if (success) {
				ShutdownHandler.removeHandler(entry.getDsSetting().getStudioId());
			}
		});
	}

}
