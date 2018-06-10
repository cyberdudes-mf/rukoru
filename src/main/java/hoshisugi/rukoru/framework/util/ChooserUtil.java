package hoshisugi.rukoru.framework.util;

import static hoshisugi.rukoru.app.enums.Preferences.DirectorySelection;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import hoshisugi.rukoru.app.models.settings.Preference;
import hoshisugi.rukoru.app.services.settings.LocalSettingService;
import hoshisugi.rukoru.framework.inject.Injector;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

public class ChooserUtil {

	public static final ExtensionFilter TXT = new ExtensionFilter("Text Files", "*.txt");
	public static final ExtensionFilter IMAGE = new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif");
	public static final ExtensionFilter HTML = new ExtensionFilter("HTML Files", "*.html", "*.htm");
	public static final ExtensionFilter ARCHIVE = new ExtensionFilter("Archive Files", "*.zip", "*.gz", "*.tar");
	public static final ExtensionFilter XLSX = new ExtensionFilter("Excel Files", "*.xlsx");
	public static final ExtensionFilter ALL = new ExtensionFilter("All Files", "*.*");

	public static Optional<Path> showSaveDialog(final Window window) {
		return ChooserBuilder.create().showSaveDialog(window);
	}

	public static Optional<Path> showOpenDialog(final Window window) {
		return ChooserBuilder.create().showOpenDialog(window);
	}

	public static Optional<Path> showDirectoryDialog(final Window window) {
		return ChooserBuilder.create().showDirectoryDialog(window);
	}

	public static ChooserBuilder chooser() {
		return new ChooserBuilder();
	}

	public static class ChooserBuilder {
		private String title;
		private String initialFileName;
		private final List<ExtensionFilter> extensions = new ArrayList<>();
		private File initialDirectory;
		private final LocalSettingService service;

		private final Preference selection;

		private ChooserBuilder() {
			service = Injector.getInstance(LocalSettingService.class);
			selection = getSavedSelection().orElseGet(() -> new Preference(DirectorySelection));
		}

		public static ChooserBuilder create() {
			return new ChooserBuilder();
		}

		public ChooserBuilder title(final String title) {
			this.title = title;
			return this;
		}

		public ChooserBuilder initialFileName(final String initialFileName) {
			this.initialFileName = initialFileName;
			return this;
		}

		public ChooserBuilder extensions(final ExtensionFilter... extensions) {
			this.extensions.addAll(Arrays.asList(extensions));
			return this;
		}

		public ChooserBuilder extension(final String description, final String... extensions) {
			this.extensions.add(new ExtensionFilter(description, extensions));
			return this;
		}

		public ChooserBuilder initialDirectory(final Path path) {
			this.initialDirectory = path.toFile();
			return this;
		}

		public Optional<Path> showSaveDialog(final Window window) {
			return showFileChooser(c -> c.showSaveDialog(window));
		}

		public Optional<Path> showOpenDialog(final Window window) {
			return showFileChooser(c -> c.showOpenDialog(window));

		}

		public Optional<Path> showDirectoryDialog(final Window window) {
			final DirectoryChooser chooser = new DirectoryChooser();
			if (title != null) {
				chooser.setTitle(title);
			}
			if (initialDirectory != null) {
				chooser.setInitialDirectory(initialDirectory);
			} else {
				getDirectory().ifPresent(chooser::setInitialDirectory);
			}
			final File selection = chooser.showDialog(window);
			return toOptional(selection);
		}

		private Optional<Path> showFileChooser(final Function<FileChooser, File> supplier) {
			final FileChooser chooser = new FileChooser();
			if (title != null) {
				chooser.setTitle(title);
			}
			if (initialFileName != null) {
				chooser.setInitialFileName(initialFileName);
			}
			if (initialDirectory != null) {
				chooser.setInitialDirectory(initialDirectory);
			} else {
				getDirectory().ifPresent(chooser::setInitialDirectory);
			}
			if (!extensions.isEmpty()) {
				chooser.getExtensionFilters().addAll(extensions);
			}
			final File selection = supplier.apply(chooser);
			saveSelection(selection);
			return toOptional(selection);
		}

		private Optional<Path> toOptional(final File selection) {
			if (selection != null) {
				return Optional.of(selection.toPath());
			}
			return Optional.empty();
		}

		private Optional<File> getDirectory() {
			final String value = selection.getValue();
			if (value == null) {
				return Optional.empty();
			}
			return Optional.of(Paths.get(value).toFile());
		}

		private Optional<Preference> getSavedSelection() {
			try {
				return service.findPreference(DirectorySelection);
			} catch (final SQLException e) {
				return Optional.empty();
			}
		}

		private void saveSelection(final File selection) {
			if (selection == null) {
				return;
			}
			final String parent = selection.toPath().getParent().toString();
			this.selection.setValue(parent);
			try {
				service.savePreference(this.selection);
			} catch (final SQLException e) {
			}
		}

	}
}
