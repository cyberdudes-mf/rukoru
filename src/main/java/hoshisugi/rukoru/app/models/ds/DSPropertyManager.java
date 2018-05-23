package hoshisugi.rukoru.app.models.ds;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DSPropertyManager {
	private final List<String> statement = new ArrayList<>();

	public void load(final Path path) throws IOException {
		reflesh();
		try (BufferedReader br = Files.newBufferedReader(path)) {
			statement.addAll(br.lines().collect(Collectors.toList()));
		}
	}

	public void replace(final String target, final String newValue) {
		statement.set(statement.indexOf(target), newValue);
	}

	public void reflesh() {
		statement.clear();
	}

	public String generate() {
		final StringBuilder builder = new StringBuilder();
		statement.stream().forEach(s -> builder.append(s + System.lineSeparator()));
		return builder.toString();
	}

}
