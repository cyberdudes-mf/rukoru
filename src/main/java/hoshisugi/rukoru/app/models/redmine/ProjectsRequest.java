package hoshisugi.rukoru.app.models.redmine;

public class ProjectsRequest extends RedmineAPIRequest {

	public ProjectsRequest() {
		super("/projects.json");
	}

}
