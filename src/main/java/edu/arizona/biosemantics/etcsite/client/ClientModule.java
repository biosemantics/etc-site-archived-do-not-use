package edu.arizona.biosemantics.etcsite.client;

import com.google.gwt.activity.shared.MyActivityManager;
import com.google.gwt.activity.shared.MyActivityMapper;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

import edu.arizona.biosemantics.etcsite.client.common.IInputCreateView;
import edu.arizona.biosemantics.etcsite.client.common.ILoginView;
import edu.arizona.biosemantics.etcsite.client.common.IRegisterView;
import edu.arizona.biosemantics.etcsite.client.common.IResetPasswordView;
import edu.arizona.biosemantics.etcsite.client.common.InputCreatePresenter;
import edu.arizona.biosemantics.etcsite.client.common.InputCreateView;
import edu.arizona.biosemantics.etcsite.client.common.LoginPresenter;
import edu.arizona.biosemantics.etcsite.client.common.LoginView;
import edu.arizona.biosemantics.etcsite.client.common.RegisterPresenter;
import edu.arizona.biosemantics.etcsite.client.common.RegisterView;
import edu.arizona.biosemantics.etcsite.client.common.ResetPasswordPresenter;
import edu.arizona.biosemantics.etcsite.client.common.ResetPasswordView;
import edu.arizona.biosemantics.etcsite.client.common.files.CreateSemanticMarkupFilesDialogPresenter;
import edu.arizona.biosemantics.etcsite.client.common.files.CreateSemanticMarkupFilesDialogView;
import edu.arizona.biosemantics.etcsite.client.common.files.CreateSemanticMarkupFilesPresenter;
import edu.arizona.biosemantics.etcsite.client.common.files.CreateSemanticMarkupFilesView;
import edu.arizona.biosemantics.etcsite.client.common.files.FileContentPresenter;
import edu.arizona.biosemantics.etcsite.client.common.files.FileContentView;
import edu.arizona.biosemantics.etcsite.client.common.files.FilePathShortener;
import edu.arizona.biosemantics.etcsite.client.common.files.FileTreePresenter;
import edu.arizona.biosemantics.etcsite.client.common.files.FileTreeView;
import edu.arizona.biosemantics.etcsite.client.common.files.ICreateSemanticMarkupFilesDialogView;
import edu.arizona.biosemantics.etcsite.client.common.files.ICreateSemanticMarkupFilesView;
import edu.arizona.biosemantics.etcsite.client.common.files.IFileContentView;
import edu.arizona.biosemantics.etcsite.client.common.files.IFileTreeView;
import edu.arizona.biosemantics.etcsite.client.common.files.IManagableFileTreeView;
import edu.arizona.biosemantics.etcsite.client.common.files.ISelectableFileTreeView;
import edu.arizona.biosemantics.etcsite.client.common.files.ManagableFileTreePresenter;
import edu.arizona.biosemantics.etcsite.client.common.files.ManagableFileTreeView;
import edu.arizona.biosemantics.etcsite.client.common.files.SelectableFileTreePresenter;
import edu.arizona.biosemantics.etcsite.client.common.files.SelectableFileTreeView;
import edu.arizona.biosemantics.etcsite.client.content.about.AboutView;
import edu.arizona.biosemantics.etcsite.client.content.about.IAboutView;
import edu.arizona.biosemantics.etcsite.client.content.annotationReview.AnnotationReviewPresenter;
import edu.arizona.biosemantics.etcsite.client.content.annotationReview.AnnotationReviewView;
import edu.arizona.biosemantics.etcsite.client.content.annotationReview.IAnnotationReviewView;
import edu.arizona.biosemantics.etcsite.client.content.annotationReview.IResultView;
import edu.arizona.biosemantics.etcsite.client.content.annotationReview.ISearchView;
import edu.arizona.biosemantics.etcsite.client.content.annotationReview.IXMLEditorView;
import edu.arizona.biosemantics.etcsite.client.content.annotationReview.ResultPresenter;
import edu.arizona.biosemantics.etcsite.client.content.annotationReview.ResultView;
import edu.arizona.biosemantics.etcsite.client.content.annotationReview.SearchPresenter;
import edu.arizona.biosemantics.etcsite.client.content.annotationReview.SearchView;
import edu.arizona.biosemantics.etcsite.client.content.annotationReview.XMLEditorPresenter;
import edu.arizona.biosemantics.etcsite.client.content.annotationReview.XMLEditorView;
import edu.arizona.biosemantics.etcsite.client.content.fileManager.FileManagerDialogPresenter;
import edu.arizona.biosemantics.etcsite.client.content.fileManager.FileManagerDialogView;
import edu.arizona.biosemantics.etcsite.client.content.fileManager.FileManagerPresenter;
import edu.arizona.biosemantics.etcsite.client.content.fileManager.FileManagerView;
import edu.arizona.biosemantics.etcsite.client.content.fileManager.IFileManagerDialogView;
import edu.arizona.biosemantics.etcsite.client.content.fileManager.IFileManagerView;
import edu.arizona.biosemantics.etcsite.client.content.gettingstarted.GettingStartedView;
import edu.arizona.biosemantics.etcsite.client.content.gettingstarted.IGettingStartedView;
import edu.arizona.biosemantics.etcsite.client.content.home.HomeHeaderView;
import edu.arizona.biosemantics.etcsite.client.content.home.HomeMainView;
import edu.arizona.biosemantics.etcsite.client.content.home.HomePlace;
import edu.arizona.biosemantics.etcsite.client.content.home.HomeView;
import edu.arizona.biosemantics.etcsite.client.content.home.IHomeHeaderView;
import edu.arizona.biosemantics.etcsite.client.content.home.IHomeMainView;
import edu.arizona.biosemantics.etcsite.client.content.home.IHomeView;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.IMatrixGenerationInputView;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.IMatrixGenerationDefineView;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.IMatrixGenerationOutputView;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.IMatrixGenerationProcessView;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.IMatrixGenerationReviewView;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationInputPresenter;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationInputView;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationDefinePresenter;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationDefineView;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationOutputPresenter;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationOutputView;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationProcessPresenter;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationProcessView;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationReviewPresenter;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationReviewView;
import edu.arizona.biosemantics.etcsite.client.content.news.INewsView;
import edu.arizona.biosemantics.etcsite.client.content.news.NewsView;
import edu.arizona.biosemantics.etcsite.client.content.ontologize.IOntologizeBuildView;
import edu.arizona.biosemantics.etcsite.client.content.ontologize.IOntologizeInputView;
import edu.arizona.biosemantics.etcsite.client.content.ontologize.IOntologizeDefineView;
import edu.arizona.biosemantics.etcsite.client.content.ontologize.IOntologizeOutputView;
import edu.arizona.biosemantics.etcsite.client.content.ontologize.OntologizeBuildPresenter;
import edu.arizona.biosemantics.etcsite.client.content.ontologize.OntologizeBuildView;
import edu.arizona.biosemantics.etcsite.client.content.ontologize.OntologizeInputPresenter;
import edu.arizona.biosemantics.etcsite.client.content.ontologize.OntologizeInputView;
import edu.arizona.biosemantics.etcsite.client.content.ontologize.OntologizeDefinePresenter;
import edu.arizona.biosemantics.etcsite.client.content.ontologize.OntologizeDefineView;
import edu.arizona.biosemantics.etcsite.client.content.ontologize.OntologizeOutputPresenter;
import edu.arizona.biosemantics.etcsite.client.content.ontologize.OntologizeOutputView;
import edu.arizona.biosemantics.etcsite.client.content.sample.ISampleView;
import edu.arizona.biosemantics.etcsite.client.content.sample.SampleView;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.IImportOtoView;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.ISemanticMarkupInputView;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.ISemanticMarkupDefineView;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.ISemanticMarkupLearnView;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.ISemanticMarkupOutputView;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.ISemanticMarkupParseView;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.ISemanticMarkupPreprocessView;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.ISemanticMarkupReviewView;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.ImportOtoView;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupInputPresenter;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupInputView;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupDefinePresenter;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupDefineView;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupLearnPresenter;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupLearnView;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupOutputPresenter;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupOutputView;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupParsePresenter;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupParseView;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupPreprocessPresenter;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupPreprocessView;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupReviewPresenter;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupReviewView;
import edu.arizona.biosemantics.etcsite.client.content.settings.ISettingsView;
import edu.arizona.biosemantics.etcsite.client.content.settings.SettingsView;
import edu.arizona.biosemantics.etcsite.client.content.taskManager.ITaskManagerView;
import edu.arizona.biosemantics.etcsite.client.content.taskManager.TaskManagerPresenter;
import edu.arizona.biosemantics.etcsite.client.content.taskManager.TaskManagerView;
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.IProcessingView;
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.ITaxonomyComparisonAlignView;
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.ITaxonomyComparisonInputView;
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.ITaxonomyComparisonDefineView;
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.ProcessingPresenter;
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.ProcessingView;
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.TaxonomyComparisonAlignPresenter;
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.TaxonomyComparisonAlignView;
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.TaxonomyComparisonInputPresenter;
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.TaxonomyComparisonInputView;
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.TaxonomyComparisonDefinePresenter;
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.TaxonomyComparisonDefineView;
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.TaxonomyFileTreeView;
import edu.arizona.biosemantics.etcsite.client.content.treeGeneration.ITreeGenerationInputView;
import edu.arizona.biosemantics.etcsite.client.content.treeGeneration.ITreeGenerationDefineView;
import edu.arizona.biosemantics.etcsite.client.content.treeGeneration.ITreeGenerationViewView;
import edu.arizona.biosemantics.etcsite.client.content.treeGeneration.TreeGenerationInputPresenter;
import edu.arizona.biosemantics.etcsite.client.content.treeGeneration.TreeGenerationInputView;
import edu.arizona.biosemantics.etcsite.client.content.treeGeneration.TreeGenerationDefinePresenter;
import edu.arizona.biosemantics.etcsite.client.content.treeGeneration.TreeGenerationDefineView;
import edu.arizona.biosemantics.etcsite.client.content.treeGeneration.TreeGenerationViewPresenter;
import edu.arizona.biosemantics.etcsite.client.content.treeGeneration.TreeGenerationViewView;
import edu.arizona.biosemantics.etcsite.client.content.user.IUserSelectView;
import edu.arizona.biosemantics.etcsite.client.content.user.IUsersView;
import edu.arizona.biosemantics.etcsite.client.content.user.UserSelectForSharePresenter;
import edu.arizona.biosemantics.etcsite.client.content.user.UserSelectView;
import edu.arizona.biosemantics.etcsite.client.content.user.UsersPresenter;
import edu.arizona.biosemantics.etcsite.client.content.user.UsersView;
import edu.arizona.biosemantics.etcsite.client.help.HelpActivity;
import edu.arizona.biosemantics.etcsite.client.help.IHelpView;
import edu.arizona.biosemantics.etcsite.client.layout.ContentActivityManagerProvider;
import edu.arizona.biosemantics.etcsite.client.layout.ContentActivityMapper;
import edu.arizona.biosemantics.etcsite.client.layout.EtcSitePresenter;
import edu.arizona.biosemantics.etcsite.client.layout.EtcSiteView;
import edu.arizona.biosemantics.etcsite.client.layout.HelpActivityManagerProvider;
import edu.arizona.biosemantics.etcsite.client.layout.HelpActivityMapper;
import edu.arizona.biosemantics.etcsite.client.layout.IEtcSiteView;
import edu.arizona.biosemantics.etcsite.client.layout.MyPlaceHistoryMapper;
import edu.arizona.biosemantics.etcsite.client.layout.PlaceControllerProvider;
import edu.arizona.biosemantics.etcsite.client.layout.PlaceHistoryHandlerProvider;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.IAuthenticationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.IFileServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.access.IFileAccessServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.format.IFileFormatServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.permission.IFilePermissionServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.search.IFileSearchServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.setup.ISetupServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.IMatrixGenerationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.ontologize.IOntologizeServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.ISemanticMarkupServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.task.ITaskServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison.ITaxonomyComparisonServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.treegeneration.ITreeGenerationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.user.IUserServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.visualization.IVisualizationServiceAsync;
import edu.arizona.biosemantics.etcsitehelp.shared.rpc.help.IHelpServiceAsync;
import edu.arizona.biosemantics.oto2.ontologize2.shared.IUserLogService;
import edu.arizona.biosemantics.oto2.ontologize2.shared.IUserLogServiceAsync;

