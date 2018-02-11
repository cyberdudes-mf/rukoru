package hoshisugi.rukoru.app.models.redmine;

public class VersionsRequest extends RedmineAPIRequest {

	private final Integer projectId;

	public VersionsRequest(final Integer projectId) {
		super(String.format("/projects/%s/versions.json", projectId));
		this.projectId = projectId;
	}

	public Integer getProjetId() {
		return projectId;
	}

}
