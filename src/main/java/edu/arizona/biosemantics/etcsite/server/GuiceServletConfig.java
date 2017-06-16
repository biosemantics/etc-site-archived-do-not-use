package edu.arizona.biosemantics.etcsite.server;

import com.google.gwt.logging.server.RemoteLoggingServiceImpl;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;

import edu.arizona.biosemantics.etcsite.server.rpc.auth.AuthenticationService;
import edu.arizona.biosemantics.etcsite.server.rpc.file.FileService;
import edu.arizona.biosemantics.etcsite.server.rpc.file.access.FileAccessService;
import edu.arizona.biosemantics.etcsite.server.rpc.file.format.FileFormatService;
import edu.arizona.biosemantics.etcsite.server.rpc.file.permission.FilePermissionService;
import edu.arizona.biosemantics.etcsite.server.rpc.file.search.FileSearchService;
import edu.arizona.biosemantics.etcsite.server.rpc.matrixgeneration.MatrixGenerationService;
import edu.arizona.biosemantics.etcsite.server.rpc.ontologize.OntologizeService;
import edu.arizona.biosemantics.etcsite.server.rpc.semanticmarkup.SemanticMarkupService;
import edu.arizona.biosemantics.etcsite.server.rpc.setup.SetupService;
import edu.arizona.biosemantics.etcsite.server.rpc.task.TaskService;
import edu.arizona.biosemantics.etcsite.server.rpc.taxonomycomparison.TaxonomyComparisonService;
import edu.arizona.biosemantics.etcsite.server.rpc.treegeneration.TreeGenerationService;
import edu.arizona.biosemantics.etcsite.server.rpc.user.UserService;
import edu.arizona.biosemantics.etcsite.server.rpc.visualization.VisualizationService;
import edu.arizona.biosemantics.etcsite.server.upload.UploadServlet;
import edu.arizona.biosemantics.etcsitehelp.server.rpc.help.HelpService;
import edu.arizona.biosemantics.oto2.ontologize2.server.UserLogService;

public class GuiceServletConfig extends GuiceServletContextListener {

	@Override
	protected Injector getInjector() {
		return Guice.createInjector(new ServletModule() {
			/* http://www.gwtproject.org/doc/latest/DevGuideServerCommunication.html#DevGuideImplementingServices 
				-> Common pitfalls: for url-pattern help */
			@Override
			protected void configureServlets() {
				serve("/help").with(HelpService.class);
				serve("/etcsite/auth").with(AuthenticationService.class);
				serve("/etcsite/file").with(FileService.class);
				serve("/etcsite/fileAccess").with(FileAccessService.class);
				serve("/etcsite/fileSearch").with(FileSearchService.class);
				serve("/etcsite/filePermission").with(FilePermissionService.class);
				serve("/etcsite/task").with(TaskService.class);
				serve("/etcsite/semanticMarkup").with(SemanticMarkupService.class);
				serve("/etcsite/fileFormat").with(FileFormatService.class);
				serve("/etcsite/user").with(UserService.class);
				serve("/etcsite/setup").with(SetupService.class);
				serve("/etcsite/visualization").with(VisualizationService.class);
				serve("/etcsite/treeGeneration").with(TreeGenerationService.class);
				serve("/etcsite/taxonomyComparison").with(TaxonomyComparisonService.class);
				serve("/etcsite/matrixGeneration").with(MatrixGenerationService.class);
				serve("/etcsite/ontologize").with(OntologizeService.class);
				serve("/etcsite/captcha/*").with(KaptchaServlet.class);
				serve("/etcsite/ontologize2_collection").with(edu.arizona.biosemantics.oto2.ontologize2.server.CollectionService.class);
				serve("/etcsite/ontologize2_userlog").with(edu.arizona.biosemantics.oto2.ontologize2.server.UserLogService.class);
				serve("/etcsite/ontologize2_context").with(edu.arizona.biosemantics.oto2.ontologize2.server.ContextService.class);
				serve("/etcsite/oto_collection").with(edu.arizona.biosemantics.oto2.oto.server.rpc.CollectionService.class);
				serve("/etcsite/oto_community").with(edu.arizona.biosemantics.oto2.oto.server.rpc.CommunityService.class);
				serve("/etcsite/oto_context").with(edu.arizona.biosemantics.oto2.oto.server.rpc.ContextService.class);
				serve("/etcsite/oto_ontology").with(edu.arizona.biosemantics.oto2.oto.server.rpc.OntologyService.class);
				serve("/etcsite/keyGeneration").with(edu.ucdavis.cs.cfgproject.server.rpc.KeyGenerationService.class);
				serve("/etcsite/alignment").with(edu.arizona.biosemantics.euler.alignment.server.EulerAlignmentService.class);
				serve("/etcsite/matrix").with(edu.arizona.biosemantics.matrixreview.server.MatrixService.class);
				serve("/googleauth").with(GoogleAuthenticationServlet.class);
				serve("*.gpdf").with(PDFServlet.class);
				serve("*.dld").with(DownloadServlet.class);
				serve("*.gupld").with(UploadServlet.class);
				serve("/etcsite/logging").with(RemoteLoggingServiceImpl.class);
			}
			
		}, new ETCSiteModule());
	}
}