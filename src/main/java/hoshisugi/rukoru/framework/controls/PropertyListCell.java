package hoshisugi.rukoru.framework.controls;

import java.util.function.Function;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class PropertyListCell<T> extends ListCell<T> {

	private final Function<T, String> textProvider;

	public static <T> Callback<ListView<T>, ListCell<T>> forListView(final Function<T, String> textProvider) {
		return list -> new PropertyListCell<>(textProvider);
	}

	private PropertyListCell(final Function<T, String> textProvider) {
		this.textProvider = textProvider;
	}

	@Override
	protected void updateItem(final T item, final boolean empty) {
		super.updateItem(item, empty);
		if (!empty && item != null) {
			setText(textProvider.apply(item));
		} else {
			setText("");
		}
	}

}
