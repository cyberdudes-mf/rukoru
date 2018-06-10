package hoshisugi.rukoru.app.enums;

import hoshisugi.rukoru.app.view.ds.DSContentController;
import hoshisugi.rukoru.app.view.ec2.EC2ContentController;
import hoshisugi.rukoru.app.view.redmine.TaskboardController;
import hoshisugi.rukoru.app.view.repositorydb.RepositoryDBContentController;
import hoshisugi.rukoru.app.view.s3.S3ExplorerController;
import hoshisugi.rukoru.app.view.trace.ExcelTraceController;
import hoshisugi.rukoru.app.view.video.VideoController;
import hoshisugi.rukoru.framework.base.BaseController;

public enum RukoruModule {

	ManagementConsole(null, "マネコン", "32x32/AWS.png", false),
	TestPortal(null, "テストポータル", "32x32/DS.png", false),
	ClearCache(null, "Clear Cache", "32x32/WPF.png", false),
	DataSpider(DSContentController.class, "DataSpider", "32x32/DS2.png", false),
	EC2(EC2ContentController.class, "EC2", "32x32/EC2.png", false),
	RepositoryDB(RepositoryDBContentController.class, "リポジトリDB", "32x32/DB.png", false),
	S3(S3ExplorerController.class, "S3", "32x32/S3.png", false),
	Taskboard(TaskboardController.class, "タスクボード", "32x32/Redmine.png", false),
	Video(VideoController.class, "ビデオ", "32x32/Video.png", true),
	Trace(ExcelTraceController.class, "トレース", "32x32/easel.png", true),
	;

	private final Class<? extends BaseController> controllerClass;
	private final String displayName;
	private final String iconPath;
	private final boolean hidden;

	private RukoruModule(final Class<? extends BaseController> controllerClass, final String displayName,
			final String iconPath, boolean hidden) {
		this.controllerClass = controllerClass;
		this.displayName = displayName;
		this.iconPath = iconPath;
		this.hidden = hidden;
	}

	public Class<? extends BaseController> getControllerClass() {
		return controllerClass;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getIconPath() {
		return iconPath;
	}

	public boolean isHidden() {
		return hidden;
	}

}
