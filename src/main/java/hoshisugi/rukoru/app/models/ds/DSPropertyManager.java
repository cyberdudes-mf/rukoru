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
import java.util.stream.Collectors;

public class DSPropertyManager {
	private final List<String> statement = new ArrayList<>();
	private Path path;

	public void load(final Path path) throws IOException {
		reflesh();
		this.path = path;
		try (BufferedReader br = Files.newBufferedReader(path)) {
			statement.addAll(br.lines().collect(Collectors.toList()));
		}
		statement.stream().forEach(System.out::println);
	}

	public void replace(final String target, final String newValue) {
		statement.set(statement.indexOf(target), newValue);
		statement.stream().forEach(System.out::println);
	}

	public void reflesh() {
		statement.clear();
		statement.stream().forEach(System.out::println);
	}

	public void write() throws IOException {
		try (final BufferedWriter bw = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(path.toFile())))) {
			bw.write(generate());
		}
		statement.stream().forEach(System.out::println);
	}

	public String generate() {
		final StringBuilder builder = new StringBuilder();
		statement.stream().forEach(s -> builder.append(s + System.lineSeparator()));
		System.out.println(builder.toString());
		return builder.toString();
	}

	public List<String> generateProperties() {
		return statement.stream().filter(s -> s.matches("(#)?[\\w.]*=[\\w\'#/:,-. ${}]*")).collect(Collectors.toList());
	}

}
