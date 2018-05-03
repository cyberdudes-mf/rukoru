package hoshisugi.rukoru.app.models.ds;

import java.io.IOException;
import java.util.function.Consumer;

import hoshisugi.rukoru.framework.cli.CLIState;

public class DSServiceManager extends DSManagerBase {

	DSServiceManager(final DSEntry entry) {
		super(entry);
	}

	@Override
	void startServer(final DSSetting dsSetting, final DSLogWriter writer, final Consumer<CLIState> callback)
			throws IOException {
		service.startServerService(dsSetting, writer, callback);
	}

	@Override
	void stopServer(final DSSetting dsSetting, final Consumer<CLIState> callback) throws IOException {
		service.stopServerService(dsSetting, callback);
	}

	@Override
	void startStudioForDesktop(final DSSetting dsSetting, final DSLogWriter writer,
			final Consumer<CLIState> callback) throws IOException {
		service.startStudioExe(dsSetting, writer, callback);
	}

	@Override
	void stopStudioForDesktop(final DSSetting dsSetting, final Consumer<CLIState> callback) throws IOException {
		service.stopStudioExe(dsSetting, callback);
	}

}
