package hoshisugi.rukoru.app.services.ds;

import java.io.IOException;
import java.util.function.Consumer;

import hoshisugi.rukoru.app.models.ds.DSLogWriter;
import hoshisugi.rukoru.app.models.ds.DSSetting;
import hoshisugi.rukoru.framework.cli.CLIState;

public interface DSService {

	void startServerWithExe(DSSetting dsSetting, DSLogWriter writer, Consumer<CLIState> callback) throws IOException;

	void stopServerWithExe(DSSetting dsSetting, Consumer<CLIState> callback) throws InterruptedException;

	void startStudioWithExe(DSSetting dsSetting, DSLogWriter writer, Consumer<CLIState> callback) throws IOException;

	void stopStudioWithExe(DSSetting dsSetting, Consumer<CLIState> callback) throws IOException;

	void startServerWithService(DSSetting dsSetting, DSLogWriter writer, Consumer<CLIState> callback)
			throws IOException;

	void stopServerWithService(DSSetting dsSetting, Consumer<CLIState> callback) throws IOException;

}
