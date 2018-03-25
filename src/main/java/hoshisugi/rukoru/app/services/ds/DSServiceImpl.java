package hoshisugi.rukoru.app.services.ds;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.function.Consumer;

import hoshisugi.rukoru.app.models.ds.DSLogWriter;
import hoshisugi.rukoru.app.models.ds.DSSetting;
import hoshisugi.rukoru.framework.base.BaseService;
import hoshisugi.rukoru.framework.cli.CLI;
import hoshisugi.rukoru.framework.cli.CLIState;

public class DSServiceImpl extends BaseService implements DSService {

	@Override
	public void startServerWithExe(final DSSetting dsSetting, final DSLogWriter writer,
			final Consumer<CLIState> callback) throws IOException {
		final CLIState cliState = CLI.command("DataSpiderServer.exe")
				.directory(Paths.get(dsSetting.getExecutionPath() + "/server/bin/"))
				.successCondition(s -> s.contains("正常に起動しました。")).failureCondition(s -> s.contains("起動に失敗しました。"))
				.execute();
		try (final BufferedReader br = new BufferedReader(new InputStreamReader(cliState.getInputStream()))) {
			for (String line = null; (line = br.readLine()) != null;) {
				writer.writeLine(line);
				if (cliState.isSuccess() || cliState.isFailure()) {
					callback.accept(cliState);
				}
			}
			writer.shutDown();
		}
		if (cliState.isFailure()) {
			callback.accept(cliState);
		}
	}

	@Override
	public void stopServerWithExe(final DSSetting dsSetting, final Consumer<CLIState> callback)
			throws InterruptedException {
		final CLIState cliState = CLI.command("Shutdown.exe")
				.directory(Paths.get(dsSetting.getExecutionPath() + "/server/bin/"))
				.successCondition(s -> s.contains("停止しました。")).execute();
		cliState.waitFor();
		callback.accept(cliState);
	}

}
