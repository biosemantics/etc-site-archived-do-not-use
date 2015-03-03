//package edu.arizona.biosemantics.etcsite.client.menu;
//
//import com.google.gwt.activity.shared.AbstractActivity;
//import com.google.gwt.event.shared.EventBus;
//import com.google.gwt.place.shared.PlaceController;
//import com.google.gwt.user.client.rpc.AsyncCallback;
//import com.google.gwt.user.client.ui.AcceptsOneWidget;
//import com.google.inject.Inject;
//import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
//import com.sencha.gxt.widget.core.client.box.MessageBox;
//import com.sencha.gxt.widget.core.client.event.SelectEvent;
//import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
//
//import edu.arizona.biosemantics.etcsite.client.common.Alerter;
//import edu.arizona.biosemantics.etcsite.client.common.Authentication;
//import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationInputPlace;
//import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupInputPlace;
//import edu.arizona.biosemantics.etcsite.client.content.taskManager.ResumeTaskPlaceMapper;
//import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.TaxonomyComparisonPlace;
//import edu.arizona.biosemantics.etcsite.client.content.treeGeneration.TreeGenerationInputPlace;
//import edu.arizona.biosemantics.etcsite.client.content.treeGeneration.TreeGenerationPlace;
//import edu.arizona.biosemantics.etcsite.client.content.visualization.VisualizationPlace;
//import edu.arizona.biosemantics.etcsite.client.menu.IMenuView.Presenter;
//import edu.arizona.biosemantics.etcsite.shared.model.Task;
//import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.IMatrixGenerationServiceAsync;
//import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.ISemanticMarkupServiceAsync;
//import edu.arizona.biosemantics.etcsite.shared.rpc.treegeneration.ITreeGenerationServiceAsync;
//
//public class MenuActivity extends AbstractActivity implements Presenter {
//
//	private IMenuView menuView;
//	private PlaceController placeController;
//	private ISemanticMarkupServiceAsync semanticMarkupService;
//	private IMatrixGenerationServiceAsync matrixGenerationService;
//	private ITreeGenerationServiceAsync treeGenerationService;
//	private ResumeTaskPlaceMapper resumeTaskPlaceMapper;
//
//	@Inject
//	public MenuActivity(IMenuView menuView, PlaceController placeController, 
//			ISemanticMarkupServiceAsync semanticMarkupService,
//			IMatrixGenerationServiceAsync matrixGenerationService, 
//			ITreeGenerationServiceAsync treeGenerationService,
//			ResumeTaskPlaceMapper resumeTaskPlaceMapper) {
//		this.menuView = menuView;
//		this.placeController = placeController;
//		this.semanticMarkupService = semanticMarkupService;
//		this.matrixGenerationService = matrixGenerationService;
//		this.treeGenerationService = treeGenerationService;
//		this.resumeTaskPlaceMapper = resumeTaskPlaceMapper;
//	}
//	
//	@Override
//	public void start(AcceptsOneWidget panel, EventBus eventBus) {
//		menuView.setPresenter(this);
//		panel.setWidget(menuView.asWidget());
//		menuView.initNativeJavascriptAnimations();
//	}
//
//	@Override
//	public void onSemanticMarkup() {		
//		semanticMarkupService.getLatestResumable(Authentication.getInstance().getToken(),
//				new AsyncCallback<Task>() {
//			@Override
//			public void onSuccess(final Task task) {
//				if(task != null) {
//					MessageBox resumable = Alerter.resumableTask();
//					resumable.getButton(PredefinedButton.YES).addSelectHandler(new SelectHandler() {
//						@Override
//						public void onSelect(SelectEvent event) {
//							placeController.goTo(resumeTaskPlaceMapper.getPlace(task));
//						}
//					});
//					resumable.getButton(PredefinedButton.NO).addSelectHandler(new SelectHandler() {
//						@Override
//						public void onSelect(SelectEvent event) {
//							placeController.goTo(new SemanticMarkupInputPlace());
//						}
//					});
//				} else 
//					placeController.goTo(new SemanticMarkupInputPlace());
//			}
//
//			@Override
//			public void onFailure(Throwable caught) {
//				Alerter.failedToGetLatestResumable(caught);
//			}
//		});
//	}
//
//	@Override
//	public void onMatrixGeneration() {
//		matrixGenerationService.getLatestResumable(Authentication.getInstance().getToken(),
//				new AsyncCallback<Task>() {
//			@Override
//			public void onSuccess(final Task task) {
//				if(task != null) {
//					MessageBox resumable = Alerter.resumableTask();
//					resumable.getButton(PredefinedButton.YES).addSelectHandler(new SelectHandler() {
//						@Override
//						public void onSelect(SelectEvent event) {
//							placeController.goTo(resumeTaskPlaceMapper.getPlace(task));
//						}
//					});
//					resumable.getButton(PredefinedButton.NO).addSelectHandler(new SelectHandler() {
//						@Override
//						public void onSelect(SelectEvent event) {
//							placeController.goTo(new MatrixGenerationInputPlace());
//						}
//					});
//				} else 
//					placeController.goTo(new MatrixGenerationInputPlace());
//			}
//
//			@Override
//			public void onFailure(Throwable caught) {
//				Alerter.failedToGetLatestResumable(caught);
//			}
//		});
//	}
//
//	@Override
//	public void onTreeGeneration() {
//		treeGenerationService.getLatestResumable(Authentication.getInstance().getToken(),
//				new AsyncCallback<Task>() {
//			@Override
//			public void onSuccess(final Task task) {
//				if(task != null) {
//					MessageBox resumable = Alerter.resumableTask();
//					resumable.getButton(PredefinedButton.YES).addSelectHandler(new SelectHandler() {
//						@Override
//						public void onSelect(SelectEvent event) {
//							placeController.goTo(resumeTaskPlaceMapper.getPlace(task));
//						}
//					});
//					resumable.getButton(PredefinedButton.NO).addSelectHandler(new SelectHandler() {
//						@Override
//						public void onSelect(SelectEvent event) {
//							placeController.goTo(new TreeGenerationInputPlace());
//						}
//					});
//				} else 
//					placeController.goTo(new TreeGenerationInputPlace());
//			}
//
//			@Override
//			public void onFailure(Throwable caught) {
//				Alerter.failedToGetLatestResumable(caught);
//			}
//		});
//	}
//
//	@Override
//	public void onTaxonomyComparison() {
//		placeController.goTo(new TaxonomyComparisonPlace());
//	}
//
//	@Override
//	public void onVisualization() {
//		placeController.goTo(new VisualizationPlace());
//	}
//
//}
