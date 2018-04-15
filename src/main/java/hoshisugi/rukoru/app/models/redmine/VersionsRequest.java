package hoshisugi.rukoru.app.models.redmine;

import javax.ws.rs.client.WebTarget;

public class VersionsRequest extends RedmineAPIRequest {

	private final Integer projectId;

	public VersionsRequest(final Integer projectId) {
		super(String.format("/projects/%s/versions.json", projectId));
		this.projectId = projectId;
	}

	public Integer getProjetId() {
		return projectId;
	}

	@Override
	protected WebTarget applyQueries(final WebTarget target) {
		WebTarget t = super.applyQueries(target);
		if (projectId != null) {
			t = t.queryParam("projectId", projectId);
		}
		return t;
	}

}
