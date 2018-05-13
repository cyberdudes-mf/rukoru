package hoshisugi.rukoru.app.enums;

import hoshisugi.rukoru.app.view.ds.DSContentController;
import hoshisugi.rukoru.app.view.ec2.EC2ContentController;
import hoshisugi.rukoru.app.view.redmine.TaskboardController;
import hoshisugi.rukoru.app.view.repositorydb.RepositoryDBContentController;
import hoshisugi.rukoru.app.view.s3.S3ExplorerController;
import hoshisugi.rukoru.app.view.video.VideoController;
import hoshisugi.rukoru.framework.base.BaseController;

public enum RukoruModule {

	ManagementConsole(null, "マネコン", "32x32/AWS.png"),
	TestPortal(null, "テストポータル", "32x32/DS.png"),
	ClearCache(null, "Clear Cache", "32x32/WPF.png"),
	DataSpider(DSContentController.class, "DataSpider", "32x32/DS2.png"),
	EC2(EC2ContentController.class, "EC2", "32x32/EC2.png"),
	RepositoryDB(RepositoryDBContentController.class, "リポジトリDB", "32x32/DB.png"),
	S3(S3ExplorerController.class, "S3", "32x32/S3.png"),
	Taskboard(TaskboardController.class, "タスクボード", "32x32/Redmine.png"),
	Video(VideoController.class, "ビデオ", "32x32/Video.png"),
	;

	private final Class<? extends BaseController> controllerClass;
	private final String diplayName;
	private final String iconPath;

	private RukoruModule(final Class<? extends BaseController> controllerClass, final String diplayName,
			final String iconPath) {
		this.controllerClass = controllerClass;
		this.diplayName = diplayName;
		this.iconPath = iconPath;
	}

	public Class<? extends BaseController> getControllerClass() {
		return controllerClass;
	}

	public String getDiplayName() {
		return diplayName;
	}

	public String getIconPath() {
		return iconPath;
	}

}
