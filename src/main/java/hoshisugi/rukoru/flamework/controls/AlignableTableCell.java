package hoshisugi.rukoru.flamework.controls;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class AlignableTableCell<S, T> extends TableCell<S, T> {

	public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(final Pos pos) {
		return list -> new AlignableTableCell<>(pos);
	}

	private AlignableTableCell(final Pos pos) {
		setAlignment(pos);
	}

	@Override
	protected void updateItem(final T item, final boolean empty) {
		super.updateItem(item, empty);
		if (empty || item == null) {
			setText(null);
		} else {
			setText(item.toString());
		}
	}

}
