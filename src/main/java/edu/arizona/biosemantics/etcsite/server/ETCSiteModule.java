package edu.arizona.biosemantics.etcsite.server;

//import com.google.gwt.logging.server.RemoteLoggingServiceImpl;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.gwt.logging.server.RemoteLoggingServiceImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

import edu.arizona.biosemantics.common.ling.know.SingularPluralProvider;
import edu.arizona.biosemantics.common.ling.know.lib.WordNetPOSKnowledgeBase;
import edu.arizona.biosemantics.common.ling.transform.IInflector;
import edu.arizona.biosemantics.common.ling.transform.lib.SomeInflector;
import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.server.db.DAOManager;
import edu.arizona.biosemantics.etcsite.server.process.file.XMLValidator;
import edu.arizona.biosemantics.etcsite.server.rpc.auth.AuthenticationService;
import edu.arizona.biosemantics.etcsite.server.rpc.matrixgeneration.MatrixGenerationService;
import edu.arizona.biosemantics.etcsite.server.rpc.ontologize.OntologizeService;
import edu.arizona.biosemantics.etcsite.server.rpc.pipeline.PipelineService;
import edu.arizona.biosemantics.etcsite.server.rpc.file.FileService;
import edu.arizona.biosemantics.etcsite.server.rpc.file.access.FileAccessService;
import edu.arizona.biosemantics.etcsite.server.rpc.file.format.FileFormatService;
import edu.arizona.biosemantics.etcsite.server.rpc.file.permission.FilePermissionService;
import edu.arizona.biosemantics.etcsite.server.rpc.file.search.FileSearchService;
import edu.arizona.biosemantics.etcsite.server.rpc.semanticmarkup.SemanticMarkupService;
import edu.arizona.biosemantics.etcsite.server.rpc.setup.SetupService;
import edu.arizona.biosemantics.etcsite.server.rpc.task.TaskService;
import edu.arizona.biosemantics.etcsite.server.rpc.taxonomycomparison.TaxonomyComparisonService;
import edu.arizona.biosemantics.etcsite.server.rpc.treegeneration.TreeGenerationService;
import edu.arizona.biosemantics.etcsite.server.rpc.user.UserService;
import edu.arizona.biosemantics.etcsite.server.rpc.visualization.VisualizationService;
import edu.arizona.biosemantics.etcsite.server.upload.UploadServlet;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.IAuthenticationService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.IFileService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.access.IFileAccessService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.format.IFileFormatService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.permission.IFilePermissionService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.search.IFileSearchService;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.setup.ISetupService;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.IMatrixGenerationService;
import edu.arizona.biosemantics.etcsite.shared.rpc.ontologize.IOntologizeService;
import edu.arizona.biosemantics.etcsite.shared.rpc.pipeline.IPipelineService;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.ISemanticMarkupService;
import edu.arizona.biosemantics.etcsite.shared.rpc.task.ITaskService;
import edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison.ITaxonomyComparisonService;
import edu.arizona.biosemantics.etcsite.shared.rpc.treegeneration.ITreeGenerationService;
import edu.arizona.biosemantics.etcsite.shared.rpc.user.IUserService;
import edu.arizona.biosemantics.etcsite.shared.rpc.visualization.IVisualizationService;
import edu.arizona.biosemantics.etcsitehelp.server.rpc.help.HelpService;
import edu.arizona.biosemantics.etcsitehelp.shared.rpc.help.IHelpService;

