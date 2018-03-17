package hoshisugi.rukoru.app.services.settings;

import static hoshisugi.rukoru.framework.database.builder.Column.$;
import static hoshisugi.rukoru.framework.database.builder.InsertBuilder.into;
import static hoshisugi.rukoru.framework.database.builder.SelectBuilder.from;
import static hoshisugi.rukoru.framework.database.builder.UpdateBuilder.table;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import hoshisugi.rukoru.app.enums.ExecutionType;
import hoshisugi.rukoru.app.models.settings.Credential;
import hoshisugi.rukoru.app.models.settings.DSSetting;
import hoshisugi.rukoru.app.models.settings.Preference;
import hoshisugi.rukoru.app.models.settings.RepositoryDBConnection;
import hoshisugi.rukoru.framework.base.BaseService;
import hoshisugi.rukoru.framework.database.builder.Column;
import hoshisugi.rukoru.framework.database.builder.CreateBuilder;
import hoshisugi.rukoru.framework.database.builder.DeleteBuilder;
import hoshisugi.rukoru.framework.database.builder.InsertBuilder;
import hoshisugi.rukoru.framework.database.builder.SelectBuilder;
import hoshisugi.rukoru.framework.database.builder.UpdateBuilder;

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
	public Optional<Preference> findPreferenceByCategoryAndKey(final String category, final String key)
			throws SQLException {
		try (H2Database h2 = new H2Database()) {
			if (!h2.exists("PREFERENCES")) {
				return Optional.empty();
			}
			return h2.find(from("preferences").where($("category", category), $("key", key)), Preference::new);
		}
	}

	@Override
	public void saveDSSettings(final List<DSSetting> settings) throws SQLException {
		try (final H2Database h2 = new H2Database()) {
			final int sequence = getDSSettingsSequence(h2);
			if (sequence == 0) {
				h2.create(CreateBuilder.table("ds_settings_sequence"));
			}
			if (!h2.exists("DS_SETTINGS")) {
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

			updateDSSettingsToPreference(h2);

			insertDSSettingsToDSSettings(h2, insertSettings);

			insertDSSettingsToPreference(h2, insertSettings, sequence);

		}
	}

	private int getDSSettingsSequence(final H2Database h2) throws SQLException {
		int sequence = 0;
		try {
			sequence = h2.select(from("ds_settings_sequence"), rs -> {
				try {
					return rs.getInt(1);
				} catch (final SQLException e) {
					return 0;
				}
			}).get(0);
		} catch (final SQLException e) {
			return sequence;
		}
		return sequence;
	}

	private void updateDSSettingsToDSSettings(final H2Database h2, final List<DSSetting> updateSettings)
			throws SQLException {
		for (final DSSetting setting : updateSettings) {
			h2.update(table("ds_settings")
					.set($("name", setting.getName()), $("executionpath", setting.getExecutionPath()),
							$("executiontype", ExecutionType.toId(setting.getExecutionType())))
					.where($("id", setting.getId()), $("updated_at", setting.getUpdatedAt())));
		}
	}

	private void insertDSSettingsToDSSettings(final H2Database h2, final List<DSSetting> insertSettings)
			throws SQLException {
		for (final DSSetting setting : insertSettings) {
			h2.insert(into("ds_settings").values($("name", setting.getName()),
					$("executionpath", setting.getExecutionPath()),
					$("executiontype", ExecutionType.toId(setting.getExecutionType()))));
		}
	}

	private void updateDSSettingsToPreference(final H2Database h2) throws SQLException {
		final List<DSSetting> settings = h2.select(SelectBuilder.query("select * from ds_settings order by id ASC"),
				DSSetting::new);
		for (int index = 0; index < settings.size(); index++) {
			if (!findPreferenceByCategoryAndKey("DSSetting", index + "").isPresent()) {
				h2.update(UpdateBuilder.table("preferences").set($("key", index + ""))
						.where($("value", settings.get(index).getId())));
			}
		}
	}

	private void insertDSSettingsToPreference(final H2Database h2, final List<DSSetting> insertSettings, int sequence)
			throws SQLException {
		final List<Preference> preferences = new ArrayList<>();
		final int seq = getPreferencesByCategory("DSSetting").values().size();
		for (int index = 0; index < insertSettings.size(); index++) {
			final Preference p = new Preference("DSSetting", index + seq + "");
			p.setValue("" + ++sequence);
			preferences.add(p);
		}
		savePreferences(preferences);
	}

	private void deleteDSSettings(final H2Database h2, final List<DSSetting> deleteSettings) throws SQLException {
		for (final DSSetting setting : deleteSettings) {
			h2.delete(DeleteBuilder.from("ds_settings")
					.where($("id", setting.getId() != null ? setting.getId() + "" : "0")));
			h2.delete(DeleteBuilder.from("preferences").where($("category", "DSSetting"),
					$("value", setting.getId() + "")));
		}
	}

	@Override
	public List<DSSetting> loadDSSettings() throws SQLException {
		try (final H2Database h2 = new H2Database()) {
			if (!h2.exists("DS_SETTINGS")) {
				return new ArrayList<>();
			}
			return h2.select(from("ds_settings"), DSSetting::new);
		}
	}
}
