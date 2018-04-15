package hoshisugi.rukoru.app.services.redmine;

import hoshisugi.rukoru.app.models.redmine.Projects;
import hoshisugi.rukoru.app.models.redmine.ProjectsRequest;
import hoshisugi.rukoru.app.models.redmine.TimeEntries;
import hoshisugi.rukoru.app.models.redmine.TimeEntriesRequest;
import hoshisugi.rukoru.app.models.redmine.Versions;
import hoshisugi.rukoru.app.models.redmine.VersionsRequest;

public interface RedmineService {

	Projects listProjects(ProjectsRequest request);

	Versions listVersions(VersionsRequest request);

	TimeEntries listTimeEntries(TimeEntriesRequest request);
}
