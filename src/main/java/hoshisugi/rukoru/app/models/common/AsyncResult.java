package hoshisugi.rukoru.app.models.common;

import static hoshisugi.rukoru.app.models.common.AsyncResult.Status.Doing;
import static hoshisugi.rukoru.app.models.common.AsyncResult.Status.Done;
import static hoshisugi.rukoru.app.models.common.AsyncResult.Status.Error;
import static hoshisugi.rukoru.app.models.common.AsyncResult.Status.Ready;
import static javafx.beans.binding.DoubleExpression.doubleExpression;

import java.util.concurrent.CountDownLatch;

import hoshisugi.rukoru.framework.util.ConcurrentUtil;
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
		Ready, Doing, Done, Error;
	}

	private final ReadOnlyDoubleWrapper progress = new ReadOnlyDoubleWrapper(this, "progress");

	private final LongProperty total = new SimpleLongProperty(this, "total");

	private final LongProperty current = new SimpleLongProperty(this, "current");

	private final StringProperty name = new SimpleStringProperty(this, "name");

	private Throwable thrown;

	private Status status = Ready;

	private final CountDownLatch latch = new CountDownLatch(1);

	public void addCurrent(final int current) {
		if (status != Doing) {
			setStatus(Doing);
		}
		this.current.set(this.current.get() + current);
		if (progress.getValue() == 1.0) {
			setStatus(Done);
		}
	}

	public long getCurrent() {
		return current.get();
	}

	public double getProgress() {
		return progress.get();
	}

	public long getTotal() {
		return total.get();
	}

	public void setTotal(final long total) {
		this.total.set(total);
		if (total != 0) {
			progress.bind(Bindings.divide(doubleExpression(current), doubleExpression(this.total)));
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
		setStatus(Error);
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(final Status status) {
		this.status = status;
		if (status == Done || status == Error) {
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

	public void callback(final Runnable runnable) {
		ConcurrentUtil.run(() -> {
			waitFor();
			Platform.runLater(runnable);
		});
	}
}
