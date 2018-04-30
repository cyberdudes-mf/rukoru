package hoshisugi.rukoru.app.services.settings;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import hoshisugi.rukoru.app.enums.Preferences;
import hoshisugi.rukoru.app.models.settings.Credential;
import hoshisugi.rukoru.app.models.settings.Preference;
import hoshisugi.rukoru.app.models.settings.RepositoryDBConnection;

public interface LocalSettingService {

	void saveCredential(final Credential entity) throws Exception;

	Optional<Credential> loadCredential() throws SQLException;

	void saveRepositoryDBConnection(final RepositoryDBConnection entity) throws Exception;

	Optional<RepositoryDBConnection> loadRepositoryDBConnection() throws SQLException;

	Map<String, Preference> getPreferencesByCategory(String category) throws SQLException;

	Optional<Preference> findPreference(Preferences preference) throws SQLException;

	void savePreferences(Collection<Preference> preferences) throws SQLException;

	void setStyleSheet() throws SQLException;

	void changeStyleSheet(String css) throws SQLException;
}