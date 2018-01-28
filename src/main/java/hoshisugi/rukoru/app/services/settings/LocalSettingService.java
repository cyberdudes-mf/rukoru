package hoshisugi.rukoru.app.services.settings;

import java.sql.SQLException;
import java.util.Optional;

import hoshisugi.rukoru.app.models.setings.Credential;

public interface LocalSettingService {

	void saveCredential(final Credential entity) throws SQLException;

	Optional<Credential> loadCredential() throws SQLException;
}
