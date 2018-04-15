package hoshisugi.rukoru.app.models.redmine;

import java.util.Date;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TimeEntry {

	private final ObjectProperty<Activity> activity = new SimpleObjectProperty<>(this, "activity");
	private final StringProperty comments = new SimpleStringProperty(this, "comments");
	private final ObjectProperty<Date> created_on = new SimpleObjectProperty<>(this, "created_on");
	private final DoubleProperty hours = new SimpleDoubleProperty(this, "hours");
	private final IntegerProperty id = new SimpleIntegerProperty(this, "id");
	private final ObjectProperty<Issue> issue = new SimpleObjectProperty<>(this, "issue");
	private final ObjectProperty<Project> project = new SimpleObjectProperty<>(this, "project");
	private final ObjectProperty<Date> spent_on = new SimpleObjectProperty<>(this, "spent_on");
	private final ObjectProperty<Date> updated_on = new SimpleObjectProperty<>(this, "updated_on");
	private final ObjectProperty<User> user = new SimpleObjectProperty<>(this, "user");

	public Activity getActivity() {
		return activity.get();
	}

	public void setActivity(final Activity activity) {
		this.activity.set(activity);
	}

	public String getComments() {
		return comments.get();
	}

	public void setComments(final String comments) {
		this.comments.set(comments);
	}

	public Date getCreated_on() {
		return created_on.get();
	}

	public void setCreated_on(final Date created_on) {
		this.created_on.set(created_on);
	}

	public Double getHours() {
		return hours.get();
	}

	public void setHours(final Double hours) {
		this.hours.set(hours);
	}

	public Integer getId() {
		return id.get();
	}

	public void setId(final Integer id) {
		this.id.set(id);
	}

	public Issue getIssue() {
		return issue.get();
	}

	public void setIssue(final Issue issue) {
		this.issue.set(issue);
	}

	public Project getProject() {
		return project.get();
	}

	public void setProject(final Project project) {
		this.project.set(project);
	}

	public Date getSpent_on() {
		return spent_on.get();
	}

	public void setSpent_on(final Date spent_on) {
		this.spent_on.set(spent_on);
	}

	public Date getUpdated_on() {
		return updated_on.get();
	}

	public void setUpdated_on(final Date updated_on) {
		this.updated_on.set(updated_on);
	}

	public User getUser() {
		return user.get();
	}

	public void setUser(final User user) {
		this.user.set(user);
	}

	public ObjectProperty<Activity> activityProperty() {
		return activity;
	}

	public StringProperty commentsProperty() {
		return comments;
	}

	public ObjectProperty<Date> created_onProperty() {
		return created_on;
	}

	public DoubleProperty hoursProperty() {
		return hours;
	}

	public IntegerProperty idProperty() {
		return id;
	}

	public ObjectProperty<Issue> issueProperty() {
		return issue;
	}

	public ObjectProperty<Project> projectProperty() {
		return project;
	}

	public ObjectProperty<Date> spent_onProperty() {
		return spent_on;
	}

	public ObjectProperty<Date> updated_onProperty() {
		return updated_on;
	}

	public ObjectProperty<User> userProperty() {
		return user;
	}

}
