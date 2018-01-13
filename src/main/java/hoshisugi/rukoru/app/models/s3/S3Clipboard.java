package hoshisugi.rukoru.app.models.s3;

import static hoshisugi.rukoru.app.models.s3.S3Clipboard.Type.Copy;
import static hoshisugi.rukoru.app.models.s3.S3Clipboard.Type.Cut;

import java.io.Serializable;
import java.util.Optional;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;

public class S3Clipboard {
	enum Type {
		Cut, Copy;
	}

	private static final DataFormat DATA_FORMAT = new DataFormat("application/x-s3object");

	public static void copy(final S3Item item) {
		cutOrCopy(Copy, item);
	}

	public static void cut(final S3Item item) {
		cutOrCopy(Cut, item);
	}

	public static void clear() {
		Clipboard.getSystemClipboard().clear();
	}

	public static void cutOrCopy(final S3Clipboard.Type type, final S3Item item) {
		final ClipboardContent content = new ClipboardContent();
		content.put(DATA_FORMAT, new S3ClipboardContent(item, type));
		Clipboard.getSystemClipboard().setContent(content);
	}

	public static boolean canPaste() {
		final Clipboard clipboard = Clipboard.getSystemClipboard();
		return clipboard.hasContent(DATA_FORMAT);
	}

	public static Optional<S3ClipboardContent> getContent() {
		final Clipboard clipboard = Clipboard.getSystemClipboard();
		if (clipboard.hasContent(DATA_FORMAT)) {
			final S3ClipboardContent content = (S3ClipboardContent) clipboard.getContent(DATA_FORMAT);
			return Optional.of(content);
		}
		return Optional.empty();
	}

	public static class S3ClipboardContent implements Serializable {

		private final String bucketName;
		private final String key;
		private final String name;
		private final Type type;

		public S3ClipboardContent(final S3Item item, final Type type) {
			super();
			this.bucketName = item.getBucketName();
			this.key = item.getKey();
			this.name = item.getKey().replace(item.getParentKey(), "");
			this.type = type;
		}

		public String getBucketName() {
			return bucketName;
		}

		public String getKey() {
			return key;
		}

		public String getName() {
			return name;
		}

		public boolean isCut() {
			return type == Cut;
		}

		public boolean isCopied() {
			return type == Copy;
		}

	}

}
