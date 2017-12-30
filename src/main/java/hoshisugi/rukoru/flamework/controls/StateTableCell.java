package hoshisugi.rukoru.flamework.controls;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class StateTableCell<S> extends TableCell<S, String> {

	public static <S> Callback<TableColumn<S, String>, TableCell<S, String>> forTableCellFactory() {
		return item -> new StateTableCell<>();
	}

	@Override
	protected void updateItem(final String state, final boolean empty) {
		super.updateItem(state, empty);
		getStyleClass().clear();
		if (!empty) {
			setText(state);
			switch (state) {
			case "running":
				getStyleClass().add("running");
				break;
			case "stopped":
				getStyleClass().add("stopped");
				break;
			case "pending":
			case "stopping":
				getStyleClass().add("pending");
				break;
			}
		} else {
			setText("");
		}
	}

}
