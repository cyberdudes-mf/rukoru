package hoshisugi.rukoru.app.models.settings;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.google.common.util.concurrent.UncheckedExecutionException;

import hoshisugi.rukoru.app.services.settings.LocalSettingService;
import hoshisugi.rukoru.framework.inject.Injector;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Credential extends DBEntity {

	private final StringProperty account = new SimpleStringProperty(this, "account");

	private final StringProperty accessKeyId = new SimpleStringProperty(this, "accessKeyId");

	private final StringProperty secretAccessKey = new SimpleStringProperty(this, "secretAccessKey");

	private static EntityCache<Credential> cache = new EntityCache<>(Credential::load);

	public static Credential get() {
		return cache.get();
	}

	public static boolean hasCredential() {
		return cache.hasEntity();
	}

	public static void reload() throws Exception {
		cache.reload();
	}

	private static Optional<Credential> load() throws SQLException {
		final LocalSettingService service = Injector.getInstance(LocalSettingService.class);
		return service.loadCredential();
	}

	public Credential() {
	}

	public Credential(final ResultSet rs) {
		try {
			setId(rs.getInt("id"));
			setAccount(rs.getString("account"));
			setAccessKeyId(rs.getString("access_key_id"));
			setSecretAccessKey(rs.getString("secret_access_key"));
			setCreatedAt(rs.getTimestamp("created_at"));
			setUpdatedAt(rs.getTimestamp("updated_at"));
		} catch (final SQLException e) {
			throw new UncheckedExecutionException(e);
		}
	}

	public AWSCredentialsProvider createCredentialsProvider() {
		final BasicAWSCredentials credentials = new BasicAWSCredentials(getAccessKeyId(), getSecretAccessKey());
		return new AWSStaticCredentialsProvider(credentials);
	}

	public String getAccount() {
		return account.get();
	}

	public void setAccount(final String account) {
		this.account.set(account);
	}

	public String getAccessKeyId() {
		return accessKeyId.get();
	}

	public void setAccessKeyId(final String accessKeyId) {
		this.accessKeyId.set(accessKeyId);
	}

	public String getSecretAccessKey() {
		return secretAccessKey.get();
	}

	public void setSecretAccessKey(final String secretAccessKey) {
		this.secretAccessKey.set(secretAccessKey);
	}

	public StringProperty accountProperty() {
		return account;
	}

	public StringProperty accessKeyIdProperty() {
		return accessKeyId;
	}

	public StringProperty secretAccessKeyProperty() {
		return secretAccessKey;
	}

}
