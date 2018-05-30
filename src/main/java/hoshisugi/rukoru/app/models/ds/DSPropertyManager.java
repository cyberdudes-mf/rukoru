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

	private final List<String> tmp = new ArrayList<>();
	private final Path path;

	public DSPropertyManager(final Path path) throws IOException {
		tmp.clear();
		this.path = path;
		try (BufferedReader br = Files.newBufferedReader(path)) {
			tmp.addAll(br.lines().collect(Collectors.toList()));
		}
	}

	public List<DSProperty> load() throws IOException {
		return tmp.stream().map(PATTERN::matcher).filter(Matcher::matches).map(m -> {
			final String enable = m.group("enable");
			final String key = m.group("key");
			final String value = m.group("value");
			return new DSProperty(enable, key, value, this);
		}).collect(Collectors.toList());
	}

	public DSProperty addProperty(final String enable, final String key, final String value) {
		tmp.add(enable + key + "=" + value);
		return new DSProperty(enable, key, value, this);
	}

	public void deleteProperty(final String article) {
		tmp.remove(article);
	}

	public void replace(final String oldArticle, final String newArticle) {
		tmp.set(tmp.indexOf(oldArticle), newArticle);
	}

	public void save() throws IOException {
		try (final BufferedWriter bw = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(path.toFile())))) {
			for (final String line : tmp) {
				bw.write(line + System.lineSeparator());
			}
		}
	}

}