public class ClientModule extends AbstractGinModule {
	
	//convention: don't set view as singleton, unless for good reason. 
	//A view should in the general case be a widget, which can only be attached to one parent at a time.
	//If defined as singleton, it will with multiple use be attached to a new parent, hence disappear in another view.
	//This is usually not the desired behavior. 
	//Use providers or make presenter singleton and responsible of view/return the view when necessary for third party
	protected void configure() {
		bind(Integer.class).annotatedWith(Names.named("CheckResumables")).toProvider(CheckResumablesProvider.class);
		
		//views, presenter
		bind(IEtcSiteView.class).to(EtcSiteView.class);
		bind(IEtcSiteView.Presenter.class).to(EtcSitePresenter.class).in(Singleton.class);
		bind(IHomeMainView.class).to(HomeMainView.class);
		bind(IHomeHeaderView.class).to(HomeHeaderView.class);
		bind(IHomeView.class).to(HomeView.class);
		bind(IAboutView.class).to(AboutView.class);
		bind(INewsView.class).to(NewsView.class);
		bind(IGettingStartedView.class).to(GettingStartedView.class);
		bind(ISettingsView.class).to(SettingsView.class);
		bind(ITaskManagerView.class).to(TaskManagerView.class);
		bind(ITaskManagerView.Presenter.class).to(TaskManagerPresenter.class);
		
		bind(IFileManagerView.Presenter.class).toProvider(FileManagerPresenterProvider.class).in(Singleton.class);
		bind(IFileManagerDialogView.Presenter.class).toProvider(FileManagerDialogPresenterProvider.class).in(Singleton.class);
		bind(ISelectableFileTreeView.Presenter.class).toProvider(SelectableFileTreePresenterProvider.class).in(Singleton.class);
		bind(ISelectableFileTreeView.Presenter.class).annotatedWith(Names.named("TaxonomySelection")).toProvider(
				SelectableFileTreePresenterProvider.class).in(Singleton.class);
		
		bind(IUsersView.class).to(UsersView.class);
		bind(IUsersView.Presenter.class).to(UsersPresenter.class).in(Singleton.class);
		bind(IUserSelectView.class).to(UserSelectView.class);
		bind(IUserSelectView.Presenter.class).to(UserSelectForSharePresenter.class).in(Singleton.class);
				
		bind(IFileTreeView.Presenter.class).to(FileTreePresenter.class).in(Singleton.class);
		bind(IFileTreeView.class).to(FileTreeView.class).in(Singleton.class);
		bind(IManagableFileTreeView.Presenter.class).to(ManagableFileTreePresenter.class).in(Singleton.class);
		bind(IManagableFileTreeView.class).to(ManagableFileTreeView.class).in(Singleton.class);
		bind(IFileContentView.class).to(FileContentView.class).in(Singleton.class);
		bind(IFileContentView.Presenter.class).to(FileContentPresenter.class).in(Singleton.class);		
		
		bind(ILoginView.class).to(LoginView.class);
		bind(ILoginView.Presenter.class).to(LoginPresenter.class);
		bind(IResetPasswordView.class).to(ResetPasswordView.class);
		bind(IResetPasswordView.Presenter.class).to(ResetPasswordPresenter.class);
		bind(IRegisterView.class).to(RegisterView.class);
		bind(IRegisterView.Presenter.class).to(RegisterPresenter.class);
		bind(ICreateSemanticMarkupFilesDialogView.Presenter.class).to(CreateSemanticMarkupFilesDialogPresenter.class).in(Singleton.class);
		bind(ICreateSemanticMarkupFilesDialogView.class).to(CreateSemanticMarkupFilesDialogView.class).in(Singleton.class);
		bind(ICreateSemanticMarkupFilesView.Presenter.class).to(CreateSemanticMarkupFilesPresenter.class).in(Singleton.class);
		bind(ICreateSemanticMarkupFilesView.class).to(CreateSemanticMarkupFilesView.class).in(Singleton.class);
		
		bind(IOntologizeInputView.class).to(OntologizeInputView.class).in(Singleton.class);
		bind(IOntologizeInputView.Presenter.class).to(OntologizeInputPresenter.class).in(Singleton.class);
		bind(IOntologizeDefineView.class).to(OntologizeDefineView.class).in(Singleton.class);
		bind(IOntologizeDefineView.Presenter.class).to(OntologizeDefinePresenter.class).in(Singleton.class);
		bind(IOntologizeBuildView.class).to(OntologizeBuildView.class).in(Singleton.class);
		bind(IOntologizeBuildView.Presenter.class).to(OntologizeBuildPresenter.class).in(Singleton.class);
		bind(IOntologizeOutputView.class).to(OntologizeOutputView.class).in(Singleton.class);
		bind(IOntologizeOutputView.Presenter.class).to(OntologizeOutputPresenter.class).in(Singleton.class);
		
		//userlog
		//bind(IUserLogServiceAsync.class).to userLogService = GWT.create(IUserLogService.class);
		
		bind(IAnnotationReviewView.class).to(AnnotationReviewView.class);
		bind(IAnnotationReviewView.Presenter.class).to(AnnotationReviewPresenter.class);
		bind(ISearchView.class).to(SearchView.class);
		bind(ISearchView.Presenter.class).to(SearchPresenter.class);
		bind(IResultView.class).to(ResultView.class);
		bind(IResultView.Presenter.class).to(ResultPresenter.class);
		bind(IXMLEditorView.class).to(XMLEditorView.class);
		bind(IXMLEditorView.Presenter.class).to(XMLEditorPresenter.class);
		
		bind(IMatrixGenerationDefineView.class).to(MatrixGenerationDefineView.class);
		bind(IMatrixGenerationDefineView.Presenter.class).to(MatrixGenerationDefinePresenter.class);
		bind(IMatrixGenerationInputView.class).to(MatrixGenerationInputView.class);
		bind(IMatrixGenerationInputView.Presenter.class).to(MatrixGenerationInputPresenter.class);
		bind(IMatrixGenerationProcessView.class).to(MatrixGenerationProcessView.class);
		bind(IMatrixGenerationProcessView.Presenter.class).to(MatrixGenerationProcessPresenter.class);
		bind(IMatrixGenerationReviewView.class).to(MatrixGenerationReviewView.class);
		bind(IMatrixGenerationReviewView.Presenter.class).to(MatrixGenerationReviewPresenter.class);
		bind(IMatrixGenerationOutputView.class).to(MatrixGenerationOutputView.class);
		bind(IMatrixGenerationOutputView.Presenter.class).to(MatrixGenerationOutputPresenter.class);
		
		bind(ISemanticMarkupDefineView.class).to(SemanticMarkupDefineView.class);
		bind(ISemanticMarkupDefineView.Presenter.class).to(SemanticMarkupDefinePresenter.class);
		bind(ISemanticMarkupInputView.class).to(SemanticMarkupInputView.class);
		bind(ISemanticMarkupInputView.Presenter.class).to(SemanticMarkupInputPresenter.class);
		bind(ISemanticMarkupPreprocessView.class).to(SemanticMarkupPreprocessView.class);
		bind(ISemanticMarkupPreprocessView.Presenter.class).to(SemanticMarkupPreprocessPresenter.class);
		bind(ISemanticMarkupLearnView.class).to(SemanticMarkupLearnView.class);
		bind(ISemanticMarkupLearnView.Presenter.class).to(SemanticMarkupLearnPresenter.class);
		bind(ISemanticMarkupReviewView.class).to(SemanticMarkupReviewView.class);
		bind(ISemanticMarkupReviewView.Presenter.class).to(SemanticMarkupReviewPresenter.class);
		bind(ISemanticMarkupParseView.class).to(SemanticMarkupParseView.class);
		bind(ISemanticMarkupParseView.Presenter.class).to(SemanticMarkupParsePresenter.class);
		bind(ISemanticMarkupOutputView.class).to(SemanticMarkupOutputView.class);
		bind(ISemanticMarkupOutputView.Presenter.class).to(SemanticMarkupOutputPresenter.class);
		bind(ITreeGenerationDefineView.class).to(TreeGenerationDefineView.class);
		bind(ITreeGenerationDefineView.Presenter.class).to(TreeGenerationDefinePresenter.class);
		bind(ITreeGenerationInputView.class).to(TreeGenerationInputView.class);
		bind(ITreeGenerationInputView.Presenter.class).to(TreeGenerationInputPresenter.class);
		bind(ITreeGenerationViewView.class).to(TreeGenerationViewView.class);
		bind(ITreeGenerationViewView.Presenter.class).to(TreeGenerationViewPresenter.class);
		bind(IImportOtoView.class).to(ImportOtoView.class);
		
		bind(ITaxonomyComparisonDefineView.class).to(TaxonomyComparisonDefineView.class);
		bind(ITaxonomyComparisonDefineView.Presenter.class).to(TaxonomyComparisonDefinePresenter.class);
		bind(ITaxonomyComparisonInputView.class).to(TaxonomyComparisonInputView.class);
		bind(ITaxonomyComparisonInputView.Presenter.class).to(TaxonomyComparisonInputPresenter.class).in(Singleton.class);
		bind(ITaxonomyComparisonAlignView.class).to(TaxonomyComparisonAlignView.class);
		bind(ITaxonomyComparisonAlignView.Presenter.class).to(TaxonomyComparisonAlignPresenter.class);
		bind(IProcessingView.class).to(ProcessingView.class);
		bind(IProcessingView.Presenter.class).to(ProcessingPresenter.class);
		bind(edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.IInputCreateView.class)
		.to(edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.InputCreateView.class);
		bind(edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.IInputCreateView.Presenter.class)
		.to(edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.InputCreatePresenter.class).in(Singleton.class);
		
		bind(edu.arizona.biosemantics.etcsite.client.help.IHelpView.class).to(edu.arizona.biosemantics.etcsite.client.help.HelpView.class).in(Singleton.class);
		bind(IHelpView.Presenter.class).to(HelpActivity.class).in(Singleton.class);
		
		bind(ISampleView.class).to(SampleView.class).in(Singleton.class);
		
		//activites, places, eventbus
		bind(EventBus.class).annotatedWith(Names.named("ActivitiesBus")).to(SimpleEventBus.class).in(Singleton.class);
		bind(PlaceController.class).toProvider(PlaceControllerProvider.class).in(Singleton.class);
		bind(MyActivityMapper.class).annotatedWith(Names.named("Content")).
			to(ContentActivityMapper.class).in(Singleton.class);
		bind(MyActivityManager.class).annotatedWith(Names.named("Content")).
			toProvider(ContentActivityManagerProvider.class).in(Singleton.class);
		bind(MyActivityManager.class).annotatedWith(Names.named("Help")).
			toProvider(HelpActivityManagerProvider.class).in(Singleton.class);
		bind(MyActivityMapper.class).annotatedWith(Names.named("Help")).
			to(HelpActivityMapper.class).in(Singleton.class);
		bind(Place.class).annotatedWith(Names.named("DefaultPlace")).to(HomePlace.class);
		
		bind(PlaceHistoryMapper.class).to(MyPlaceHistoryMapper.class).in(Singleton.class);
		bind(PlaceHistoryHandler.class).toProvider(PlaceHistoryHandlerProvider.class).in(Singleton.class);
		
		bind(EventBus.class).annotatedWith(Names.named("EtcSite")).to(SimpleEventBus.class).in(Singleton.class);
		bind(EventBus.class).annotatedWith(Names.named("AnnotationReview")).to(SimpleEventBus.class).in(Singleton.class);
		
		//services
		bind(IAuthenticationServiceAsync.class).in(Singleton.class);
		bind(IFileAccessServiceAsync.class).in(Singleton.class);
		bind(IFileFormatServiceAsync.class).in(Singleton.class);
		bind(IFilePermissionServiceAsync.class).in(Singleton.class);
		bind(IFileSearchServiceAsync.class).in(Singleton.class);
		bind(IFileServiceAsync.class).in(Singleton.class);
		bind(IMatrixGenerationServiceAsync.class).in(Singleton.class);
		bind(ISemanticMarkupServiceAsync.class).in(Singleton.class);
		bind(ISetupServiceAsync.class).in(Singleton.class);
		bind(ITaskServiceAsync.class).in(Singleton.class);
		bind(IMatrixGenerationServiceAsync.class).in(Singleton.class);
		bind(ITreeGenerationServiceAsync.class).in(Singleton.class);
		bind(IUserServiceAsync.class).in(Singleton.class);
		bind(IVisualizationServiceAsync.class).in(Singleton.class);
		bind(ITreeGenerationServiceAsync.class).in(Singleton.class);
		bind(ITaxonomyComparisonServiceAsync.class).in(Singleton.class);
		bind(IOntologizeServiceAsync.class).in(Singleton.class);
		bind(IHelpServiceAsync.class).in(Singleton.class);
		
		//misc
		bind(FilePathShortener.class).in(Singleton.class);
		bind(IInputCreateView.class).to(InputCreateView.class);
		bind(IInputCreateView.Presenter.class).annotatedWith(Names.named("SemanticMarkup")).to(InputCreatePresenter.class).in(Singleton.class);
		bind(IInputCreateView.Presenter.class).annotatedWith(Names.named("Ontologize")).to(InputCreatePresenter.class).in(Singleton.class);
		bind(IInputCreateView.Presenter.class).annotatedWith(Names.named("MatrixGeneration")).to(InputCreatePresenter.class).in(Singleton.class);
		bind(IInputCreateView.Presenter.class).annotatedWith(Names.named("TreeGeneration")).to(InputCreatePresenter.class).in(Singleton.class);
		bind(IInputCreateView.Presenter.class).annotatedWith(Names.named("TaxonomyComparison")).to(InputCreatePresenter.class).in(Singleton.class);
	}
	
