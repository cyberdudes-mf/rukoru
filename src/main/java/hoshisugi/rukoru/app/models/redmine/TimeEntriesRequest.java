package hoshisugi.rukoru.app.models.redmine;

import java.time.LocalDate;

public class TimeEntriesRequest extends RedmineAPIRequest {

	private Integer user_id;

	private Integer project_id;

	private LocalDate spent_on_min;

	private LocalDate spent_on_max;

	public TimeEntriesRequest(final String path) {
		super("/time_entries.json");
	}

	public Integer getUser_id() {
		return user_id;
	}

	public void setUser_id(final Integer user_id) {
		this.user_id = user_id;
	}

	public Integer getProject_id() {
		return project_id;
	}

	public void setProject_id(final Integer project_id) {
		this.project_id = project_id;
	}

	public LocalDate getSpent_on_min() {
		return spent_on_min;
	}

	public void setSpent_on_min(final LocalDate spent_on_min) {
		this.spent_on_min = spent_on_min;
	}

	public LocalDate getSpent_on_max() {
		return spent_on_max;
	}

	public void setSpent_on_max(final LocalDate spent_on_max) {
		this.spent_on_max = spent_on_max;
	}

}
