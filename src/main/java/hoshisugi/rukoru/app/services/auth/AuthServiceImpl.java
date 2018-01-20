package hoshisugi.rukoru.app.services.auth;

import static hoshisugi.rukoru.framework.util.AssetUtil.loadSQL;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import hoshisugi.rukoru.app.models.auth.AuthSetting;
import hoshisugi.rukoru.framework.base.BaseService;

public class AuthServiceImpl extends BaseService implements AuthService {

	public AuthServiceImpl() {
		super();
	}

	@Override
	public void save(final AuthSetting entity) throws SQLException {
		final H2Database h2 = new H2Database();
		if (!h2.exists("AUTH_SETTINGS")) {
			h2.executeUpdate(loadSQL("create_auth_settings.sql"));
		}
		if (entity.getId() == null) {
			if (load().isPresent()) {
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
		AuthSetting.reload();
	}

	public Optional<AuthSetting> load(final H2Database h2) throws SQLException {
		if (!h2.exists("AUTH_SETTINGS")) {
			return Optional.empty();
		}
		final List<AuthSetting> results = h2.executeQuery(loadSQL("select_auth_settings.sql"), AuthSetting::new);
		if (results.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(results.get(0));
	}

	@Override
	public Optional<AuthSetting> load() throws SQLException {
		return load(new H2Database());
	}

}
