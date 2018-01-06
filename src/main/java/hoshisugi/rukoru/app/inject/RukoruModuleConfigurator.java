package hoshisugi.rukoru.app.inject;

import com.google.inject.Singleton;

import hoshisugi.rukoru.app.services.auth.AuthService;
import hoshisugi.rukoru.app.services.auth.AuthServiceImpl;
import hoshisugi.rukoru.app.services.ec2.EC2Service;
import hoshisugi.rukoru.app.services.ec2.EC2ServiceImpl;
import hoshisugi.rukoru.app.services.s3.S3Service;
import hoshisugi.rukoru.app.services.s3.S3ServiceImpl;
import hoshisugi.rukoru.app.view.ContentController;
import hoshisugi.rukoru.app.view.MainController;
import hoshisugi.rukoru.app.view.ToolBarController;
import hoshisugi.rukoru.app.view.content.EC2ContentController;
import hoshisugi.rukoru.app.view.content.ImageTabController;
import hoshisugi.rukoru.app.view.content.InstanceTabController;
import hoshisugi.rukoru.app.view.content.RepositoryDBContentController;
import hoshisugi.rukoru.app.view.content.S3ExplorerTableController;
import hoshisugi.rukoru.app.view.content.S3ExplorerController;
import hoshisugi.rukoru.app.view.content.S3ExplorerMenuController;
import hoshisugi.rukoru.app.view.content.S3ExplorerTreeController;
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
		provide(EC2ContentController.class);
		provide(RepositoryDBContentController.class);
		provide(S3ExplorerController.class);
		provide(InstanceTabController.class);
		provide(ImageTabController.class);
		provide(S3ExplorerMenuController.class);
		provide(S3ExplorerTreeController.class);
		provide(S3ExplorerTableController.class);

	}

	private void configureServices() {
		bind(AuthService.class).toProvider(() -> new AuthServiceImpl()).in(Singleton.class);
		bind(EC2Service.class).toProvider(() -> new EC2ServiceImpl()).in(Singleton.class);
		bind(S3Service.class).toProvider(() -> new S3ServiceImpl()).in(Singleton.class);
	}
}
