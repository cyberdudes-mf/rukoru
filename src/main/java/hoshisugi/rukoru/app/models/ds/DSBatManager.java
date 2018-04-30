package hoshisugi.rukoru.app.models.ds;

import java.io.IOException;
import java.util.function.Consumer;

import hoshisugi.rukoru.framework.cli.CLIState;

public class DSBatManager extends DSManagerBase {

	public DSBatManager(final DSEntry entry) {
		super(entry);
	}

	@Override
	public void startServer(final DSSetting dsSetting, final DSLogWriter writer, final Consumer<CLIState> callback)
			throws IOException {
		service.startServerBat(dsSetting, writer, callback);
	}

	@Override
	public void stopServer(final DSSetting dsSetting, final Consumer<CLIState> callback) throws IOException {
		service.stopServerBat(dsSetting, callback);
	}

	@Override
	public void startStudio(final DSSetting dsSetting, final DSLogWriter writer, final Consumer<CLIState> callback)
			throws IOException {
		service.startStudioBat(dsSetting, writer, callback);
	}

	@Override
	public void stopStudio(final DSSetting dsSetting, final Consumer<CLIState> callback) throws IOException {
		service.stopStudioBat(dsSetting, callback);
	}

}
