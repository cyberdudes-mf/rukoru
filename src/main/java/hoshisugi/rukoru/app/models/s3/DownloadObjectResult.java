package hoshisugi.rukoru.app.models.s3;

import static javafx.beans.binding.DoubleExpression.doubleExpression;

import javafx.beans.binding.Bindings;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.SimpleLongProperty;

public class DownloadObjectResult {

	public enum Status {
		Ready, Downloading, Done;
	}

	private final ReadOnlyDoubleWrapper progress = new ReadOnlyDoubleWrapper(this, "progress");

	private final LongProperty contentLength = new SimpleLongProperty(this, "contentLength");

	private final LongProperty wrote = new SimpleLongProperty(this, "wrote");

	private Exception thrown;

	private Status status = Status.Ready;

	public void addWrote(final int wrote) {
		this.wrote.set(this.wrote.get() + wrote);
	}

	public double getProgress() {
		return progress.get();
	}

	public long getContentLength() {
		return contentLength.get();
	}

	public void setContentLength(final long contentLength) {
		this.contentLength.set(contentLength);
		if (contentLength != 0) {
			progress.bind(Bindings.divide(doubleExpression(wrote), doubleExpression(this.contentLength)));
		}
	}

	public long getWrote() {
		return wrote.get();
	}

	public ReadOnlyDoubleProperty progressProperty() {
		return progress.getReadOnlyProperty();
	}

	public Exception getThrown() {
		return thrown;
	}

	public void setThrown(final Exception thrown) {
		this.thrown = thrown;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(final Status status) {
		this.status = status;
	}

}
