package hoshisugi.rukoru.app.models.redmine;

public abstract class RedmineAPIResponse {

	private int limit;
	private int offset;
	private int total_count;

	public int getLimit() {
		return limit;
	}

	public void setLimit(final int limit) {
		this.limit = limit;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(final int offset) {
		this.offset = offset;
	}

	public int getTotal_count() {
		return total_count;
	}

	public void setTotal_count(final int total_count) {
		this.total_count = total_count;
	}

}
