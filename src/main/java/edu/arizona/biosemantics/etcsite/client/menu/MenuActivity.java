package edu.arizona.biosemantics.etcsite.client.menu;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.IMessageConfirmView;
import edu.arizona.biosemantics.etcsite.client.common.MessageConfirmPresenter.IConfirmListener;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationInputPlace;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupInputPlace;
import edu.arizona.biosemantics.etcsite.client.content.taskManager.ResumeTaskPlaceMapper;
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.TaxonomyComparisonPlace;
import edu.arizona.biosemantics.etcsite.client.content.treeGeneration.TreeGenerationPlace;
import edu.arizona.biosemantics.etcsite.client.content.visualization.VisualizationPlace;
import edu.arizona.biosemantics.etcsite.client.menu.IMenuView.Presenter;
import edu.arizona.biosemantics.etcsite.shared.db.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.IMatrixGenerationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.ISemanticMarkupServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCCallback;

public class MenuActivity extends AbstractActivity implements Presenter {

	private IMenuView menuView;
	private PlaceController placeController;
	private ISemanticMarkupServiceAsync semanticMarkupService;
	private IMatrixGenerationServiceAsync matrixGenerationService;
	private IMessageConfirmView.Presenter messageConfirmPresenter;
	private ResumeTaskPlaceMapper resumeTaskPlaceMapper;

	@Inject
	public MenuActivity(IMenuView menuView, PlaceController placeController, 
			ISemanticMarkupServiceAsync semanticMarkupService,
			IMatrixGenerationServiceAsync matrixGenerationService, 
			IMessageConfirmView.Presenter messageConfirmPresenter, 
			ResumeTaskPlaceMapper resumeTaskPlaceMapper) {
		this.menuView = menuView;
		this.placeController = placeController;
		this.semanticMarkupService = semanticMarkupService;
		this.matrixGenerationService = matrixGenerationService;
		this.messageConfirmPresenter = messageConfirmPresenter;
		this.resumeTaskPlaceMapper = resumeTaskPlaceMapper;
	}
	
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		menuView.setPresenter(this);
		panel.setWidget(menuView.asWidget());
	}

	@Override
	public void onSemanticMarkup() {		
		semanticMarkupService.getLatestResumable(Authentication.getInstance().getToken(),
				new RPCCallback<Task>() {
			@Override
			public void onResult(final Task task) {
				messageConfirmPresenter.show(
					"Resumable Task", "You have a resumable task of this type", "Start new", "Resume", new IConfirmListener() {
						public void onConfirm() {
							placeController.goTo(resumeTaskPlaceMapper.getPlace(task));
						}
						public void onCancel() {
							placeController.goTo(new SemanticMarkupInputPlace());
						}
					});
			}
		});
	}

	@Override
	public void onMatrixGeneration() {
		matrixGenerationService.getLatestResumable(Authentication.getInstance().getToken(),
				new RPCCallback<Task>() {
			@Override
			public void onResult(final Task task) {
				messageConfirmPresenter.show(
					"Resumable Task", "You have a resumable task of this type", "Start new", "Resume", new IConfirmListener() {
						public void onConfirm() {
							placeController.goTo(resumeTaskPlaceMapper.getPlace(task));
						}
						public void onCancel() {
							placeController.goTo(new MatrixGenerationInputPlace());
						}
					});
			}
		});
	}

	@Override
	public void onTreeGeneration() {
		placeController.goTo(new TreeGenerationPlace());
	}

	@Override
	public void onTaxonomyComparison() {
		placeController.goTo(new TaxonomyComparisonPlace());
	}

	@Override
	public void onVisualization() {
		placeController.goTo(new VisualizationPlace());
	}

}
