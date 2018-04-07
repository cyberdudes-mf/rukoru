package hoshisugi.rukoru.app.services.ds;

import java.io.IOException;
import java.util.function.Consumer;

import hoshisugi.rukoru.app.models.ds.DSLogWriter;
import hoshisugi.rukoru.app.models.ds.DSSetting;
import hoshisugi.rukoru.framework.cli.CLIState;

public interface DSService {

	void startServerExe(DSSetting dsSetting, DSLogWriter writer, Consumer<CLIState> callback) throws IOException;

	void stopServerExe(DSSetting dsSetting, Consumer<CLIState> callback) throws InterruptedException;

	void startStudioExe(DSSetting dsSetting, DSLogWriter writer, Consumer<CLIState> callback) throws IOException;

	void stopStudioExe(DSSetting dsSetting, Consumer<CLIState> callback) throws IOException;

	void startServerService(DSSetting dsSetting, DSLogWriter writer, Consumer<CLIState> callback) throws IOException;

	void stopServerService(DSSetting dsSetting, Consumer<CLIState> callback) throws IOException;

	void changePort(DSSetting setting, String port) throws IOException;
}
