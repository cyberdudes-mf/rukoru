package hoshisugi.rukoru.flamework.controls;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.paint.Color;
import javafx.util.Callback;

public class StateTableCell<S> extends TableCell<S, String> {

	public static <S> Callback<TableColumn<S, String>, TableCell<S, String>> forTableCellFactory() {
		return item -> new StateTableCell<>();
	}

	@Override
	protected void updateItem(final String state, final boolean empty) {
		super.updateItem(state, empty);
		if (!empty) {
			setText(state);
			switch (state) {
			case "running":
				setTextFill(Color.GREEN);
				break;
			case "stopped":
				setTextFill(Color.RED);
				break;
			case "pending":
			case "stopping":
			case "shutting-down":
				setTextFill(Color.GOLDENROD);
				break;
			case "terminated":
				setTextFill(Color.PURPLE);
				break;
			}
		} else {
			setText("");
			setTextFill(Color.BLACK);
		}
	}

}
