package hoshisugi.rukoru.app.services.settings;

import static hoshisugi.rukoru.framework.util.AssetUtil.loadSQL;
import static hoshisugi.rukoru.framework.util.SelectBuilder.from;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import hoshisugi.rukoru.app.models.settings.Credential;
import hoshisugi.rukoru.app.models.settings.Preference;
import hoshisugi.rukoru.app.models.settings.RepositoryDBConnection;
import hoshisugi.rukoru.framework.base.BaseService;
import hoshisugi.rukoru.framework.util.SelectBuilder;

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
		final List<Credential> results = h2.executeQuery(from("auth_settings"), Credential::new);
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
		final List<RepositoryDBConnection> results = h2.executeQuery(from("repositorydb_settings"),
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

	@Override
	public Map<String, Preference> getPreferencesByCategory(final String category) throws SQLException {
		try (H2Database h2 = new H2Database()) {
			if (!h2.exists("PREFERENCES")) {
				return Collections.emptyMap();
			}
			final SelectBuilder select = from("preferences").where("category", category);
			final List<Preference> preferences = h2.executeQuery(select, Preference::new);
			return preferences.stream().collect(Collectors.toMap(Preference::getKey, Function.identity()));
		}
	}

	@Override
	public void savePreferences(final Collection<Preference> preferences) throws SQLException {
		try (final H2Database h2 = new H2Database()) {
			if (!h2.exists("PREFERENCES")) {
				h2.executeUpdate(loadSQL("create_preferences.sql"));
			}
			for (final Preference preference : preferences) {
				if (preference.getId() == null) {
					final SelectBuilder select = from("preferences").where("category", preference.getCategory())
							.and("key", preference.getKey());
					final List<Preference> entities = h2.executeQuery(select, Preference::new);
					if (entities.isEmpty()) {
						h2.executeUpdate(loadSQL("insert_preferences.sql"), preference.getCategory(),
								preference.getKey(), preference.getValue());
					} else {
						updatePreference(h2, entities.get(0));
					}
				} else {
					updatePreference(h2, preference);
				}
			}
		}
	}

	private void updatePreference(final H2Database h2, final Preference preference) throws SQLException {
		final int result = h2.executeUpdate(loadSQL("update_preferences.sql"), preference.getCategory(),
				preference.getKey(), preference.getValue(), preference.getId(), preference.getUpdatedAt());
		if (result == 0) {
			throw new IllegalStateException("Record has been updated from other thread.");
		}
	}

	@Override
	public Optional<Preference> findPreferenceByCategoryAndKey(final String category, final String key)
			throws SQLException {
		try (H2Database h2 = new H2Database()) {
			if (!h2.exists("PREFERENCES")) {
				return Optional.empty();
			}
			final SelectBuilder select = from("preferences").where("category", category).and("key", key);
			return h2.executeQuery(select, Preference::new).stream().findFirst();
		}
	}

}
