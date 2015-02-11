package edu.arizona.biosemantics.etcsite.client.content.home;

import com.google.gwt.activity.shared.MyAbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.HasTaskPlace;
import edu.arizona.biosemantics.etcsite.client.common.ILoginView;
import edu.arizona.biosemantics.etcsite.client.common.IRegisterView;
import edu.arizona.biosemantics.etcsite.client.common.IResetPasswordView;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationInputPlace;
import edu.arizona.biosemantics.etcsite.client.content.pipeline.PipelinePlace;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupInputPlace;
import edu.arizona.biosemantics.etcsite.client.content.taskManager.ResumeTaskPlaceMapper;
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.TaxonomyComparisonPlace;
import edu.arizona.biosemantics.etcsite.client.content.treeGeneration.TreeGenerationInputPlace;
import edu.arizona.biosemantics.etcsite.client.content.treeGeneration.TreeGenerationPlace;
import edu.arizona.biosemantics.etcsite.client.content.visualization.VisualizationPlace;
import edu.arizona.biosemantics.etcsite.client.top.LoggedInPlace;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.IHasTasksServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.IAuthenticationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.IMatrixGenerationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.pipeline.IPipelineServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.ISemanticMarkupServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison.ITaxonomyComparisonServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.treegeneration.ITreeGenerationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.visualization.IVisualizationServiceAsync;

public class HomeActivity extends MyAbstractActivity implements IHomeContentView.Presenter {

	private IHomeContentView homeContentView;
	private ISemanticMarkupServiceAsync semanticMarkupService;
	private IMatrixGenerationServiceAsync matrixGenerationService;
	private ResumeTaskPlaceMapper resumeTaskPlaceMapper;
	private IMatrixGenerationServiceAsync taxonomyComparisonService;
	private ITreeGenerationServiceAsync treeGenerationService;
	private IVisualizationServiceAsync visualizationService;
	private IPipelineServiceAsync pipelineService;

	@Inject
	public HomeActivity(IHomeContentView homeContentView, 
			ISemanticMarkupServiceAsync semanticMarkupService,
			IMatrixGenerationServiceAsync matrixGenerationService,
			IMatrixGenerationServiceAsync taxonomyComparisonService,
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
		tryGotoPlace(new TreeGenerationInputPlace(), treeGenerationService);
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
				new AsyncCallback<Task>() {
			@Override
			public void onSuccess(final Task task) {
				if(task != null) {
					MessageBox resumable = Alerter.resumableTask();
					resumable.getButton(PredefinedButton.YES).addSelectHandler(new SelectHandler() {
						@Override
						public void onSelect(SelectEvent event) {
							gotoPlace.setTask(task);
							placeController.goTo(gotoPlace);
						}
					});
					resumable.getButton(PredefinedButton.NO).addSelectHandler(new SelectHandler() {
						@Override
						public void onSelect(SelectEvent event) {
							placeController.goTo(gotoPlace);
						}
					});
				}
				else {
					placeController.goTo(gotoPlace);
				}
			}
			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToGetLatestResumable(caught);
			}
		});
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
}
