package edu.arizona.biosemantics.etcsite.client;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.activity.shared.MyActivityManager;
import com.google.gwt.activity.shared.MyActivityMapper;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

import edu.arizona.biosemantics.etcsite.client.common.IMessageConfirmView;
import edu.arizona.biosemantics.etcsite.client.common.IMessageView;
import edu.arizona.biosemantics.etcsite.client.common.ITextInputView;
import edu.arizona.biosemantics.etcsite.client.common.MessageConfirmPresenter;
import edu.arizona.biosemantics.etcsite.client.common.MessageConfirmView;
import edu.arizona.biosemantics.etcsite.client.common.MessagePresenter;
import edu.arizona.biosemantics.etcsite.client.common.MessageView;
import edu.arizona.biosemantics.etcsite.client.common.TextInputPresenter;
import edu.arizona.biosemantics.etcsite.client.common.TextInputView;
import edu.arizona.biosemantics.etcsite.client.common.files.DnDFileTreePresenter;
import edu.arizona.biosemantics.etcsite.client.common.files.FileContentPresenter;
import edu.arizona.biosemantics.etcsite.client.common.files.FileContentView;
import edu.arizona.biosemantics.etcsite.client.common.files.FileDragDropHandler;
import edu.arizona.biosemantics.etcsite.client.common.files.FileTreeDecorator;
import edu.arizona.biosemantics.etcsite.client.common.files.FileTreePresenter;
import edu.arizona.biosemantics.etcsite.client.common.files.FileTreeView;
import edu.arizona.biosemantics.etcsite.client.common.files.IFileContentView;
import edu.arizona.biosemantics.etcsite.client.common.files.IFileTreeView;
import edu.arizona.biosemantics.etcsite.client.common.files.IManagableFileTreeView;
import edu.arizona.biosemantics.etcsite.client.common.files.ISavableFileTreeView;
import edu.arizona.biosemantics.etcsite.client.common.files.ISelectableFileTreeView;
import edu.arizona.biosemantics.etcsite.client.common.files.ManagableFileTreePresenter;
import edu.arizona.biosemantics.etcsite.client.common.files.ManagableFileTreeView;
import edu.arizona.biosemantics.etcsite.client.common.files.SavableFileTreePresenter;
import edu.arizona.biosemantics.etcsite.client.common.files.SavableFileTreeView;
import edu.arizona.biosemantics.etcsite.client.common.files.SelectableFileTreePresenter;
import edu.arizona.biosemantics.etcsite.client.common.files.SelectableFileTreeView;
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
import edu.arizona.biosemantics.etcsite.client.content.fileManager.IFileManagerDialogView.Presenter;
import edu.arizona.biosemantics.etcsite.client.content.fileManager.IFileManagerView;
import edu.arizona.biosemantics.etcsite.client.content.help.HelpView;
import edu.arizona.biosemantics.etcsite.client.content.help.IHelpView;
import edu.arizona.biosemantics.etcsite.client.content.home.HomeContentView;
import edu.arizona.biosemantics.etcsite.client.content.home.IHomeContentView;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.IMatrixGenerationInputView;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.IMatrixGenerationOutputView;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.IMatrixGenerationProcessView;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.IMatrixGenerationReviewView;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationInputPresenter;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationInputView;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationOutputPresenter;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationOutputView;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationProcessPresenter;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationProcessView;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationReviewPresenter;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationReviewView;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.review.IReviewView;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.review.ReviewPresenter;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.review.ReviewView;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.ISemanticMarkupInputView;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.ISemanticMarkupLearnView;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.ISemanticMarkupOutputView;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.ISemanticMarkupParseView;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.ISemanticMarkupPreprocessView;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.ISemanticMarkupReviewView;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupInputPresenter;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupInputView;
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
import edu.arizona.biosemantics.etcsite.client.content.user.IUserSelectView;
import edu.arizona.biosemantics.etcsite.client.content.user.IUsersView;
import edu.arizona.biosemantics.etcsite.client.content.user.UserSelectPresenter;
import edu.arizona.biosemantics.etcsite.client.content.user.UserSelectView;
import edu.arizona.biosemantics.etcsite.client.content.user.UsersPresenter;
import edu.arizona.biosemantics.etcsite.client.content.user.UsersView;
import edu.arizona.biosemantics.etcsite.client.layout.ContentActivityManagerProvider;
import edu.arizona.biosemantics.etcsite.client.layout.ContentActivityMapper;
import edu.arizona.biosemantics.etcsite.client.layout.EtcSiteView;
import edu.arizona.biosemantics.etcsite.client.layout.IEtcSiteView;
import edu.arizona.biosemantics.etcsite.client.layout.MenuActivityManagerProvider;
import edu.arizona.biosemantics.etcsite.client.layout.MenuActivityMapper;
import edu.arizona.biosemantics.etcsite.client.layout.MyPlaceHistoryMapper;
import edu.arizona.biosemantics.etcsite.client.layout.PlaceControllerProvider;
import edu.arizona.biosemantics.etcsite.client.layout.PlaceHistoryHandlerProvider;
import edu.arizona.biosemantics.etcsite.client.layout.TopActivityManagerProvider;
import edu.arizona.biosemantics.etcsite.client.layout.TopActivityMapper;
import edu.arizona.biosemantics.etcsite.client.menu.IMenuView;
import edu.arizona.biosemantics.etcsite.client.menu.IStartMenuView;
import edu.arizona.biosemantics.etcsite.client.menu.MenuView;
import edu.arizona.biosemantics.etcsite.client.menu.StartMenuView;
import edu.arizona.biosemantics.etcsite.client.top.ILoginTopView;
import edu.arizona.biosemantics.etcsite.client.top.ITopView;
import edu.arizona.biosemantics.etcsite.client.top.LoggedOutPlace;
import edu.arizona.biosemantics.etcsite.client.top.LoginTopView;
import edu.arizona.biosemantics.etcsite.client.top.TopView;
import edu.arizona.biosemantics.etcsite.shared.file.FilePathShortener;
import edu.arizona.biosemantics.etcsite.shared.rpc.IAuthenticationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileAccessServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileFormatServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFilePermissionServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileSearchServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.IMatrixGenerationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.ISemanticMarkupServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.ISetupServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.ITaskServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.ITaxonomyComparisonServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.ITreeGenerationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.IUserServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.IVisualizationServiceAsync;

