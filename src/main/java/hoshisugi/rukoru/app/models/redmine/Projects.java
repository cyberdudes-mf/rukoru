package hoshisugi.rukoru.app.models.redmine;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Projects extends RedmineAPIResponse {

	private final ObservableList<Project> projects = FXCollections.observableArrayList();

	public ObservableList<Project> getProjects() {
		return projects;
	}

	public void setProjects(final List<Project> projects) {
		this.projects.addAll(projects);
	}

	@Override
	public int count() {
		return projects.size();
	}

}
