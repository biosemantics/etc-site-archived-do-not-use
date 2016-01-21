package edu.arizona.biosemantics.etcsite.filemanager.server;

import com.google.code.kaptcha.servlet.KaptchaServlet;
import com.google.gwt.logging.server.RemoteLoggingServiceImpl;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;

import edu.arizona.biosemantics.etcsite.core.server.GoogleAuthenticationServlet;
import edu.arizona.biosemantics.etcsite.core.server.rpc.auth.AuthenticationService;
import edu.arizona.biosemantics.etcsite.core.server.rpc.setup.SetupService;
import edu.arizona.biosemantics.etcsite.core.server.rpc.task.TaskService;
import edu.arizona.biosemantics.etcsite.core.server.rpc.user.UserService;
import edu.arizona.biosemantics.etcsite.filemanager.server.rpc.FileAccessService;
import edu.arizona.biosemantics.etcsite.filemanager.server.rpc.FileFormatService;
import edu.arizona.biosemantics.etcsite.filemanager.server.rpc.FilePermissionService;
import edu.arizona.biosemantics.etcsite.filemanager.server.rpc.FileSearchService;
import edu.arizona.biosemantics.etcsite.filemanager.server.rpc.FileService;
import edu.arizona.biosemantics.etcsite.filemanager.server.upload.UploadServlet;

public class GuiceServletConfig extends GuiceServletContextListener {

	@Override
	protected Injector getInjector() {
		return Guice.createInjector(new ServletModule() {
			/* http://www.gwtproject.org/doc/latest/DevGuideServerCommunication.html#DevGuideImplementingServices 
				-> Common pitfalls: for url-pattern help */
			@Override
			protected void configureServlets() {
				//serve("/filemanager/auth").with(AuthenticationService.class);
				serve("/filemanager/fileService").with(FileService.class);
				serve("/filemanager/fileAccess").with(FileAccessService.class);
				serve("/filemanager/fileSearch").with(FileSearchService.class);
				serve("/filemanager/filePermission").with(FilePermissionService.class);
				//serve("/filemanager/task").with(TaskService.class);
				serve("/filemanager/fileFormat").with(FileFormatService.class);
				serve("/filemanager/user").with(UserService.class);
				serve("/filemanager/setup").with(SetupService.class);
				//serve("/filemanager/captcha/*").with(KaptchaServlet.class);
				serve("/googleauth").with(GoogleAuthenticationServlet.class);
				serve("*.gpdf").with(PDFServlet.class);
				serve("*.dld").with(DownloadServlet.class);
				serve("*.gupld").with(UploadServlet.class);
				serve("/filemanager/logging").with(RemoteLoggingServiceImpl.class);
			}
			
		}, new FileManagerModule());
	}
}