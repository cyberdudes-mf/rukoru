package hoshisugi.rukoru.app.models.ds;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DSPropertyManager {
	private static final Pattern PATTERN = Pattern.compile("(?<enable>#)?(?<key>\\S+)=(?<value>\\S*)");

	private final List<String> statement = new ArrayList<>();
	private Path path;

	public List<DSProperty> load(final Path path) throws IOException {
		statement.clear();
		this.path = path;
		try (BufferedReader br = Files.newBufferedReader(path)) {
			statement.addAll(br.lines().collect(Collectors.toList()));
		}
		return statement.stream().map(PATTERN::matcher).filter(Matcher::matches).map(m -> {
			final String enable = m.group("enable");
			final String key = m.group("key");
			final String value = m.group("value");
			return new DSProperty(enable, key, value, this);
		}).collect(Collectors.toList());
	}

	public DSProperty addProperty(final String enable, final String key, final String value) {
		statement.add(enable + key + "=" + value);
		return new DSProperty(enable, key, value, this);
	}

	public void deleteProperty(final String value) {
		statement.remove(value);
	}

	public void replace(final String target, final String newValue) {
		statement.set(statement.indexOf(target), newValue);
	}

	public void save() throws IOException {
		try (final BufferedWriter bw = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(path.toFile())))) {
			bw.write(generate());
		}
	}

	public String generate() {
		final StringBuilder builder = new StringBuilder();
		statement.stream().forEach(s -> builder.append(s + System.lineSeparator()));
		return builder.toString();
	}

}