	public static class HandlerManagerProvider implements Provider<HandlerManager> {
		private HandlerManager handlerManager;
		public HandlerManagerProvider() {
			this.handlerManager = new HandlerManager(null);
		}
		@Override
		public HandlerManager get() {
			return handlerManager;
		}
	}
	
	public static class FileManagerPresenterProvider implements Provider<IFileManagerView.Presenter> {

		private IManagableFileTreeView managableFileTreeView;
		private IManagableFileTreeView.Presenter managableFileTreePresenter;
		private IFileManagerView fileManagerView;
		private IFileManagerView.Presenter fileManagerPresenter;
		private IFileTreeView.Presenter fileTreePresenter;
		private IFileTreeView fileTreeView;
		
		@Inject
		public FileManagerPresenterProvider(IFileServiceAsync fileService,
				ICreateSemanticMarkupFilesDialogView.Presenter createSemanticMarkupFilesDialogPresenter,
				PlaceController placeController, IFileContentView.Presenter fileContentPresenter) {
			fileTreeView = new FileTreeView(fileService, fileContentPresenter);
			fileTreePresenter = new FileTreePresenter(fileTreeView);
			managableFileTreeView = new ManagableFileTreeView(fileTreePresenter);			
			managableFileTreePresenter = new ManagableFileTreePresenter(managableFileTreeView, fileTreePresenter, fileService, 
						createSemanticMarkupFilesDialogPresenter);
			fileManagerView = new FileManagerView(managableFileTreePresenter);
			fileManagerPresenter = new FileManagerPresenter(placeController, fileManagerView, managableFileTreePresenter);
		}
		
