package edu.arizona.biosemantics.etcsite.server;

//import com.google.gwt.logging.server.RemoteLoggingServiceImpl;
import java.io.File;

import com.google.gwt.logging.server.RemoteLoggingServiceImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

import edu.arizona.biosemantics.etcsite.server.db.DAOManager;
import edu.arizona.biosemantics.etcsite.server.process.file.XMLValidator;
import edu.arizona.biosemantics.etcsite.server.rpc.auth.AuthenticationService;
import edu.arizona.biosemantics.etcsite.server.rpc.matrixgeneration.MatrixGenerationService;
import edu.arizona.biosemantics.etcsite.server.rpc.ontologize.OntologizeService;
import edu.arizona.biosemantics.etcsite.server.rpc.pipeline.PipelineService;
import edu.arizona.biosemantics.etcsite.server.rpc.file.FileService;
import edu.arizona.biosemantics.etcsite.server.rpc.file.access.FileAccessService;
import edu.arizona.biosemantics.etcsite.server.rpc.file.format.FileFormatService;
import edu.arizona.biosemantics.etcsite.server.rpc.file.search.FileSearchService;
import edu.arizona.biosemantics.etcsite.server.rpc.semanticmarkup.SemanticMarkupService;
import edu.arizona.biosemantics.etcsite.server.rpc.setup.SetupService;
import edu.arizona.biosemantics.etcsite.server.rpc.task.TaskService;
import edu.arizona.biosemantics.etcsite.server.rpc.taxonomycomparison.TaxonomyComparisonService;
import edu.arizona.biosemantics.etcsite.server.rpc.treegeneration.TreeGenerationService;
import edu.arizona.biosemantics.etcsite.server.rpc.user.UserService;
import edu.arizona.biosemantics.etcsite.server.rpc.visualization.VisualizationService;
import edu.arizona.biosemantics.etcsite.server.upload.UploadServlet;

public class ETCSiteModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(AuthenticationService.class).in(Scopes.SINGLETON);
		bind(MatrixGenerationService.class).in(Scopes.SINGLETON);
		bind(FileService.class).in(Scopes.SINGLETON);
		bind(FileAccessService.class).in(Scopes.SINGLETON);
		bind(FileSearchService.class).in(Scopes.SINGLETON);
		bind(TaskService.class).in(Scopes.SINGLETON);
		bind(SemanticMarkupService.class).in(Scopes.SINGLETON);
		bind(FileFormatService.class).in(Scopes.SINGLETON);
		bind(UserService.class).in(Scopes.SINGLETON);
		bind(SetupService.class).in(Scopes.SINGLETON);
		bind(PipelineService.class).in(Scopes.SINGLETON);
		bind(VisualizationService.class).in(Scopes.SINGLETON);
		bind(TaxonomyComparisonService.class).in(Scopes.SINGLETON);
		bind(OntologizeService.class).in(Scopes.SINGLETON);
		bind(TreeGenerationService.class).in(Scopes.SINGLETON);
		bind(KaptchaServlet.class).in(Scopes.SINGLETON);
		bind(edu.arizona.biosemantics.oto2.ontologize.server.rpc.CollectionService.class).in(Scopes.SINGLETON);
		bind(edu.arizona.biosemantics.oto2.ontologize.server.rpc.ToOntologyService.class).in(Scopes.SINGLETON);
		bind(edu.arizona.biosemantics.oto2.ontologize.server.rpc.ContextService.class).in(Scopes.SINGLETON);
		bind(edu.arizona.biosemantics.oto2.oto.server.rpc.CollectionService.class).in(Scopes.SINGLETON);
		bind(edu.arizona.biosemantics.oto2.oto.server.rpc.CommunityService.class).in(Scopes.SINGLETON);
		bind(edu.arizona.biosemantics.oto2.oto.server.rpc.ContextService.class).in(Scopes.SINGLETON);
		bind(edu.arizona.biosemantics.oto2.oto.server.rpc.OntologyService.class).in(Scopes.SINGLETON);
		bind(edu.ucdavis.cs.cfgproject.server.rpc.KeyGenerationService.class).in(Scopes.SINGLETON);
		bind(edu.arizona.biosemantics.euler.alignment.server.EulerAlignmentService.class).in(Scopes.SINGLETON);
		bind(GoogleAuthenticationServlet.class).in(Scopes.SINGLETON);
		bind(PDFServlet.class).in(Scopes.SINGLETON);
		bind(DownloadServlet.class).in(Scopes.SINGLETON);
		bind(HelpServlet.class).in(Scopes.SINGLETON);
		bind(UploadServlet.class).in(Scopes.SINGLETON);
		bind(RemoteLoggingServiceImpl.class).in(Scopes.SINGLETON);
		
		bind(DAOManager.class).in(Scopes.SINGLETON);
		bind(Emailer.class).in(Scopes.SINGLETON);
		
		XMLValidator taxonDescriptionValidator = new XMLValidator(new File(Configuration.taxonDescriptionSchemaFile));
		XMLValidator markedUpTaxonDescriptionValidator = new XMLValidator(new File(Configuration.markedUpTaxonDescriptionSchemaFile));
		bind(XMLValidator.class).annotatedWith(Names.named("TaxonDescription")).toInstance(taxonDescriptionValidator);
		bind(XMLValidator.class).annotatedWith(Names.named("MarkedUpTaxonDescription")).toInstance(markedUpTaxonDescriptionValidator);
		
	}

}
