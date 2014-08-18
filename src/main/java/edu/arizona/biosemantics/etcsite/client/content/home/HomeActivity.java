package edu.arizona.biosemantics.etcsite.client.content.home;

import com.google.gwt.activity.shared.MyAbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.HasTaskPlace;
import edu.arizona.biosemantics.etcsite.client.common.ILoginView;
import edu.arizona.biosemantics.etcsite.client.common.IRegisterView;
import edu.arizona.biosemantics.etcsite.client.common.IResetPasswordView;
import edu.arizona.biosemantics.etcsite.client.common.MessagePresenter.IConfirmListener;
import edu.arizona.biosemantics.etcsite.client.common.MessagePresenter;
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
import edu.arizona.biosemantics.etcsite.shared.rpc.IAuthenticationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.IHasTasksServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.IPipelineServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.ITaxonomyComparisonServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.ITreeGenerationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.IVisualizationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCCallback;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.IMatrixGenerationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.ISemanticMarkupServiceAsync;

public class HomeActivity extends MyAbstractActivity implements IHomeContentView.Presenter {

	private IHomeContentView homeContentView;
	private ISemanticMarkupServiceAsync semanticMarkupService;
	private IMatrixGenerationServiceAsync matrixGenerationService;
	private ResumeTaskPlaceMapper resumeTaskPlaceMapper;
	private ITaxonomyComparisonServiceAsync taxonomyComparisonService;
	private ITreeGenerationServiceAsync treeGenerationService;
	private IVisualizationServiceAsync visualizationService;
	private IPipelineServiceAsync pipelineService;
	private MessagePresenter messagePresenter = new MessagePresenter();

	@Inject
	public HomeActivity(IHomeContentView homeContentView, 
			ISemanticMarkupServiceAsync semanticMarkupService,
			IMatrixGenerationServiceAsync matrixGenerationService,
			ITaxonomyComparisonServiceAsync taxonomyComparisonService,
			ITreeGenerationServiceAsync treeGenerationService,
			IVisualizationServiceAsync visualizationService,
			IPipelineServiceAsync pipelineService,
			ResumeTaskPlaceMapper resumeTaskPlaceMapper, 
			PlaceController placeController,
			IAuthenticationServiceAsync authenticationService, 
			ILoginView.Presenter loginPresenter, 
			IRegisterView.Presenter registerPresenter, 
			IResetPasswordView.Presenter resetPasswordPresenter) {
		super(placeController, authenticationService, loginPresenter, registerPresenter, resetPasswordPresenter);
		this.homeContentView = homeContentView;
		this.semanticMarkupService = semanticMarkupService;
		this.matrixGenerationService = matrixGenerationService;
		this.taxonomyComparisonService = taxonomyComparisonService;
		this.treeGenerationService = treeGenerationService;
		this.visualizationService = visualizationService;
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
		tryGotoPlace(new MatrixGenerationInputPlace(), matrixGenerationService);
	}

	@Override
	public void onSemanticMarkup() {
		tryGotoPlace(new SemanticMarkupInputPlace(), semanticMarkupService);
	}

	@Override
	public void onTaxonomyComparison() {
		tryGotoPlace(new TaxonomyComparisonPlace(), taxonomyComparisonService);
	}

	@Override
	public void onVisualization() {
		tryGotoPlace(new VisualizationPlace(), visualizationService);
	}

	@Override
	public void onPipeline() {
		tryGotoPlace(new PipelinePlace(), pipelineService);
	}

	@Override
	public void onTreeGeneration() {
		tryGotoPlace(new TreeGenerationPlace(), treeGenerationService);
	}
	
	private void tryGotoPlace(final HasTaskPlace gotoPlace, final IHasTasksServiceAsync tasksService) {
		requireLogin(new LoggedInListener() {
			@Override
			public void onLoggedIn() {
				//when the user is logged in, go to the task place. 
				placeController.goTo(new LoggedInPlace());
				HomeActivity.this.doGotoPlace(gotoPlace, tasksService);
			}

			@Override
			public void onCancel() {
				//if the user cancels login, do nothing. 
			}

		});
	}
	
	private void doGotoPlace(final HasTaskPlace gotoPlace, IHasTasksServiceAsync tasksService) {
		tasksService.getLatestResumable(Authentication.getInstance().getToken(),
				new RPCCallback<Task>() {
			@Override
			public void onResult(final Task task) {
				if(task != null) {
					messagePresenter.showOkCandelBox(
							"Resumable Task", "You have a resumable task of this type", "Start new", "Resume", new IConfirmListener() {
							public void onConfirm() {
								gotoPlace.setTask(task);
								placeController.goTo(gotoPlace);
							}
							public void onCancel() {
								placeController.goTo(gotoPlace);
							}
						});
				}
				else {
					placeController.goTo(gotoPlace);
				}
			}
		});
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
}
