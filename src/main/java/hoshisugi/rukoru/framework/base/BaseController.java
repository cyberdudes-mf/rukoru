package hoshisugi.rukoru.framework.base;

import hoshisugi.rukoru.framework.inject.Injectable;
import hoshisugi.rukoru.framework.inject.Injector;
import javafx.fxml.Initializable;

public abstract class BaseController implements Initializable, Injectable {

	public BaseController() {
		Injector.regist(this);
		Injector.injectMembers(this);
	}

}
