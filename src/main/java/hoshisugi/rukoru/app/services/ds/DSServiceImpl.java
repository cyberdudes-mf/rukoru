package hoshisugi.rukoru.app.services.ds;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Consumer;

import hoshisugi.rukoru.app.models.ds.DSLogWriter;
import hoshisugi.rukoru.app.models.ds.DSSetting;
import hoshisugi.rukoru.framework.base.BaseService;
import hoshisugi.rukoru.framework.cli.CLI;
import hoshisugi.rukoru.framework.cli.CLIState;
import hoshisugi.rukoru.framework.util.ConcurrentUtil;
import hoshisugi.rukoru.framework.util.IOUtil;

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

	@Override
	public void startStudioWithExe(final DSSetting dsSetting, final DSLogWriter writer,
			final Consumer<CLIState> callback) throws IOException {
		final CLIState cliState = CLI.command("DataSpiderStudio.exe")
				.directory(Paths.get(dsSetting.getExecutionPath() + "/client/bin/")).execute();
		ConcurrentUtil.run(() -> {
			try (final BufferedReader br = new BufferedReader(new InputStreamReader(cliState.getInputStream()))) {
				for (String line = null; (line = br.readLine()) != null;) {
					writer.writeLine(line);
				}
				writer.shutDown();
			}
		});
		callback.accept(cliState);
	}

	@Override
	public void stopStudioWithExe(final DSSetting dsSetting, final Consumer<CLIState> callback) throws IOException {
		final Optional<WindowsProcess> process = getDataSpiderStudioProcess(dsSetting.getExecutionPath());
		if (process.isPresent()) {
			final CLIState state = CLI.command("taskkill").options("/pid", process.get().getProcessId(), "/t", "/f")
					.execute();
			callback.accept(state);
		}
	}

	private Optional<WindowsProcess> getDataSpiderStudioProcess(final String getExecutionPath) throws IOException {
		final CLIState wmicState = CLI.command("WMIC").options("PROCESS", "WHERE",
				"\"Name LIKE 'DataSpiderStudio.exe'\"", "GET", "ExecutablePath,Name,ProcessId", "/FORMAT:CSV")
				.execute();
		try (BufferedReader reader = IOUtil.newBufferedReader(wmicState.getInputStream())) {
			for (String line = null; (line = reader.readLine()) != null;) {
				if (line.isEmpty()) {
					continue;
				}
				final WindowsProcess process = new WindowsProcess(line);
				if (process.executablePath.startsWith(getExecutionPath)) {
					return Optional.of(process);
				}
			}
		}
		return Optional.empty();
	}

	static class WindowsProcess {
		private final String node;
		private final String executablePath;
		private final String name;
		private final String processId;

		public WindowsProcess(final String line) {
			final String[] cols = line.split(",");
			node = cols[0];
			executablePath = cols[1];
			name = cols[2];
			processId = cols[3];
		}

		public String getNode() {
			return node;
		}

		public String getExecutablePath() {
			return executablePath;
		}

		public String getName() {
			return name;
		}

		public String getProcessId() {
			return processId;
		}

	}
}
