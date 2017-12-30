package hoshisugi.rukoru.flamework.controls;

import java.util.function.Function;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

public class ButtonOperationTableCell<S> extends TableCell<S, S> {

	private final Function<S, StringBinding> labelBindingFunction;
	private final Function<S, BooleanBinding> disableBindingFunction;
	private final EventHandler<ActionEvent> handler;

	public static <S> Callback<CellDataFeatures<S, S>, ObservableValue<S>> forTableCellValueFactory() {
		return param -> new ReadOnlyObjectWrapper<>(param.getValue());
	}

	public static <S> Callback<TableColumn<S, S>, TableCell<S, S>> forTableCellFactory(
			final Function<S, StringBinding> labelBindingFunction,
			final Function<S, BooleanBinding> disableBindingFunction, final EventHandler<ActionEvent> handler) {
		return item -> new ButtonOperationTableCell<>(labelBindingFunction, disableBindingFunction, handler);
	}

	public ButtonOperationTableCell(final Function<S, StringBinding> labelBindingFunction,
			final Function<S, BooleanBinding> disableBindingFunction, final EventHandler<ActionEvent> handler) {
		this.labelBindingFunction = labelBindingFunction;
		this.disableBindingFunction = disableBindingFunction;
		this.handler = handler;
	}

	@Override
	protected void updateItem(final S item, final boolean empty) {
		super.updateItem(item, empty);
		if (!empty && item != null) {
			final Button button = new Button();
			button.textProperty().bind(labelBindingFunction.apply(item));
			button.disableProperty().bind(disableBindingFunction.apply(item));
			button.setOnAction(handler);
			button.setUserData(item);
			setGraphic(button);
		} else {
			setGraphic(null);
		}
	}

}
