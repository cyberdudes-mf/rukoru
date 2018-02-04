package hoshisugi.rukoru.framework.controls;

import java.util.function.Function;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

public class GraphicTableCell<S, T> extends TableCell<S, T> {

	private final Function<T, ? extends Node> nodeProvider;

	public static <S> Callback<CellDataFeatures<S, S>, ObservableValue<S>> forTableCellValueFactory() {
		return param -> new ReadOnlyObjectWrapper<>(param.getValue());
	}

	public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableCellFactory(
			final Function<T, ? extends Node> nodeProvider) {
		return item -> new GraphicTableCell<>(nodeProvider);
	}

	public GraphicTableCell(final Function<T, ? extends Node> nodeProvider) {
		this.nodeProvider = nodeProvider;
	}

	@Override
	protected void updateItem(final T item, final boolean empty) {
		super.updateItem(item, empty);
		if (!empty && item != null) {
			final Node node = nodeProvider.apply(item);
			setGraphic(node);
		} else {
			setGraphic(null);
		}
	}

}
