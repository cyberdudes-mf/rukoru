package hoshisugi.rukoru.flamework.controls;

import java.util.function.Function;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

public class GraphicTableCell<S> extends TableCell<S, S> {

	private final Function<S, ? extends Node> nodeProvider;

	public static <S> Callback<CellDataFeatures<S, S>, ObservableValue<S>> forTableCellValueFactory() {
		return param -> new ReadOnlyObjectWrapper<>(param.getValue());
	}

	public static <S> Callback<TableColumn<S, S>, TableCell<S, S>> forTableCellFactory(
			final Function<S, ? extends Node> nodeProvider) {
		return item -> new GraphicTableCell<>(nodeProvider);
	}

	public GraphicTableCell(final Function<S, ? extends Node> nodeProvider) {
		this.nodeProvider = nodeProvider;
	}

	@Override
	protected void updateItem(final S item, final boolean empty) {
		super.updateItem(item, empty);
		if (!empty && item != null) {
			final Node node = nodeProvider.apply(item);
			setGraphic(node);
		} else {
			setGraphic(null);
		}
	}

}
