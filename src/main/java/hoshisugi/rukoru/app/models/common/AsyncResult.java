package hoshisugi.rukoru.app.models.common;

import static javafx.beans.binding.DoubleExpression.doubleExpression;

import java.util.concurrent.CountDownLatch;

import hoshisugi.rukoru.framework.util.DialogUtil;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class AsyncResult {

	public enum Status {
		Ready, Doing, Done;
	}

	private final ReadOnlyDoubleWrapper progress = new ReadOnlyDoubleWrapper(this, "progress");

	private final LongProperty size = new SimpleLongProperty(this, "size");

	private final LongProperty bytes = new SimpleLongProperty(this, "bytes");

	private final StringProperty name = new SimpleStringProperty(this, "name");

	private Throwable thrown;

	private Status status = Status.Ready;

	private final CountDownLatch latch = new CountDownLatch(1);

	public void addBytes(final int bytes) {
		this.bytes.set(this.bytes.get() + bytes);
	}

	public long getBytes() {
		return bytes.get();
	}

	public double getProgress() {
		return progress.get();
	}

	public long getSize() {
		return size.get();
	}

	public void setSize(final long size) {
		this.size.set(size);
		if (size != 0) {
			progress.bind(Bindings.divide(doubleExpression(bytes), doubleExpression(this.size)));
		}
	}

	public String getName() {
		return name.get();
	}

	public void setName(final String name) {
		this.name.set(name);
	}

	public ReadOnlyDoubleProperty progressProperty() {
		return progress.getReadOnlyProperty();
	}

	public Throwable getThrown() {
		return thrown;
	}

	public void setThrown(final Throwable thrown) {
		this.thrown = thrown;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(final Status status) {
		this.status = status;
		if (status == Status.Done) {
			latch.countDown();
		}
	}

	public boolean checkResult() {
		if (thrown != null) {
			Platform.runLater(() -> DialogUtil.showErrorDialog(getThrown()));
		}
		return thrown == null;
	}

	public void waitFor() throws InterruptedException {
		latch.await();
	}
}
