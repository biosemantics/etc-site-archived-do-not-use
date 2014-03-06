package edu.arizona.biosemantics.etcsite.client.content.home;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.activity.shared.MyAbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.HasTaskPlace;
import edu.arizona.biosemantics.etcsite.client.common.ILoginView;
import edu.arizona.biosemantics.etcsite.client.common.IMessageConfirmView;
import edu.arizona.biosemantics.etcsite.client.common.ILoginView.ILoginListener;
import edu.arizona.biosemantics.etcsite.client.common.ILoginView.Presenter;
import edu.arizona.biosemantics.etcsite.client.common.IMessageConfirmView.IConfirmListener;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationInputPlace;
import edu.arizona.biosemantics.etcsite.client.content.pipeline.PipelinePlace;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupInputPlace;
import edu.arizona.biosemantics.etcsite.client.content.taskManager.ResumeTaskPlaceMapper;
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.TaxonomyComparisonPlace;
import edu.arizona.biosemantics.etcsite.client.content.treeGeneration.TreeGenerationPlace;
import edu.arizona.biosemantics.etcsite.client.content.visualization.VisualizationPlace;
import edu.arizona.biosemantics.etcsite.client.menu.IStartMenuView;
import edu.arizona.biosemantics.etcsite.client.top.LoggedInPlace;
import edu.arizona.biosemantics.etcsite.shared.db.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.IHasTasksServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.IPipelineServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.ITaxonomyComparisonServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.ITreeGenerationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.IVisualizationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCCallback;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.IMatrixGenerationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.ISemanticMarkupServiceAsync;

public class HomeActivity extends MyAbstractActivity implements IStartMenuView.Presenter, IHomeContentView.Presenter {

    private PlaceController placeController;
	private IHomeContentView homeContentView;
	private Presenter loginPresenter;
	private ISemanticMarkupServiceAsync semanticMarkupService;
	private IMatrixGenerationServiceAsync matrixGenerationService;
	private IMessageConfirmView.Presenter messageConfirmPresenter;
	private ResumeTaskPlaceMapper resumeTaskPlaceMapper;
	private ITaxonomyComparisonServiceAsync taxonomyComparisonService;
	private ITreeGenerationServiceAsync treeGenerationService;
	private IVisualizationServiceAsync visualizationService;
	private IPipelineServiceAsync pipelineService;

	@Inject
	public HomeActivity(IHomeContentView homeContentView, PlaceController placeController, ILoginView.Presenter loginPresenter, 
			ISemanticMarkupServiceAsync semanticMarkupService,
			IMatrixGenerationServiceAsync matrixGenerationService,
			ITaxonomyComparisonServiceAsync taxonomyComparisonService,
			ITreeGenerationServiceAsync treeGenerationService,
			IVisualizationServiceAsync visualizationService,
			IPipelineServiceAsync pipelineService,
			IMessageConfirmView.Presenter messageConfirmPresenter, 
			ResumeTaskPlaceMapper resumeTaskPlaceMapper) {
		this.homeContentView = homeContentView;
    	this.placeController = placeController;
    	this.loginPresenter = loginPresenter;
		this.semanticMarkupService = semanticMarkupService;
		this.matrixGenerationService = matrixGenerationService;
		this.taxonomyComparisonService = taxonomyComparisonService;
		this.treeGenerationService = treeGenerationService;
		this.visualizationService = visualizationService;
		this.messageConfirmPresenter = messageConfirmPresenter;
		this.resumeTaskPlaceMapper = resumeTaskPlaceMapper;
		this.pipelineService = pipelineService;
    }

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		homeContentView.setPresenter(this);
		panel.setWidget(homeContentView.asWidget());
	}

	@Override
	public void onMatrixGeneration() {		
		checkLogin(new MatrixGenerationInputPlace(), matrixGenerationService);
	}

	@Override
	public void onSemanticMarkup() {
		checkLogin(new SemanticMarkupInputPlace(), semanticMarkupService);
	}

	@Override
	public void onTaxonomyComparison() {
		checkLogin(new TaxonomyComparisonPlace(), taxonomyComparisonService);
	}

	@Override
	public void onVisualization() {
		checkLogin(new VisualizationPlace(), visualizationService);
	}

	@Override
	public void onPipeline() {
		checkLogin(new PipelinePlace(), pipelineService);
	}

	@Override
	public void onTreeGeneration() {
		checkLogin(new TreeGenerationPlace(), treeGenerationService);
	}
	
	private void checkLogin(final HasTaskPlace gotoPlace, final IHasTasksServiceAsync tasksService) {
		if(Authentication.getInstance().isSet()) {
			placeController.goTo(new LoggedInPlace());
			this.doGotoPlace(gotoPlace, tasksService);
		} else {
			loginPresenter.show(new ILoginListener() {
				@Override
				public void onLogin() {
					placeController.goTo(new LoggedInPlace());
					HomeActivity.this.doGotoPlace(gotoPlace, tasksService);
				}
				@Override
				public void onLoginFailure() {
				}
			});
		}
	}
	
	private void doGotoPlace(final HasTaskPlace gotoPlace, IHasTasksServiceAsync tasksService) {
		tasksService.getLatestResumable(Authentication.getInstance().getToken(),
				new RPCCallback<Task>() {
			@Override
			public void onResult(final Task task) {
				if(task != null) 
					messageConfirmPresenter.show(
						"Resumable Task", "You have a resumable task of this type", "Start new", "Resume", new IConfirmListener() {
							public void onConfirm() {
								gotoPlace.setTask(task);
								placeController.goTo(gotoPlace);
							}
							public void onCancel() {
								placeController.goTo(gotoPlace);
							}
						});
				else 
					placeController.goTo(gotoPlace);
			}
		});
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
}
