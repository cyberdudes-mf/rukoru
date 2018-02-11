package hoshisugi.rukoru.app.models.redmine;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TimeEntries extends RedmineAPIResponse {

	private final ObservableList<TimeEntry> time_entries = FXCollections.observableArrayList();

	public ObservableList<TimeEntry> getTime_entries() {
		return time_entries;
	}

	public void setTime_entries(final List<TimeEntry> time_entries) {
		this.time_entries.setAll(time_entries);
	}

}
