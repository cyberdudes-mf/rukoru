package hoshisugi.rukoru.app.models.settings;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import com.google.common.util.concurrent.UncheckedExecutionException;

import hoshisugi.rukoru.app.services.settings.LocalSettingService;
import hoshisugi.rukoru.framework.inject.Injector;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class S3VideoCredential extends DBEntity {
	private final StringProperty accessKeyId = new SimpleStringProperty(this, "accessKeyId");
	private final StringProperty secretAccessKey = new SimpleStringProperty(this, "secretAccessKey");
	private final StringProperty bucket = new SimpleStringProperty(this, "bucket");

	private static EntityCache<S3VideoCredential> cache = new EntityCache<>(S3VideoCredential::load);

	public S3VideoCredential() {
	}

	public S3VideoCredential(final ResultSet rs) {
		try {
			setId(rs.getInt("id"));
			setAccessKeyId(rs.getString("access_key_id"));
			setSecretAccessKey(rs.getString("secret_access_key"));
			setBucket(rs.getString("bucket"));
			setCreatedAt(rs.getTimestamp("created_at"));
			setUpdatedAt(rs.getTimestamp("updated_at"));
		} catch (final SQLException e) {
			throw new UncheckedExecutionException(e);
		}
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

	public String getBucket() {
		return bucket.get();
	}

	public void setBucket(final String bucket) {
		this.bucket.set(bucket);
	}

	public StringProperty accessKeyIdProperty() {
		return accessKeyId;
	}

	public StringProperty secretAccessKeyProperty() {
		return secretAccessKey;
	}

	public StringProperty bucketProperty() {
		return bucket;
	}

	public static S3VideoCredential get() {
		return cache.get();
	}

	public static boolean hasCredential() {
		return cache.hasEntity();
	}

	public static void reload() throws Exception {
		cache.reload();
	}

	private static Optional<S3VideoCredential> load() throws Exception {
		final LocalSettingService service = Injector.getInstance(LocalSettingService.class);
		return service.loadS3VideoCredential();
	}

}
