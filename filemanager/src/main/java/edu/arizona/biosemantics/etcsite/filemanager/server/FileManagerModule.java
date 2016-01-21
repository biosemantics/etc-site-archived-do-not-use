package edu.arizona.biosemantics.etcsite.filemanager.server;

//import com.google.gwt.logging.server.RemoteLoggingServiceImpl;
import java.io.File;

import com.google.gwt.logging.server.RemoteLoggingServiceImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

import edu.arizona.biosemantics.etcsite.core.server.Emailer;
import edu.arizona.biosemantics.etcsite.core.server.GoogleAuthenticationServlet;
import edu.arizona.biosemantics.etcsite.core.server.db.DAOManager;
import edu.arizona.biosemantics.etcsite.core.server.rpc.setup.SetupService;
import edu.arizona.biosemantics.etcsite.core.server.rpc.user.UserService;
import edu.arizona.biosemantics.etcsite.core.shared.rpc.setup.ISetupService;
import edu.arizona.biosemantics.etcsite.core.shared.rpc.user.IUserService;
import edu.arizona.biosemantics.etcsite.filemanager.server.process.validate.XMLValidator;
import edu.arizona.biosemantics.etcsite.filemanager.server.rpc.FileAccessService;
import edu.arizona.biosemantics.etcsite.filemanager.server.rpc.FileFormatService;
import edu.arizona.biosemantics.etcsite.filemanager.server.rpc.FilePermissionService;
import edu.arizona.biosemantics.etcsite.filemanager.server.rpc.FileSearchService;
import edu.arizona.biosemantics.etcsite.filemanager.server.rpc.FileService;
import edu.arizona.biosemantics.etcsite.filemanager.server.upload.UploadServlet;
import edu.arizona.biosemantics.etcsite.filemanager.shared.rpc.IFileAccessService;
import edu.arizona.biosemantics.etcsite.filemanager.shared.rpc.IFileFormatService;
import edu.arizona.biosemantics.etcsite.filemanager.shared.rpc.IFilePermissionService;
import edu.arizona.biosemantics.etcsite.filemanager.shared.rpc.IFileSearchService;
import edu.arizona.biosemantics.etcsite.filemanager.shared.rpc.IFileService;

public class FileManagerModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(FileService.class).in(Scopes.SINGLETON);
		bind(FileAccessService.class).in(Scopes.SINGLETON);
		bind(FileFormatService.class).in(Scopes.SINGLETON);
		bind(FilePermissionService.class).in(Scopes.SINGLETON);
		bind(FileSearchService.class).in(Scopes.SINGLETON);
		bind(PDFServlet.class).in(Scopes.SINGLETON);
		bind(DownloadServlet.class).in(Scopes.SINGLETON);
		bind(UploadServlet.class).in(Scopes.SINGLETON);
		bind(RemoteLoggingServiceImpl.class).in(Scopes.SINGLETON);
		bind(UserService.class).in(Scopes.SINGLETON);
		bind(SetupService.class).in(Scopes.SINGLETON);
		bind(GoogleAuthenticationServlet.class).in(Scopes.SINGLETON);
		
		bind(IFileService.class).to(FileService.class).in(Scopes.SINGLETON);
		//bind(IAuthenticationService.class).to(AuthenticationService.class).in(Scopes.SINGLETON);
		bind(IFileAccessService.class).to(FileAccessService.class).in(Scopes.SINGLETON);
		bind(IFileSearchService.class).to(FileSearchService.class).in(Scopes.SINGLETON);
		bind(IFilePermissionService.class).to(FilePermissionService.class).in(Scopes.SINGLETON);
		//bind(ITaskService.class).to(TaskService.class).in(Scopes.SINGLETON);
		bind(IFileFormatService.class).to(FileFormatService.class).in(Scopes.SINGLETON);
		bind(IUserService.class).to(UserService.class).in(Scopes.SINGLETON);
		bind(ISetupService.class).to(SetupService.class).in(Scopes.SINGLETON);
		
		XMLValidator taxonDescriptionValidator = new XMLValidator(new File(Configuration.taxonDescriptionSchemaFile));
		XMLValidator markedUpTaxonDescriptionValidator = new XMLValidator(new File(Configuration.markedUpTaxonDescriptionSchemaFile));
		bind(XMLValidator.class).annotatedWith(Names.named("TaxonDescription")).toInstance(taxonDescriptionValidator);
		bind(XMLValidator.class).annotatedWith(Names.named("MarkedUpTaxonDescription")).toInstance(markedUpTaxonDescriptionValidator);
		bind(DAOManager.class).in(Scopes.SINGLETON);
		bind(Emailer.class).in(Scopes.SINGLETON);
	}

}
