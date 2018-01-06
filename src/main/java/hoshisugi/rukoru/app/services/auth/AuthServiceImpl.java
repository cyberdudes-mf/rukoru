package hoshisugi.rukoru.app.services.auth;

import static hoshisugi.rukoru.framework.util.AssetUtil.loadSQL;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.inject.Inject;

import hoshisugi.rukoru.app.models.AuthSetting;
import hoshisugi.rukoru.framework.database.Database;
import hoshisugi.rukoru.framework.services.BaseService;

public class AuthServiceImpl extends BaseService implements AuthService {

	@Inject
	private Database database;

	public AuthServiceImpl() {
		super();
	}

	@Override
	public void save(final AuthSetting entity) throws SQLException {
		if (!database.exists("AUTH_SETTINGS")) {
			database.executeUpdate(loadSQL("create_auth_settings.sql"));
		}
		if (entity.getId() == null) {
			if (load().isPresent()) {
				throw new IllegalStateException("Record has been updated from other thread.");
			}
			database.executeUpdate(loadSQL("insert_auth_settings.sql"), entity.getAccount(), entity.getAccessKeyId(),
					entity.getSecretAccessKey());
		} else {
			final int result = database.executeUpdate(loadSQL("update_auth_settings.sql"), entity.getAccount(),
					entity.getAccessKeyId(), entity.getSecretAccessKey(), entity.getId(), entity.getUpdatedAt());
			if (result == 0) {
				throw new IllegalStateException("Record has been updated from other thread.");
			}
		}
		AuthSetting.reload();
	}

	@Override
	public Optional<AuthSetting> load() {
		try {
			if (!database.exists("AUTH_SETTINGS")) {
				return Optional.empty();
			}
			final List<AuthSetting> results = database.executeQuery(AuthSetting::new,
					loadSQL("select_auth_settings.sql"));
			if (results.isEmpty()) {
				return Optional.empty();
			}
			return Optional.of(results.get(0));
		} catch (final SQLException e) {
			throw new UncheckedExecutionException(e);
		}
	}

}
