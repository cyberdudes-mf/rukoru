package hoshisugi.rukoru.app.view.content;

import java.net.URL;
import java.util.ResourceBundle;

import hoshisugi.rukoru.app.models.S3Item;
import hoshisugi.rukoru.flamework.controls.BaseController;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class S3ExplorerController extends BaseController {

	private final ObjectProperty<S3Item> selectedItem = new SimpleObjectProperty<>(this, "selectedItem",
			new S3Item("Amazon S3"));

	@Override
	public void initialize(final URL url, final ResourceBundle resource) {
		// TODO Auto-generated method stub
	}

	public S3Item getSelectedItem() {
		return selectedItem.get();
	}

	public void setSelectedItem(final S3Item item) {
		selectedItem.set(item);
	}

	public ObjectProperty<S3Item> selectedItemProperty() {
		return selectedItem;
	}
}
