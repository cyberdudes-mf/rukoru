package hoshisugi.rukoru.app.models.ds;

import java.io.IOException;
import java.util.function.Consumer;

import hoshisugi.rukoru.framework.cli.CLIState;

class DSBatManager extends DSManagerBase {

	DSBatManager(final DSEntry entry) {
		super(entry);
	}

	@Override
	void startServer(final DSSetting dsSetting, final DSLogWriter writer, final Consumer<CLIState> callback)
			throws IOException {
		service.startServerBat(dsSetting, writer, callback);
	}

	@Override
	void stopServer(final DSSetting dsSetting, final Consumer<CLIState> callback) throws IOException {
		service.stopServerBat(dsSetting, callback);
	}

	@Override
	void startStudioForDesktop(final DSSetting dsSetting, final DSLogWriter writer,
			final Consumer<CLIState> callback) throws IOException {
		service.startStudioBat(dsSetting, writer, callback);
	}

	@Override
	void stopStudioForDesktop(final DSSetting dsSetting, final Consumer<CLIState> callback) throws IOException {
		service.stopStudioBat(dsSetting, callback);
	}

}
