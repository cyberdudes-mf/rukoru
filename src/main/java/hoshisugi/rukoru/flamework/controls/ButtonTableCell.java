package hoshisugi.rukoru.flamework.controls;

import com.google.common.base.Strings;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class ButtonTableCell<S> extends TableCell<S, String> {

	private final EventHandler<ActionEvent> handler;

	public static <S> Callback<TableColumn<S, String>, TableCell<S, String>> forTableCellFactory(
			final EventHandler<ActionEvent> handler) {
		return item -> new ButtonTableCell<>(handler);
	}

	public ButtonTableCell(final EventHandler<ActionEvent> handler) {
		this.handler = handler;
	}

	@Override
	protected void updateItem(final String text, final boolean empty) {
		super.updateItem(text, empty);
		if (!empty) {
			if (!Strings.isNullOrEmpty(text)) {
				final Button button = new Button(text);
				button.setOnAction(handler);
				setGraphic(button);
				button.setPrefWidth(getTableColumn().getWidth());

			}
		}
	}

}
