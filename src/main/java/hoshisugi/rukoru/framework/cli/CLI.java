package hoshisugi.rukoru.framework.cli;

public class CLI {

	public static CLIBuilder command(final String command) {
		return new CLIBuilder(command);
	}
}
