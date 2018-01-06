package hoshisugi.rukoru.app.models;

import static hoshisugi.rukoru.app.models.S3Item.Type.Root;

public class S3Root extends S3Item {

	public S3Root() {
		setName("Amazon S3");
	}

	@Override
	public Type getType() {
		return Root;
	}

}