public class ETCSiteModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(HelpService.class).in(Scopes.SINGLETON);
		bind(AuthenticationService.class).in(Scopes.SINGLETON);
		bind(MatrixGenerationService.class).in(Scopes.SINGLETON);
		bind(FileService.class).in(Scopes.SINGLETON);
		bind(FileAccessService.class).in(Scopes.SINGLETON);
		bind(FileSearchService.class).in(Scopes.SINGLETON);
		bind(FilePermissionService.class).in(Scopes.SINGLETON);
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
		bind(edu.arizona.biosemantics.oto2.ontologize2.server.CollectionService.class).in(Scopes.SINGLETON);
		bind(edu.arizona.biosemantics.oto2.ontologize2.server.UserLogService.class).in(Scopes.SINGLETON);
		bind(edu.arizona.biosemantics.oto2.ontologize2.server.ContextService.class).in(Scopes.SINGLETON);
		bind(edu.arizona.biosemantics.oto2.oto.server.rpc.CollectionService.class).in(Scopes.SINGLETON);
		bind(edu.arizona.biosemantics.oto2.oto.server.rpc.CommunityService.class).in(Scopes.SINGLETON);
		bind(edu.arizona.biosemantics.oto2.oto.server.rpc.ContextService.class).in(Scopes.SINGLETON);
		bind(edu.arizona.biosemantics.oto2.oto.server.rpc.OntologyService.class).in(Scopes.SINGLETON);
		bind(edu.ucdavis.cs.cfgproject.server.rpc.KeyGenerationService.class).in(Scopes.SINGLETON);
		bind(edu.arizona.biosemantics.euler.alignment.server.EulerAlignmentService.class).in(Scopes.SINGLETON);
		bind(edu.arizona.biosemantics.matrixreview.server.MatrixService.class).in(Scopes.SINGLETON);
		bind(GoogleAuthenticationServlet.class).in(Scopes.SINGLETON);
		bind(PDFServlet.class).in(Scopes.SINGLETON);
		bind(DownloadServlet.class).in(Scopes.SINGLETON);
		bind(UploadServlet.class).in(Scopes.SINGLETON);
		bind(RemoteLoggingServiceImpl.class).in(Scopes.SINGLETON);
		
		bind(IHelpService.class).to(HelpService.class).in(Scopes.SINGLETON);
		bind(IFileService.class).to(FileService.class).in(Scopes.SINGLETON);
		bind(IAuthenticationService.class).to(AuthenticationService.class).in(Scopes.SINGLETON);
		bind(IMatrixGenerationService.class).to(MatrixGenerationService.class).in(Scopes.SINGLETON);
		bind(IFileAccessService.class).to(FileAccessService.class).in(Scopes.SINGLETON);
		bind(IFileSearchService.class).to(FileSearchService.class).in(Scopes.SINGLETON);
		bind(IFilePermissionService.class).to(FilePermissionService.class).in(Scopes.SINGLETON);
		bind(ITaskService.class).to(TaskService.class).in(Scopes.SINGLETON);
		bind(ISemanticMarkupService.class).to(SemanticMarkupService.class).in(Scopes.SINGLETON);
		bind(IFileFormatService.class).to(FileFormatService.class).in(Scopes.SINGLETON);
		bind(IUserService.class).to(UserService.class).in(Scopes.SINGLETON);
		bind(ISetupService.class).to(SetupService.class).in(Scopes.SINGLETON);
		bind(IPipelineService.class).to(PipelineService.class).in(Scopes.SINGLETON);
		bind(IVisualizationService.class).to(VisualizationService.class).in(Scopes.SINGLETON);
		bind(ITaxonomyComparisonService.class).to(TaxonomyComparisonService.class).in(Scopes.SINGLETON);
		bind(IOntologizeService.class).to(OntologizeService.class).in(Scopes.SINGLETON);
		bind(ITreeGenerationService.class).to(TreeGenerationService.class).in(Scopes.SINGLETON);
		bind(edu.arizona.biosemantics.oto2.ontologize2.shared.ICollectionService.class).to(
				edu.arizona.biosemantics.oto2.ontologize2.server.CollectionService.class).in(Scopes.SINGLETON);
		bind(edu.arizona.biosemantics.oto2.ontologize2.shared.IContextService.class).to(
				edu.arizona.biosemantics.oto2.ontologize2.server.ContextService.class).in(Scopes.SINGLETON);
		bind(edu.arizona.biosemantics.oto2.oto.shared.rpc.ICollectionService.class).to(
				edu.arizona.biosemantics.oto2.oto.server.rpc.CollectionService.class).in(Scopes.SINGLETON);
		bind(edu.arizona.biosemantics.oto2.oto.shared.rpc.ICommunityService.class).to(
				edu.arizona.biosemantics.oto2.oto.server.rpc.CommunityService.class).in(Scopes.SINGLETON);
		bind(edu.arizona.biosemantics.oto2.oto.shared.rpc.IContextService.class).to(
				edu.arizona.biosemantics.oto2.oto.server.rpc.ContextService.class).in(Scopes.SINGLETON);
		bind(edu.arizona.biosemantics.oto2.oto.shared.rpc.IOntologyService.class).to(
				edu.arizona.biosemantics.oto2.oto.server.rpc.OntologyService.class).in(Scopes.SINGLETON);
		bind(edu.ucdavis.cs.cfgproject.shared.rpc.IKeyGenerationService.class).to(
				edu.ucdavis.cs.cfgproject.server.rpc.KeyGenerationService.class).in(Scopes.SINGLETON);
		bind(edu.arizona.biosemantics.euler.alignment.shared.IEulerAlignmentService.class).to(
				edu.arizona.biosemantics.euler.alignment.server.EulerAlignmentService.class).in(Scopes.SINGLETON);
		bind(edu.arizona.biosemantics.matrixreview.shared.IMatrixService.class).to(
				edu.arizona.biosemantics.matrixreview.server.MatrixService.class).in(Scopes.SINGLETON);
		
		XMLValidator taxonDescriptionValidator = new XMLValidator(new File(Configuration.taxonDescriptionSchemaFile));
		XMLValidator markedUpTaxonDescriptionValidator = new XMLValidator(new File(Configuration.markedUpTaxonDescriptionSchemaFile));
		bind(XMLValidator.class).annotatedWith(Names.named("TaxonDescription")).toInstance(taxonDescriptionValidator);
		bind(XMLValidator.class).annotatedWith(Names.named("MarkedUpTaxonDescription")).toInstance(markedUpTaxonDescriptionValidator);
		bind(DAOManager.class).in(Scopes.SINGLETON);
		bind(Emailer.class).in(Scopes.SINGLETON);
		
		WordNetPOSKnowledgeBase wordNetPOSKnowledgeBase = null;
		try {
			wordNetPOSKnowledgeBase = new WordNetPOSKnowledgeBase(Configuration.charaparser_wordnet, false);
			final SingularPluralProvider singularPluralProvider = new SingularPluralProvider();
			final SomeInflector someInflector = new SomeInflector(wordNetPOSKnowledgeBase, 
					singularPluralProvider.getSingulars(), singularPluralProvider.getPlurals());
			bind(IInflector.class).toInstance(someInflector);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
