package hoshisugi.rukoru.app.inject;

import com.google.inject.Singleton;

import hoshisugi.rukoru.app.services.ds.DSService;
import hoshisugi.rukoru.app.services.ds.DSServiceImpl;
import hoshisugi.rukoru.app.services.ec2.EC2Service;
import hoshisugi.rukoru.app.services.ec2.EC2ServiceImpl;
import hoshisugi.rukoru.app.services.excel.ExcelService;
import hoshisugi.rukoru.app.services.excel.ExcelServiceImpl;
import hoshisugi.rukoru.app.services.rds.RDSService;
import hoshisugi.rukoru.app.services.rds.RDSServiceImpl;
import hoshisugi.rukoru.app.services.redmine.RedmineService;
import hoshisugi.rukoru.app.services.redmine.RedmineServiceImpl;
import hoshisugi.rukoru.app.services.repositorydb.RepositoryDBService;
import hoshisugi.rukoru.app.services.repositorydb.RepositoryDBServiceImpl;
import hoshisugi.rukoru.app.services.s3.S3Service;
import hoshisugi.rukoru.app.services.s3.S3ServiceImpl;
import hoshisugi.rukoru.app.services.settings.LocalSettingService;
import hoshisugi.rukoru.app.services.settings.LocalSettingServiceImpl;
import hoshisugi.rukoru.app.services.video.S3VideoServiceImpl;
import hoshisugi.rukoru.app.services.video.VideoService;
import hoshisugi.rukoru.app.view.ContentController;
import hoshisugi.rukoru.app.view.MainController;
import hoshisugi.rukoru.app.view.ToolBarController;
import hoshisugi.rukoru.app.view.ds.DSContentController;
import hoshisugi.rukoru.app.view.ec2.EC2ContentController;
import hoshisugi.rukoru.app.view.ec2.ImageTabController;
import hoshisugi.rukoru.app.view.ec2.InstanceTabController;
import hoshisugi.rukoru.app.view.redmine.TaskboardController;
import hoshisugi.rukoru.app.view.repositorydb.RepositoryDBContentController;
import hoshisugi.rukoru.app.view.s3.S3ExplorerController;
import hoshisugi.rukoru.app.view.s3.S3ExplorerMenuController;
import hoshisugi.rukoru.app.view.s3.S3ExplorerTableController;
import hoshisugi.rukoru.app.view.s3.S3ExplorerTreeController;
import hoshisugi.rukoru.app.view.trace.ExcelTraceController;
import hoshisugi.rukoru.app.view.video.VideoController;
import hoshisugi.rukoru.framework.inject.ModuleConfigurator;

public class RukoruModuleConfigurator extends ModuleConfigurator {

	@Override
	protected void configure() {
		configureServices();
		configureControllers();
	}

	private void configureControllers() {
		provide(MainController.class);
		provide(ContentController.class);
		provide(ToolBarController.class);
		provide(DSContentController.class);
		provide(EC2ContentController.class);
		provide(RepositoryDBContentController.class);
		provide(S3ExplorerController.class);
		provide(InstanceTabController.class);
		provide(ImageTabController.class);
		provide(S3ExplorerMenuController.class);
		provide(S3ExplorerTreeController.class);
		provide(S3ExplorerTableController.class);
		provide(TaskboardController.class);
		provide(VideoController.class);
		provide(ExcelTraceController.class);
	}

	private void configureServices() {
		bind(LocalSettingService.class).toProvider(() -> new LocalSettingServiceImpl()).in(Singleton.class);
		bind(EC2Service.class).toProvider(() -> new EC2ServiceImpl()).in(Singleton.class);
		bind(RDSService.class).toProvider(() -> new RDSServiceImpl()).in(Singleton.class);
		bind(S3Service.class).toProvider(() -> new S3ServiceImpl()).in(Singleton.class);
		bind(RepositoryDBService.class).toProvider(() -> new RepositoryDBServiceImpl()).in(Singleton.class);
		bind(RedmineService.class).toProvider(() -> new RedmineServiceImpl()).in(Singleton.class);
		bind(DSService.class).toProvider(() -> new DSServiceImpl()).in(Singleton.class);
		bind(VideoService.class).toProvider(() -> new S3VideoServiceImpl()).in(Singleton.class);
		bind(ExcelService.class).toProvider(() -> new ExcelServiceImpl()).in(Singleton.class);
	}
}
