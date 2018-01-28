package hoshisugi.rukoru.app.services.settings;

import static hoshisugi.rukoru.framework.util.AssetUtil.loadSQL;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import hoshisugi.rukoru.app.models.settings.Credential;
import hoshisugi.rukoru.app.models.settings.RepositoryDBConnection;
import hoshisugi.rukoru.framework.base.BaseService;

public class LocalSettingServiceImpl extends BaseService implements LocalSettingService {

	@Override
	public void saveCredential(final Credential entity) throws Exception {
		try (H2Database h2 = new H2Database()) {
			if (!h2.exists("AUTH_SETTINGS")) {
				h2.executeUpdate(loadSQL("create_auth_settings.sql"));
			}
			if (entity.getId() == null) {
				if (loadCredential(h2).isPresent()) {
					throw new IllegalStateException("Record has been updated from other thread.");
				}
				h2.executeUpdate(loadSQL("insert_auth_settings.sql"), entity.getAccount(), entity.getAccessKeyId(),
						entity.getSecretAccessKey());
			} else {
				final int result = h2.executeUpdate(loadSQL("update_auth_settings.sql"), entity.getAccount(),
						entity.getAccessKeyId(), entity.getSecretAccessKey(), entity.getId(), entity.getUpdatedAt());
				if (result == 0) {
					throw new IllegalStateException("Record has been updated from other thread.");
				}
			}
			Credential.reload();
		}
	}

	@Override
	public Optional<Credential> loadCredential() throws SQLException {
		try (H2Database h2 = new H2Database()) {
			return loadCredential(h2);
		}
	}

	private Optional<Credential> loadCredential(final H2Database h2) throws SQLException {
		if (!h2.exists("AUTH_SETTINGS")) {
			return Optional.empty();
		}
		final List<Credential> results = h2.executeQuery(loadSQL("select_auth_settings.sql"), Credential::new);
		if (results.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(results.get(0));
	}

	@Override
	public Optional<RepositoryDBConnection> loadRepositoryDBConnection() throws SQLException {
		try (H2Database h2 = new H2Database()) {
			return loadRepositoryDBConnection(h2);
		}
	}

	private Optional<RepositoryDBConnection> loadRepositoryDBConnection(final H2Database h2) throws SQLException {
		if (!h2.exists("REPOSITORYDB_SETTINGS")) {
			return Optional.empty();
		}
		final List<RepositoryDBConnection> results = h2.executeQuery(loadSQL("select_repositorydb_settings.sql"),
				RepositoryDBConnection::new);
		if (results.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(results.get(0));
	}

	@Override
	public void saveRepositoryDBConnection(final RepositoryDBConnection entity) throws Exception {
		try (H2Database h2 = new H2Database()) {
			if (!h2.exists("REPOSITORYDB_SETTINGS")) {
				h2.executeUpdate(loadSQL("create_repositorydb_settings.sql"));
			}
			if (entity.getId() == null) {
				if (loadRepositoryDBConnection(h2).isPresent()) {
					throw new IllegalStateException("Record has been updated from other thread.");
				}
				h2.executeUpdate(loadSQL("insert_repositorydb_settings.sql"), entity.getInstanceName(),
						entity.getEndpoint(), entity.getPort(), entity.getUsername(), entity.getPassword());
			} else {
				final int result = h2.executeUpdate(loadSQL("update_repositorydb_settings.sql"),
						entity.getInstanceName(), entity.getEndpoint(), entity.getPort(), entity.getUsername(),
						entity.getPassword(), entity.getId(), entity.getUpdatedAt());
				if (result == 0) {
					throw new IllegalStateException("Record has been updated from other thread.");
				}
			}
			RepositoryDBConnection.reload();
		}
	}

}
