package hoshisugi.rukoru.app.services.settings;

import static hoshisugi.rukoru.framework.util.AssetUtil.loadSQL;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import hoshisugi.rukoru.app.models.setings.Credential;
import hoshisugi.rukoru.framework.base.BaseService;

public class LocalSettingServiceImpl extends BaseService implements LocalSettingService {

	@Override
	public void saveCredential(final Credential entity) throws SQLException {
		try (H2Database h2 = new H2Database()) {
			if (!h2.exists("AUTH_SETTINGS")) {
				h2.executeUpdate(loadSQL("create_auth_settings.sql"));
			}
			if (entity.getId() == null) {
				if (loadCredential().isPresent()) {
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

}
