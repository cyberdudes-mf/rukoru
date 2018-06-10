package hoshisugi.rukoru.app.models.common;

import java.io.InputStream;

public class ContentInfo {

	private final long contentLength;
	private final InputStream inputStream;

	public ContentInfo(final long contentLength, final InputStream inputStream) {
		super();
		this.contentLength = contentLength;
		this.inputStream = inputStream;
	}

	public long getContentLength() {
		return contentLength;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

}
