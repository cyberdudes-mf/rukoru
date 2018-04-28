package hoshisugi.rukoru.app.models.ds;

import hoshisugi.rukoru.app.services.ds.DSService;
import hoshisugi.rukoru.framework.cli.CLIState;
import hoshisugi.rukoru.framework.inject.Injector;
import javafx.application.Platform;

public abstract class DSManagerBase implements DSManager {

	protected final DSService service;
	protected final DSEntry entry;

	public DSManagerBase(final DSEntry entry) {
		this.entry = entry;
		service = Injector.getInstance(DSService.class);
	}

	protected void onServerStarted(final CLIState state) {
		Platform.runLater(() -> entry.setServerButtonDisable(false));
		if (state != null && state.isSuccess()) {
			// FXUtil.getPrimaryStage().setOnCloseRequest(stopOnExit);
		}
		final boolean succeeded = state == null || state.isSuccess();
		Platform.runLater(() -> entry.setServerButtonSelected(succeeded));
	}

	protected void onServerStopped(final CLIState state) {
		Platform.runLater(() -> entry.setServerButtonDisable(false));
		final boolean selected = state != null && !state.isSuccess();
		Platform.runLater(() -> entry.setServerButtonSelected(selected));
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
