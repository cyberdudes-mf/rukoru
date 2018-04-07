package hoshisugi.rukoru.app.services.ds;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
	public void startServerExe(final DSSetting dsSetting, final DSLogWriter writer, final Consumer<CLIState> callback)
			throws IOException {
		final CLIState cliState = CLI.command(dsSetting.getServerExecutorName())
				.directory(Paths.get(dsSetting.getExecutionPath()).resolve("server/bin"))
				.successCondition(s -> s.contains("正常に起動しました。")).execute();
		try (final BufferedReader br = new BufferedReader(new InputStreamReader(cliState.getInputStream()))) {
			for (String line = null; (line = br.readLine()) != null;) {
				writer.writeLine(line);
				if (line.contains("起動に失敗しました。")) {
					cliState.fail();
				}
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
	public void stopServerExe(final DSSetting dsSetting, final Consumer<CLIState> callback)
			throws InterruptedException {
		final CLIState cliState = CLI.command("Shutdown.exe")
				.directory(Paths.get(dsSetting.getExecutionPath() + "/server/bin/"))
				.successCondition(s -> s.contains("停止しました。")).execute();
		cliState.waitFor();
		callback.accept(cliState);
	}

	@Override
	public void startStudioExe(final DSSetting dsSetting, final DSLogWriter writer, final Consumer<CLIState> callback)
			throws IOException {
		final CLIState cliState = CLI.command(dsSetting.getStudioExecutorName())
				.directory(Paths.get(dsSetting.getExecutionPath()).resolve("client/bin")).execute();
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
	public void stopStudioExe(final DSSetting dsSetting, final Consumer<CLIState> callback) throws IOException {
		final Optional<WindowsProcess> process = getDataSpiderStudioProcess(dsSetting);
		if (process.isPresent()) {
			final CLIState state = CLI.command("taskkill").options("/pid", process.get().getProcessId(), "/t", "/f")
					.execute();
			callback.accept(state);
		}
	}

	@Override
	public void startServerService(final DSSetting dsSetting, final DSLogWriter writer,
			final Consumer<CLIState> callback) throws IOException {
		if (dsSetting.getServiceName().isPresent()) {
			final CLIState cliState = CLI.command("sc").options("start", dsSetting.getServiceName().get())
					.successCondition(s -> s.contains("RUNNING")).execute();
			try (BufferedReader br = new BufferedReader(new InputStreamReader(cliState.getInputStream()))) {
				for (String line = null; (line = br.readLine()) != null;) {
					writer.writeLine(line);
				}
				callback.accept(cliState);
			} finally {
				writer.shutDown();
			}
		} else {
			final CLIState cliState = new CLIState(null);
			cliState.fail();
			callback.accept(cliState);
			writer.writeLine("サービスは登録されていません。");
		}
		writer.shutDown();
	}

	@Override
	public void stopServerService(final DSSetting dsSetting, final Consumer<CLIState> callback) throws IOException {
		if (dsSetting.getServiceName().isPresent()) {
			final CLIState cliState = CLI.command("sc").options("stop", dsSetting.getServiceName().get())
					.successCondition(s -> s.contains("STOPPED")).execute();
			callback.accept(cliState);
		}
	}

	@Override
	public void changePort(final DSSetting setting, final String port) throws IOException {
		final Path path = Paths.get(setting.getExecutionPath()).resolve("server/system/conf/webcontainer.properties");
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE)) {
			writer.write("port=" + port);
		}
	}

	private Optional<WindowsProcess> getDataSpiderStudioProcess(final DSSetting dsSetting) throws IOException {
		final CLIState wmicState = CLI.command("WMIC")
				.options("PROCESS", "WHERE", "\"Name LIKE '" + dsSetting.getStudioExecutorName() + "'\"", "GET",
						"ExecutablePath,Name,ProcessId", "/FORMAT:CSV")
				.execute();
		try (BufferedReader reader = IOUtil.newBufferedReader(wmicState.getInputStream())) {
			for (String line = null; (line = reader.readLine()) != null;) {
				if (line.isEmpty()) {
					continue;
				}
				final WindowsProcess process = new WindowsProcess(line);
				if (process.executablePath.startsWith(dsSetting.getExecutionPath())) {
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
