package hoshisugi.rukoru.app.models.s3;

import java.util.ArrayList;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ExplorerSelection {

	private final ObservableList<S3Item> history;
	private final ReadOnlyObjectWrapper<S3Item> selectedItem = new ReadOnlyObjectWrapper<>(this, "selectedItem");
	private final ReadOnlyIntegerWrapper selectedIndex = new ReadOnlyIntegerWrapper(this, "selectedIndex", -1);
	private final ReadOnlyBooleanWrapper hasPrevious = new ReadOnlyBooleanWrapper(this, "hasPrevious");
	private final ReadOnlyBooleanWrapper hasNext = new ReadOnlyBooleanWrapper(this, "hasNext");

	public ExplorerSelection(final int limit) {
		history = FXCollections.observableList(new ArrayList<S3Item>(limit) {
			@Override
			public void add(final int index, final S3Item item) {
				while (index < size()) {
					remove(index);
				}
				super.add(index, item);
				if (index == limit) {
					remove(0);
				}
				selectedIndex.set(size() - 1);
			}
		});
		hasPrevious.bind(Bindings.greaterThan(selectedIndex, 0));
		hasNext.bind(Bindings.lessThan(selectedIndex, Bindings.size(history).subtract(1)));
	}

	public S3Item getSelectedItem() {
		return selectedItem.get();
	}

	public void select(final S3Item item) {
		if (getSelectedItem() != item) {
			history.add(selectedIndex.get() + 1, item);
			selectedItem.set(item);
		}
	}

	public void goPrevious() {
		if (hasPrevious.get()) {
			final int prevIndex = selectedIndex.get() - 1;
			selectedItem.set(history.get(prevIndex));
			selectedIndex.set(prevIndex);
		}
	}

	public void goNext() {
		if (hasNext.get()) {
			final int nextIndex = selectedIndex.get() + 1;
			selectedItem.set(history.get(nextIndex));
			selectedIndex.set(nextIndex);
		}
	}

	public ReadOnlyObjectProperty<S3Item> selectedItemProperty() {
		return selectedItem.getReadOnlyProperty();
	}

	public ReadOnlyBooleanProperty hasNextProperty() {
		return hasNext.getReadOnlyProperty();
	}

	public ReadOnlyBooleanProperty hasPrevious() {
		return hasPrevious.getReadOnlyProperty();
	}
}
