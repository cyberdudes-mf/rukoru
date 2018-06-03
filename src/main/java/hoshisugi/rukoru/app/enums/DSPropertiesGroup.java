package hoshisugi.rukoru.app.enums;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import hoshisugi.rukoru.app.models.ds.DSProperties;
import hoshisugi.rukoru.app.models.ds.DSPropertiesTreeNode;
import hoshisugi.rukoru.app.models.ds.DSSetting;

public enum DSPropertiesGroup implements DSPropertiesTreeNode {

	Server("server/conf/"), Studio("client/conf/");

	private final String path;

	private DSPropertiesGroup(final String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public List<DSProperties> loadFiles(final DSSetting setting) {
		final Path target = setting.getPath(path);
		try (DirectoryStream<Path> ds = Files.newDirectoryStream(target, "*.properties")) {
			return StreamSupport.stream(ds.spliterator(), false).map(DSProperties::new).collect(Collectors.toList());
		} catch (final IOException e) {
			// ディレクトリが存在しない場合はからのリストを返す
			return Collections.emptyList();
		}
	}

}
