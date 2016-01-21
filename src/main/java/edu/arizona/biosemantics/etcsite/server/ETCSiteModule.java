package edu.arizona.biosemantics.etcsite.server;

//import com.google.gwt.logging.server.RemoteLoggingServiceImpl;
import java.io.File;

import com.google.gwt.logging.server.RemoteLoggingServiceImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

import edu.arizona.biosemantics.etcsite.core.server.Emailer;
import edu.arizona.biosemantics.etcsite.core.server.GoogleAuthenticationServlet;
import edu.arizona.biosemantics.etcsite.core.server.db.DAOManager;
import edu.arizona.biosemantics.etcsite.core.server.rpc.auth.AuthenticationService;
import edu.arizona.biosemantics.etcsite.core.server.rpc.setup.SetupService;
import edu.arizona.biosemantics.etcsite.core.server.rpc.task.TaskService;
import edu.arizona.biosemantics.etcsite.core.server.rpc.user.UserService;
import edu.arizona.biosemantics.etcsite.core.shared.rpc.IHasTasksService;
import edu.arizona.biosemantics.etcsite.core.shared.rpc.auth.IAuthenticationService;
import edu.arizona.biosemantics.etcsite.core.shared.rpc.setup.ISetupService;
import edu.arizona.biosemantics.etcsite.core.shared.rpc.task.ITaskService;
import edu.arizona.biosemantics.etcsite.core.shared.rpc.user.IUserService;
import edu.arizona.biosemantics.etcsite.etcsitehelp.server.rpc.help.HelpService;
import edu.arizona.biosemantics.etcsite.etcsitehelp.shared.rpc.help.IHelpService;
import edu.arizona.biosemantics.etcsite.filemanager.server.DownloadServlet;
import edu.arizona.biosemantics.etcsite.filemanager.server.PDFServlet;
import edu.arizona.biosemantics.etcsite.filemanager.server.rpc.FileAccessService;
import edu.arizona.biosemantics.etcsite.filemanager.server.rpc.FileFormatService;
import edu.arizona.biosemantics.etcsite.filemanager.server.rpc.FilePermissionService;
import edu.arizona.biosemantics.etcsite.filemanager.server.rpc.FileSearchService;
import edu.arizona.biosemantics.etcsite.filemanager.server.rpc.FileService;
import edu.arizona.biosemantics.etcsite.filemanager.server.rpc.XMLValidator;
import edu.arizona.biosemantics.etcsite.filemanager.server.upload.UploadServlet;
import edu.arizona.biosemantics.etcsite.filemanager.shared.rpc.IFileAccessService;
import edu.arizona.biosemantics.etcsite.filemanager.shared.rpc.IFileFormatService;
import edu.arizona.biosemantics.etcsite.filemanager.shared.rpc.IFilePermissionService;
import edu.arizona.biosemantics.etcsite.filemanager.shared.rpc.IFileSearchService;
import edu.arizona.biosemantics.etcsite.filemanager.shared.rpc.IFileService;
import edu.arizona.biosemantics.etcsite.server.rpc.matrixgeneration.MatrixGenerationService;
import edu.arizona.biosemantics.etcsite.server.rpc.ontologize.OntologizeService;
import edu.arizona.biosemantics.etcsite.server.rpc.pipeline.PipelineService;
import edu.arizona.biosemantics.etcsite.server.rpc.semanticmarkup.SemanticMarkupService;
import edu.arizona.biosemantics.etcsite.server.rpc.taxonomycomparison.TaxonomyComparisonService;
import edu.arizona.biosemantics.etcsite.server.rpc.treegeneration.TreeGenerationService;
import edu.arizona.biosemantics.etcsite.server.rpc.visualization.VisualizationService;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.IMatrixGenerationService;
import edu.arizona.biosemantics.etcsite.shared.rpc.ontologize.IOntologizeService;
import edu.arizona.biosemantics.etcsite.shared.rpc.pipeline.IPipelineService;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.ISemanticMarkupService;
import edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison.ITaxonomyComparisonService;
import edu.arizona.biosemantics.etcsite.shared.rpc.treegeneration.ITreeGenerationService;
import edu.arizona.biosemantics.etcsite.shared.rpc.visualization.IVisualizationService;

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
		bind(UploadServlet.class).in(Scopes.SINGLETON);
		bind(RemoteLoggingServiceImpl.class).in(Scopes.SINGLETON);
		bind(IHasTasksService.class).annotatedWith(Names.named("MatrixGeneration")).to(MatrixGenerationService.class);
		bind(IHasTasksService.class).annotatedWith(Names.named("SemanticMarkup")).to(SemanticMarkupService.class);
		bind(IHasTasksService.class).annotatedWith(Names.named("TreeGeneration")).to(TreeGenerationService.class);
		bind(IHasTasksService.class).annotatedWith(Names.named("Ontologize")).to(OntologizeService.class);
		bind(IHasTasksService.class).annotatedWith(Names.named("TaxonomyComparison")).to(MatrixGenerationService.class);
		
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
		bind(edu.arizona.biosemantics.oto2.ontologize.shared.rpc.ICollectionService.class).to(
				edu.arizona.biosemantics.oto2.ontologize.server.rpc.CollectionService.class).in(Scopes.SINGLETON);
		bind(edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyService.class).to(
				edu.arizona.biosemantics.oto2.ontologize.server.rpc.ToOntologyService.class).in(Scopes.SINGLETON);
		bind(edu.arizona.biosemantics.oto2.ontologize.shared.rpc.IContextService.class).to(
				edu.arizona.biosemantics.oto2.ontologize.server.rpc.ContextService.class).in(Scopes.SINGLETON);
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
		
		XMLValidator taxonDescriptionValidator = new XMLValidator(new File(Configuration.taxonDescriptionSchemaFile));
		XMLValidator markedUpTaxonDescriptionValidator = new XMLValidator(new File(Configuration.markedUpTaxonDescriptionSchemaFile));
		bind(XMLValidator.class).annotatedWith(Names.named("TaxonDescription")).toInstance(taxonDescriptionValidator);
		bind(XMLValidator.class).annotatedWith(Names.named("MarkedUpTaxonDescription")).toInstance(markedUpTaxonDescriptionValidator);
		bind(DAOManager.class).in(Scopes.SINGLETON);
		bind(Emailer.class).in(Scopes.SINGLETON);
		
	}

}
