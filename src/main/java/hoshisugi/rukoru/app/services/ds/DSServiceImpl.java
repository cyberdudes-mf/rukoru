package hoshisugi.rukoru.app.services.ds;

import static org.glassfish.jersey.client.ClientProperties.CONNECT_TIMEOUT;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Consumer;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.apache.http.HttpStatus;
import org.glassfish.jersey.client.ClientConfig;

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
		startServer(dsSetting, writer, callback);
	}

	@Override
	public void stopServerExe(final DSSetting dsSetting, final Consumer<CLIState> callback)
			throws InterruptedException {
		if (isServerRunning(dsSetting)) {
			final CLIState cliState = CLI.command("Shutdown.exe").directory(dsSetting.getPath("server/bin/"))
					.successCondition(s -> s.contains("停止しました。")).execute();
			cliState.waitFor();
			callback.accept(cliState);
		}
	}

	@Override
	public void startStudioExe(final DSSetting dsSetting, final DSLogWriter writer, final Consumer<CLIState> callback)
			throws IOException {
		if (!isStudioRunning(dsSetting)) {
			startStudio(dsSetting, writer, callback);
		}
	}

	@Override
	public void stopStudioExe(final DSSetting dsSetting, final Consumer<CLIState> callback) throws IOException {
		if (isStudioRunning(dsSetting)) {
			final Optional<WindowsProcess> process = getDataSpiderStudioProcess(dsSetting);
			if (process.isPresent()) {
				final CLIState state = CLI.command("taskkill").options("/pid", process.get().getProcessId(), "/t", "/f")
						.execute();
				callback.accept(state);
			}
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
	public void startServerBat(final DSSetting dsSetting, final DSLogWriter writer, final Consumer<CLIState> callback)
			throws IOException {
		startServer(dsSetting, writer, callback);
	}

	@Override
	public void stopServerBat(final DSSetting dsSetting, final Consumer<CLIState> callback)
			throws InterruptedException {
		// TODO Auto-generated method stub

	}

	@Override
	public void startStudioBat(final DSSetting dsSetting, final DSLogWriter writer, final Consumer<CLIState> callback)
			throws IOException {
		startStudio(dsSetting, writer, callback);
	}

	@Override
	public void stopStudioBat(final DSSetting dsSetting, final Consumer<CLIState> callback) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void changePort(final DSSetting setting, final String port) throws IOException {
		final Path serverPropPath = setting.getPath("server/system/conf/webcontainer.properties");
		try (BufferedWriter writer = Files.newBufferedWriter(serverPropPath, StandardOpenOption.CREATE)) {
			writer.write("port=" + port);
		}
		final Path studioPropPath = setting.getPath("client/conf/boot.properties");
		if (Files.exists(studioPropPath)) {
			final Properties studioProp = new Properties();
			try (InputStream input = Files.newInputStream(studioPropPath)) {
				studioProp.load(input);
			}
			studioProp.setProperty("server.port", port);
			try (OutputStream output = Files.newOutputStream(studioPropPath)) {
				studioProp.store(output, "");
			}
		}
	}

	@Override
	public boolean isServerRunning(final DSSetting dsSetting) {
		try {
			final ClientConfig config = new ClientConfig().property(CONNECT_TIMEOUT, 500);
			final Client client = ClientBuilder.newClient(config);
			final WebTarget target = client.target(dsSetting.getServerUrl());
			final Response response = target.request().get();
			return response.getStatus() == HttpStatus.SC_OK;
		} catch (final Exception e) {
			return false;
		}
	}

	@Override
	public boolean isStudioRunning(final DSSetting dsSetting) {
		return Files.exists(dsSetting.getPath("client/bin/.lock"));
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

	private void startServer(final DSSetting dsSetting, final DSLogWriter writer, final Consumer<CLIState> callback)
			throws IOException {
		if (isServerRunning(dsSetting)) {
			return;
		}
		final CLIState cliState = CLI.command(dsSetting.getServerExecutorName())
				.directory(dsSetting.getPath("server/bin")).successCondition(s -> s.contains("正常に起動しました。")).execute();
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

	private void startStudio(final DSSetting dsSetting, final DSLogWriter writer, final Consumer<CLIState> callback)
			throws IOException {
		if (isStudioRunning(dsSetting)) {
			return;
		}
		final CLIState cliState = CLI.command(dsSetting.getStudioExecutorName())
				.directory(dsSetting.getPath("client/bin")).execute();
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