public class ClientModule extends AbstractGinModule {
	
	//convention: don't set view as singleton, unless for good reason. 
	//A view should in the general case be a widget, which can only be attached to one parent at a time.
	//If defined as singleton, it will with multiple use be attached to a new parent, hence disappear in another view.
	//This is usually not the desired behavior. 
	//Use providers or make presenter singleton and responsible of view 
	protected void configure() {		
		//views, presenter
		bind(IEtcSiteView.class).to(EtcSiteView.class);
		bind(ITopView.class).to(TopView.class);
		bind(ILoginTopView.class).to(LoginTopView.class);
		bind(IMenuView.class).to(MenuView.class);
		bind(IStartMenuView.class).to(StartMenuView.class);
		bind(IHomeContentView.class).to(HomeContentView.class);
		bind(IHelpView.class).to(HelpView.class);
		bind(ISettingsView.class).to(SettingsView.class);
		bind(ITaskManagerView.class).to(TaskManagerView.class);
		bind(ITaskManagerView.Presenter.class).to(TaskManagerPresenter.class);
		
		bind(IFileManagerView.Presenter.class).toProvider(FileManagerPresenterProvider.class).in(Singleton.class);
		bind(IFileManagerDialogView.Presenter.class).toProvider(FileManagerDialogPresenterProvider.class).in(Singleton.class);
		bind(ISelectableFileTreeView.Presenter.class).toProvider(SelectableFileTreePresenterProvider.class).in(Singleton.class);
		
		bind(IUsersView.class).to(UsersView.class);
		bind(IUsersView.Presenter.class).to(UsersPresenter.class);
		bind(IUserSelectView.class).to(UserSelectView.class);
		bind(IUserSelectView.Presenter.class).to(UserSelectPresenter.class);
				
		bind(IFileTreeView.Presenter.class).to(FileTreePresenter.class);
		bind(IFileTreeView.class).to(FileTreeView.class);
		bind(IManagableFileTreeView.Presenter.class).to(ManagableFileTreePresenter.class);
		bind(IManagableFileTreeView.class).to(ManagableFileTreeView.class);
		bind(IFileContentView.class).to(FileContentView.class);
		bind(IFileContentView.Presenter.class).to(FileContentPresenter.class);		
		
		bind(ITextInputView.class).to(TextInputView.class);
		bind(ITextInputView.Presenter.class).to(TextInputPresenter.class);
		bind(IMessageView.class).to(MessageView.class);
		bind(IMessageView.Presenter.class).to(MessagePresenter.class);
		bind(IMessageConfirmView.class).to(MessageConfirmView.class);
		bind(IMessageConfirmView.Presenter.class).to(MessageConfirmPresenter.class);
		
		bind(IAnnotationReviewView.class).to(AnnotationReviewView.class);
		bind(IAnnotationReviewView.Presenter.class).to(AnnotationReviewPresenter.class);
		bind(ISearchView.class).to(SearchView.class);
		bind(ISearchView.Presenter.class).to(SearchPresenter.class);
		bind(IResultView.class).to(ResultView.class);
		bind(IResultView.Presenter.class).to(ResultPresenter.class);
		bind(IXMLEditorView.class).to(XMLEditorView.class);
		bind(IXMLEditorView.Presenter.class).to(XMLEditorPresenter.class);
		
		bind(IMatrixGenerationInputView.class).to(MatrixGenerationInputView.class);
		bind(IMatrixGenerationInputView.Presenter.class).to(MatrixGenerationInputPresenter.class);
		bind(IMatrixGenerationProcessView.class).to(MatrixGenerationProcessView.class);
		bind(IMatrixGenerationProcessView.Presenter.class).to(MatrixGenerationProcessPresenter.class);
		bind(IMatrixGenerationReviewView.class).to(MatrixGenerationReviewView.class);
		bind(IMatrixGenerationReviewView.Presenter.class).to(MatrixGenerationReviewPresenter.class);
		bind(IReviewView.class).to(ReviewView.class);
		bind(IReviewView.Presenter.class).to(ReviewPresenter.class).in(Singleton.class);
		bind(IMatrixGenerationOutputView.class).to(MatrixGenerationOutputView.class);
		bind(IMatrixGenerationOutputView.Presenter.class).to(MatrixGenerationOutputPresenter.class);
		
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
		
		//activites, places, eventbus
		bind(EventBus.class).annotatedWith(Names.named("ActivitiesBus")).to(SimpleEventBus.class).in(Singleton.class);
		bind(PlaceController.class).toProvider(PlaceControllerProvider.class).in(Singleton.class);
		bind(ActivityMapper.class).annotatedWith(Names.named("Top")).
			to(TopActivityMapper.class).in(Singleton.class);
		bind(ActivityMapper.class).annotatedWith(Names.named("Menu")).
			to(MenuActivityMapper.class).in(Singleton.class);
		bind(MyActivityMapper.class).annotatedWith(Names.named("Content")).
			to(ContentActivityMapper.class).in(Singleton.class);
		bind(ActivityManager.class).annotatedWith(Names.named("Top")).
			toProvider(TopActivityManagerProvider.class).in(Singleton.class);
		bind(ActivityManager.class).annotatedWith(Names.named("Menu")).
			toProvider(MenuActivityManagerProvider.class).in(Singleton.class);
		bind(MyActivityManager.class).annotatedWith(Names.named("Content")).
			toProvider(ContentActivityManagerProvider.class).in(Singleton.class);
		bind(PlaceHistoryMapper.class).to(MyPlaceHistoryMapper.class).in(Singleton.class);
		bind(PlaceHistoryHandler.class).toProvider(PlaceHistoryHandlerProvider.class).in(Singleton.class);
		bind(Place.class).annotatedWith(Names.named("DefaultPlace")).to(LoggedOutPlace.class);
		
		bind(EventBus.class).annotatedWith(Names.named("Tasks")).to(SimpleEventBus.class).in(Singleton.class);
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
		bind(ITaxonomyComparisonServiceAsync.class).in(Singleton.class);
		bind(ITreeGenerationServiceAsync.class).in(Singleton.class);
		bind(IUserServiceAsync.class).in(Singleton.class);
		bind(IVisualizationServiceAsync.class).in(Singleton.class);
		
		//misc
		bind(FilePathShortener.class).in(Singleton.class);
	}
	
