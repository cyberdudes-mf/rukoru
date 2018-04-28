package hoshisugi.rukoru.app.models.ds;

public interface DSEntry {

	DSSetting getDsSetting();

	DSLogWriter getServerLogWriter();

	DSLogWriter getStudioLogWriter();

	void setServerButtonDisable(boolean disable);

	void setStudioButtonDisable(boolean disable);

	void setServerButtonSelected(boolean disable);

	void setStudioButtonSelected(boolean disable);

	boolean isServerButtonSelected();

	boolean isStudioButtonSelected();

	void stopOnExit();
}
