package hoshisugi.rukoru.app.models.redmine;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Versions extends RedmineAPIResponse {

	private final ObservableList<Version> versions = FXCollections.observableArrayList();

	public ObservableList<Version> getVersions() {
		return versions;
	}

	public void setVersions(final List<Version> versions) {
		this.versions.setAll(versions);
	}

}
