package hoshisugi.rukoru.app.models.s3;

public class UploadObjectResult extends AsyncResult {

	private S3Item item;

	public S3Item getItem() {
		return item;
	}

	public void setItem(final S3Item item) {
		this.item = item;
	}

}
