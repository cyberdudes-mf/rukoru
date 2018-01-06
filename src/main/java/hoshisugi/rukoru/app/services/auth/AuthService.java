package hoshisugi.rukoru.app.services.auth;

import java.sql.SQLException;
import java.util.Optional;

import hoshisugi.rukoru.app.models.auth.AuthSetting;

public interface AuthService {

	void save(final AuthSetting entity) throws SQLException;

	Optional<AuthSetting> load();
}
