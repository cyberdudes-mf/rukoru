package hoshisugi.rukoru.app.services.settings;

import static hoshisugi.rukoru.framework.database.builder.Column.$;
import static hoshisugi.rukoru.framework.database.builder.InsertBuilder.into;
import static hoshisugi.rukoru.framework.database.builder.SelectBuilder.from;
import static hoshisugi.rukoru.framework.database.builder.UpdateBuilder.table;

import java.sql.ResultSet;
import java.sql.SQLException;
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
import hoshisugi.rukoru.app.models.settings.S3VideoCredential;
import hoshisugi.rukoru.framework.base.BaseService;
import hoshisugi.rukoru.framework.database.builder.Column;
import hoshisugi.rukoru.framework.database.builder.CreateBuilder;
import hoshisugi.rukoru.framework.database.builder.DeleteBuilder;
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
				h2.insert(into("auth_settings").values($("account", entity.getAccount()),
						$("access_key_id", entity.getAccessKeyId()),
						$("secret_access_key", entity.getSecretAccessKey())));
				loadCredential(h2).ifPresent(c -> {
					entity.setId(c.getId());
					entity.setUpdatedAt(c.getUpdatedAt());
				});
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
				loadRepositoryDBConnection(h2).ifPresent(e -> {
					entity.setId(e.getId());
					entity.setUpdatedAt(e.getUpdatedAt());
				});
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
	public void savePreference(final Preference preference) throws SQLException {
		savePreferences(Arrays.asList(preference));
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
			if (!h2.exists("DS_SETTINGS")) {
				h2.create(CreateBuilder.table("ds_settings_sequence"));
				h2.create(CreateBuilder.table("ds_settings"));
			}
			final Map<String, List<DSSetting>> stateMap = settings.stream()
					.collect(Collectors.groupingBy(DSSetting::getState));
			deleteDSSettings(h2, stateMap.get("Delete"));
			updateDSSettings(h2, stateMap.get("Update"));
			insertDSSettings(h2, stateMap.get("Insert"));
		}
	}

	@Override
	public List<DSSetting> loadDSSettings() throws SQLException {
		try (final H2Database h2 = new H2Database()) {
			if (!h2.exists("DS_SETTINGS")) {
				return Collections.emptyList();
			}
			return h2.select(from("ds_settings"), DSSetting::new);
		}
	}

	@Override
	public Optional<S3VideoCredential> loadS3VideoCredential() throws SQLException {
		try (final H2Database h2 = new H2Database()) {
			return loadS3VideoCredential(h2);
		}
	}

	public Optional<S3VideoCredential> loadS3VideoCredential(final H2Database h2) throws SQLException {
		if (!h2.exists("S3_VIDEO_CREDENTIAL")) {
			return Optional.empty();
		}
		return h2.find(from("s3_video_credential"), S3VideoCredential::new);
	}

	@Override
	public void saveS3VideoCredential(final S3VideoCredential credential) throws Exception {
		try (H2Database h2 = new H2Database()) {
			if (!h2.exists("S3_VIDEO_CREDENTIAL")) {
				h2.create(CreateBuilder.table("s3_video_credential"));
			}
			if (credential.getId() == null) {
				if (loadS3VideoCredential(h2).isPresent()) {
					throw new IllegalStateException("Record has been updated from other thread.");
				}
				h2.insert(into("s3_video_credential").values($("access_key_id", credential.getAccessKeyId()),
						$("secret_access_key", credential.getSecretAccessKey()), $("bucket", credential.getBucket())));
				loadS3VideoCredential(h2).ifPresent(c -> {
					credential.setId(c.getId());
					credential.setUpdatedAt(c.getUpdatedAt());
				});
			} else {
				final int result = h2.update(table("s3_video_credential")
						.set($("access_key_id", credential.getAccessKeyId()),
								$("secret_access_key", credential.getSecretAccessKey()),
								$("bucket", credential.getBucket()))
						.where($("id", credential.getId()), $("updated_at", credential.getUpdatedAt())));
				if (result == 0) {
					throw new IllegalStateException("Record has been updated from other thread.");
				}
			}
			S3VideoCredential.reload();
		}
	}

	private void deleteDSSettings(final H2Database h2, final List<DSSetting> settings) throws SQLException {
		if (settings == null) {
			return;
		}
		for (final DSSetting setting : settings) {
			final Integer id = Optional.ofNullable(setting.getId()).orElse(0);
			h2.delete(DeleteBuilder.from("ds_settings").where($("id", id)));
		}
	}

	private void updateDSSettings(final H2Database h2, final List<DSSetting> settings) throws SQLException {
		if (settings == null) {
			return;
		}
		for (final DSSetting setting : settings) {
			h2.update(table("ds_settings")
					.set($("name", setting.getName()), $("executionpath", setting.getExecutionPath()),
							$("executiontype", setting.getExecutionType().getId()),
							$("studiomode", setting.getStudioMode().getId()))
					.where($("id", setting.getId()), $("updated_at", setting.getUpdatedAt())));
		}
	}

	private void insertDSSettings(final H2Database h2, final List<DSSetting> settings) throws SQLException {
		if (settings == null) {
			return;
		}
		for (final DSSetting setting : settings) {
			setting.setId(h2.find(from("ds_settings_sequence"), this::getInt).get());
			h2.insert(into("ds_settings").values($("id", setting.getId()), $("name", setting.getName()),
					$("executionpath", setting.getExecutionPath()),
					$("executiontype", setting.getExecutionType().getId()),
					$("studiomode", setting.getStudioMode().getId())));
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