		@Override
		public IFileManagerView.Presenter get() {
			return fileManagerPresenter;
		}
	}
	
	public static class FileManagerDialogPresenterProvider implements Provider<IFileManagerDialogView.Presenter> {

		private IManagableFileTreeView managableFileTreeView;
		private IManagableFileTreeView.Presenter managableFileTreePresenter;
		private IFileManagerDialogView fileManagerDialogView;
		private IFileManagerDialogView.Presenter fileManagerDialogPresenter;
		private IFileTreeView.Presenter fileTreePresenter;
		private IFileTreeView fileTreeView;
		
		@Inject
		public FileManagerDialogPresenterProvider(IFileServiceAsync fileService,
				ICreateSemanticMarkupFilesDialogView.Presenter createSemanticMarkupFilesDialogPresenter, 
				IFileContentView.Presenter fileContentPresenter) {
			fileTreeView = new FileTreeView(fileService, fileContentPresenter);
			fileTreePresenter = new FileTreePresenter(fileTreeView);
			managableFileTreeView = new ManagableFileTreeView(fileTreePresenter);			
			managableFileTreePresenter = new ManagableFileTreePresenter(managableFileTreeView, fileTreePresenter, fileService, 
					createSemanticMarkupFilesDialogPresenter);
			fileManagerDialogView = new FileManagerDialogView(managableFileTreePresenter);
			fileManagerDialogPresenter = new FileManagerDialogPresenter(fileManagerDialogView, managableFileTreePresenter);
		}
		
