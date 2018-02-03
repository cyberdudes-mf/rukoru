package hoshisugi.rukoru.app.services.settings;

import static hoshisugi.rukoru.framework.database.builder.Column.$;
import static hoshisugi.rukoru.framework.database.builder.InsertBuilder.into;
import static hoshisugi.rukoru.framework.database.builder.SelectBuilder.from;
import static hoshisugi.rukoru.framework.database.builder.UpdateBuilder.table;

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
import hoshisugi.rukoru.framework.database.builder.CreateBuilder;
import hoshisugi.rukoru.framework.database.builder.InsertBuilder;

public class LocalSettingServiceImpl extends BaseService implements LocalSettingService {

	@Override
	public void saveCredential(final Credential entity) throws Exception {
		try (H2Database h2 = new H2Database()) {
			if (!h2.exists("AUTH_SETTINGS")) {
				h2.create(CreateBuilder.table("auth_settings"));
			}
			if (entity.getId() == null) {
				if (loadCredential(h2).isPresent()) {
					throw new IllegalStateException("Record has been updated from other thread.");
				}
				$("", "");
				h2.insert(into("auth_settings").values($("account", entity.getAccount()),
						$("access_key_id", entity.getAccessKeyId()),
						$("secret_access_key", entity.getSecretAccessKey())));
			} else {
				final int result = h2.update(table("auth_settings")
						.set($("account", entity.getAccount()), $("access_key_id", entity.getAccessKeyId()),
								$("secret_access_key", entity.getSecretAccessKey()))
						.where($("id", entity.getId()), $("updated_at", entity.getUpdatedAt())));
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
		final List<Credential> results = h2.select(from("auth_settings"), Credential::new);
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
		final List<RepositoryDBConnection> results = h2.select(from("repositorydb_settings"),
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
				h2.create(CreateBuilder.table("repositorydb_settings"));
			}
			if (entity.getId() == null) {
				if (loadRepositoryDBConnection(h2).isPresent()) {
					throw new IllegalStateException("Record has been updated from other thread.");
				}
				h2.insert(into("repositorydb_settings").values($("instance_name", entity.getInstanceName()),
						$("endpoint", entity.getEndpoint()), $("port", entity.getPort()),
						$("username", entity.getUsername()), $("password", entity.getPassword())));
			} else {
				final int result = h2.update(table("repositorydb_settings")
						.set($("instance_name", entity.getInstanceName()), $("endpoint", entity.getEndpoint()),
								$("port", entity.getPort()), $("username", entity.getUsername()),
								$("password", entity.getPassword()))
						.where($("id", entity.getId()), $("updated_at", entity.getUpdatedAt())));
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
			final List<Preference> preferences = h2.select(from("preferences").where($("category", category)),
					Preference::new);
			return preferences.stream().collect(Collectors.toMap(Preference::getKey, Function.identity()));
		}
	}

	@Override
	public void savePreferences(final Collection<Preference> preferences) throws SQLException {
		try (final H2Database h2 = new H2Database()) {
			if (!h2.exists("PREFERENCES")) {
				h2.create(CreateBuilder.table("preferences"));
			}
			for (final Preference preference : preferences) {
				if (preference.getId() == null) {
					final List<Preference> entities = h2.select(from("preferences").where(
							$("category", preference.getCategory()), $("key", preference.getKey())), Preference::new);
					if (entities.isEmpty()) {
						h2.insert(InsertBuilder.into("preferences").values($("category", preference.getCategory()),
								$("key", preference.getKey()), $("value", preference.getValue())));
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
		final int result = h2.update(table("preferences")
				.set($("category", preference.getCategory()), $("key", preference.getKey()),
						$("value", preference.getValue()))
				.where($("id", preference.getId()), $("updated_at", preference.getUpdatedAt())));
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
			return h2.select(from("preferences").where($("category", category), $("key", key)), Preference::new)
					.stream().findFirst();
		}
	}

}
