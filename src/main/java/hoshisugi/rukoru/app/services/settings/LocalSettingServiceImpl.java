package hoshisugi.rukoru.app.services.settings;

import static hoshisugi.rukoru.framework.database.builder.Column.$;
import static hoshisugi.rukoru.framework.database.builder.InsertBuilder.into;
import static hoshisugi.rukoru.framework.database.builder.SelectBuilder.from;
import static hoshisugi.rukoru.framework.database.builder.UpdateBuilder.table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import hoshisugi.rukoru.app.enums.Preferences;
import hoshisugi.rukoru.app.models.ds.DSSetting;
import hoshisugi.rukoru.app.models.settings.Credential;
import hoshisugi.rukoru.app.models.settings.Preference;
import hoshisugi.rukoru.app.models.settings.RepositoryDBConnection;
import hoshisugi.rukoru.framework.base.BaseService;
import hoshisugi.rukoru.framework.database.builder.Column;
import hoshisugi.rukoru.framework.database.builder.CreateBuilder;
import hoshisugi.rukoru.framework.database.builder.DeleteBuilder;
import hoshisugi.rukoru.framework.database.builder.InsertBuilder;
import hoshisugi.rukoru.framework.database.builder.SelectBuilder;

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
		return h2.find(from("auth_settings"), Credential::new);
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
		return h2.find(from("repositorydb_settings"), RepositoryDBConnection::new);
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
				final Column category = $("category", preference.getCategory());
				final Column key = $("key", preference.getKey());
				final Column value = $("value", preference.getValue());
				final Optional<Preference> entry = h2.find(from("preferences").where(category, key), Preference::new);
				if (entry.isPresent()) {
					final Preference p = entry.get();
					final int result = h2.update(table("preferences").set(category, key, value)
							.where($("id", p.getId()), $("updated_at", p.getUpdatedAt())));
					if (result == 0) {
						throw new IllegalStateException("Record has been updated from other thread.");
					}
				} else {
					h2.insert(InsertBuilder.into("preferences").values(category, key, value));
				}
			}
		}
	}

	@Override
	public Optional<Preference> findPreference(final Preferences preference) throws SQLException {
		try (H2Database h2 = new H2Database()) {
			if (!h2.exists("PREFERENCES")) {
				return Optional.empty();
			}
			return h2.find(from("preferences").where($("category", preference.category()), $("key", preference.key())),
					Preference::new);
		}
	}

	@Override
	public void saveDSSettings(final List<DSSetting> settings) throws SQLException {
		try (final H2Database h2 = new H2Database()) {
			if (!h2.exists("PREFERENCES")) {
				h2.create(CreateBuilder.table("preferences"));
			}
			if (!h2.exists("DS_SETTINGS")) {
				h2.create(CreateBuilder.table("ds_settings_sequence"));
				h2.create(CreateBuilder.table("ds_settings"));
			}

			final List<DSSetting> insertSettings = settings.stream().filter(t -> t.getState().equals("Insert"))
					.collect(Collectors.toList());

			final List<DSSetting> updateSettings = settings.stream().filter(t -> t.getState().equals("Update"))
					.collect(Collectors.toList());

			final List<DSSetting> deleteSettings = settings.stream().filter(t -> t.getState().equals("Delete"))
					.collect(Collectors.toList());

			deleteDSSettings(h2, deleteSettings);

			updateDSSettingsToDSSettings(h2, updateSettings);

			for (final DSSetting setting : insertSettings) {
				final int id = getDSSettingsSequence(h2);
				setting.setId(id);
				final String idString = Integer.toString(id);
				insertDSSettingsToDSSettings(h2, setting, idString);
				insertDSSettingsToPreferences(h2, setting, idString);
			}
		}
	}

	@Override
	public List<DSSetting> loadDSSettings() throws SQLException {
		try (final H2Database h2 = new H2Database()) {
			if (!h2.exists("DS_SETTINGS") || !h2.exists("PREFERENCES")) {
				return new ArrayList<>();
			}
			return h2.select(from("ds_settings"), DSSetting::new);
		}
	}

	private int getDSSettingsSequence(final H2Database h2) throws SQLException {
		return h2.find(from("ds_settings_sequence"), this::getInt).get() + 1;
	}

	private void updateDSSettingsToDSSettings(final H2Database h2, final List<DSSetting> updateSettings)
			throws SQLException {
		for (final DSSetting setting : updateSettings) {
			h2.update(table("ds_settings")
					.set($("name", setting.getName()), $("executionpath", setting.getExecutionPath()),
							$("executiontype", setting.getExecutionType().getId()),
							$("studiomode", setting.getStudioMode().getId()))
					.where($("id", setting.getId()), $("updated_at", setting.getUpdatedAt())));
		}
	}

	private void insertDSSettingsToDSSettings(final H2Database h2, final DSSetting setting, final String sequence)
			throws SQLException {
		h2.insert(into("ds_settings").values($("id", sequence), $("name", setting.getName()),
				$("executionpath", setting.getExecutionPath()), $("executiontype", setting.getExecutionType().getId()),
				$("studiomode", setting.getStudioMode().getId())));
	}

	private void insertDSSettingsToPreferences(final H2Database h2, final DSSetting setting, final String sequence)
			throws SQLException {
		final int nextKey = h2
				.find(SelectBuilder.query(
						"select MAX(CAST(key AS INT)) + 1 from preferences where category='DSSetting'"), this::getInt)
				.get();
		final Preference p = new Preference("DSSetting", Integer.toString(nextKey), sequence);
		savePreferences(Arrays.asList(p));
	}

	private void deleteDSSettings(final H2Database h2, final List<DSSetting> deleteSettings) throws SQLException {
		for (final DSSetting setting : deleteSettings) {
			h2.delete(DeleteBuilder.from("ds_settings").where($("id", setting.getId() != null ? setting.getId() : 0)));
			h2.delete(DeleteBuilder.from("preferences").where($("category", "DSSetting"), $("value", setting.getId())));
		}
	}

	private int getInt(final ResultSet rs) {
		try {
			return rs.getInt(1);
		} catch (final SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