		@Override
		public IFileManagerDialogView.Presenter get() {
			return fileManagerDialogPresenter;
		}
		
	}
	
	public static class SelectableFileTreePresenterProvider implements Provider<ISelectableFileTreeView.Presenter> {

		private ISelectableFileTreeView selectableFileTreeView;
		private ISelectableFileTreeView.Presenter selectableFileTreePresenter;
		private IFileTreeView.Presenter fileTreePresenter;
		private IFileTreeView fileTreeView;
		
		@Inject
		public SelectableFileTreePresenterProvider(IFileServiceAsync fileService, IFileContentView.Presenter fileContentPresenter) {
			fileTreeView = new FileTreeView(fileService, fileContentPresenter);
			fileTreePresenter = new FileTreePresenter(fileTreeView);
			selectableFileTreeView = new SelectableFileTreeView(fileTreePresenter);
			selectableFileTreePresenter = new SelectableFileTreePresenter(selectableFileTreeView, fileTreePresenter);
		}
		
		@Override
		public ISelectableFileTreeView.Presenter get() {
			return selectableFileTreePresenter;
		}
		
	}
	
	public static class SelectableTaxonomyFileTreePresenterProvider implements Provider<ISelectableFileTreeView.Presenter> {

		private ISelectableFileTreeView selectableFileTreeView;
		private ISelectableFileTreeView.Presenter selectableFileTreePresenter;
		private IFileTreeView.Presenter fileTreePresenter;
		private IFileTreeView fileTreeView;
		
		@Inject
		public SelectableTaxonomyFileTreePresenterProvider(IFileServiceAsync fileService) {
			fileTreeView = new TaxonomyFileTreeView(fileService);
			fileTreePresenter = new FileTreePresenter(fileTreeView);
			selectableFileTreeView = new SelectableFileTreeView(fileTreePresenter);
			selectableFileTreePresenter = new SelectableFileTreePresenter(selectableFileTreeView, fileTreePresenter);
		}
		
		@Override
		public ISelectableFileTreeView.Presenter get() {
			return selectableFileTreePresenter;
		}
		
	}
	
	public static class CheckResumablesProvider implements Provider<Integer> {
		@Override
		public Integer get() {
			return 10000;
		}
	}
}