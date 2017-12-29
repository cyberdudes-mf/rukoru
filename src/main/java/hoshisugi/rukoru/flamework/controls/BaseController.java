package hoshisugi.rukoru.flamework.controls;

import hoshisugi.rukoru.flamework.inject.Injectable;
import hoshisugi.rukoru.flamework.inject.Injector;
import javafx.fxml.Initializable;

public abstract class BaseController implements Initializable, Injectable {

	public BaseController() {
		Injector.regist(this);
		Injector.injectMembers(this);
	}

}
