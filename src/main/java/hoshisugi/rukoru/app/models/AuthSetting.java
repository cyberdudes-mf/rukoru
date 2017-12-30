package hoshisugi.rukoru.app.models;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.google.common.util.concurrent.UncheckedExecutionException;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class AuthSetting implements Serializable {

	private final ObjectProperty<Integer> id = new SimpleObjectProperty<>(this, "id");

	private final StringProperty account = new SimpleStringProperty(this, "account");

	private final StringProperty accessKeyId = new SimpleStringProperty(this, "accessKeyId");

	private final StringProperty secretAccessKey = new SimpleStringProperty(this, "secretAccessKey");

	private final ObjectProperty<Timestamp> createdAt = new SimpleObjectProperty<>(this, "createdAt");

	private final ObjectProperty<Timestamp> updatedAt = new SimpleObjectProperty<>(this, "updatedAt");

	public AuthSetting() {
	}

	public AuthSetting(final ResultSet rs) {
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

	public Integer getId() {
		return id.get();
	}

	public void setId(final Integer id) {
		this.id.set(id);
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

	public Timestamp getCreatedAt() {
		return createdAt.get();
	}

	public void setCreatedAt(final Timestamp createdAt) {
		this.createdAt.set(createdAt);
	}

	public Timestamp getUpdatedAt() {
		return updatedAt.get();
	}

	public void setUpdatedAt(final Timestamp updatedAt) {
		this.updatedAt.set(updatedAt);
	}

	public ObjectProperty<Integer> idProperty() {
		return id;
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

	public ObjectProperty<Timestamp> createdAtProperty() {
		return createdAt;
	}

	public ObjectProperty<Timestamp> updatedAtProperty() {
		return updatedAt;
	}

}
