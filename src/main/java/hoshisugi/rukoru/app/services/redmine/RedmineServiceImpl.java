package hoshisugi.rukoru.app.services.redmine;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Comparator;

import javax.ws.rs.client.WebTarget;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import hoshisugi.rukoru.app.models.redmine.Projects;
import hoshisugi.rukoru.app.models.redmine.ProjectsRequest;
import hoshisugi.rukoru.app.models.redmine.RedmineAPIRequest;
import hoshisugi.rukoru.app.models.redmine.RedmineAPIResponse;
import hoshisugi.rukoru.app.models.redmine.TimeEntries;
import hoshisugi.rukoru.app.models.redmine.TimeEntriesRequest;
import hoshisugi.rukoru.app.models.redmine.Version;
import hoshisugi.rukoru.app.models.redmine.Versions;
import hoshisugi.rukoru.app.models.redmine.VersionsRequest;
import hoshisugi.rukoru.framework.base.BaseService;

public class RedmineServiceImpl extends BaseService implements RedmineService {

	@Override
	public Projects listProjects(final ProjectsRequest request) {
		return doGet(request, Projects.class);
	}

	@Override
	public Versions listVersions(final VersionsRequest request) {
		final Versions versions = doGet(request, Versions.class);
		versions.getVersions().sort(Comparator.comparing(Version::getId).reversed());
		return versions;
	}

	@Override
	public TimeEntries listTimeEntries(final TimeEntriesRequest request) {
		return doGet(request, TimeEntries.class);
	}

	private <T extends RedmineAPIResponse> T doGet(final RedmineAPIRequest request, final Class<T> cls) {
		final WebTarget target = request.createTarget();
		final String json = target.request().get(String.class);
		final ObjectMapper mapper = new ObjectMapper();
		try {
			final T response = mapper.readValue(json, cls);
			final ObjectReader updater = mapper.readerForUpdating(response);
			while (response.hasNext()) {
				request.addOffset(request.getLimit());
				final WebTarget appendTarget = request.createTarget();
				final String appendJson = appendTarget.request().get(String.class);
				updater.readValue(appendJson);
			}
			return response;
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
