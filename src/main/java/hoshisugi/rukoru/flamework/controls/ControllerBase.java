package hoshisugi.rukoru.flamework.controls;

import hoshisugi.rukoru.flamework.inject.Injectable;
import hoshisugi.rukoru.flamework.inject.Injector;
import javafx.fxml.Initializable;

public abstract class ControllerBase implements Initializable, Injectable {

	public ControllerBase() {
		Injector.regist(this);
		Injector.injectMembers(this);
	}

}
