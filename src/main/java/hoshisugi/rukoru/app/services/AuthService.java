package hoshisugi.rukoru.app.services;

import java.sql.SQLException;
import java.util.Optional;

public interface AuthService {

	void save(final AuthSetting entity) throws SQLException;

	Optional<AuthSetting> load() throws SQLException;
}