	public static class FileManagerPresenterProvider implements Provider<IFileManagerView.Presenter> {

		private IManagableFileTreeView managableFileTreeView;
		private IManagableFileTreeView.Presenter managableFileTreePresenter;
		private IFileManagerView fileManagerView;
		private IFileManagerView.Presenter fileManagerPresenter;
		private IFileTreeView.Presenter fileTreePresenter;
		private IFileTreeView fileTreeView;
		
		@Inject
		public FileManagerPresenterProvider(IFileServiceAsync fileService, FileTreeDecorator fileTreeDecorator, FileDragDropHandler
				fileDragDropHandler, IMessageView.Presenter messagePresenter, ITextInputView.Presenter textInputPresenter, 
				PlaceController placeController) {
			fileTreeView = new FileTreeView();
			fileTreePresenter = new DnDFileTreePresenter(fileTreeView, fileService, fileTreeDecorator, fileDragDropHandler);
			managableFileTreeView = new ManagableFileTreeView(fileTreePresenter);			
			managableFileTreePresenter = new ManagableFileTreePresenter(managableFileTreeView, fileTreePresenter, fileService, 
					messagePresenter, textInputPresenter);
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
		public FileManagerDialogPresenterProvider(IFileServiceAsync fileService, FileTreeDecorator fileTreeDecorator, FileDragDropHandler
				fileDragDropHandler, IMessageView.Presenter messagePresenter, ITextInputView.Presenter textInputPresenter) {
			fileTreeView = new FileTreeView();
			fileTreePresenter = new DnDFileTreePresenter(fileTreeView, fileService, fileTreeDecorator, fileDragDropHandler);
			managableFileTreeView = new ManagableFileTreeView(fileTreePresenter);			
			managableFileTreePresenter = new ManagableFileTreePresenter(managableFileTreeView, fileTreePresenter, fileService, 
					messagePresenter, textInputPresenter);
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
		public SelectableFileTreePresenterProvider(IFileServiceAsync fileService, FileTreeDecorator fileTreeDecorator, FileDragDropHandler
				fileDragDropHandler, IMessageView.Presenter messagePresenter, ITextInputView.Presenter textInputPresenter) {
			fileTreeView = new FileTreeView();
			fileTreePresenter = new FileTreePresenter(fileTreeView, fileService, fileTreeDecorator);
			selectableFileTreeView = new SelectableFileTreeView(fileTreePresenter);
			selectableFileTreePresenter = new SelectableFileTreePresenter(selectableFileTreeView, fileTreePresenter);
		}
		
		@Override
		public ISelectableFileTreeView.Presenter get() {
			return selectableFileTreePresenter;
		}
		
	}
}