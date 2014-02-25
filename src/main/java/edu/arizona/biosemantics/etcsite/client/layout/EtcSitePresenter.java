package edu.arizona.biosemantics.etcsite.client.layout;

import com.google.gwt.activity.shared.MyActivity;
import com.google.gwt.activity.shared.MyActivityManager;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationActivity;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupActivity;
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.TaxonomyComparisonActivity;
import edu.arizona.biosemantics.etcsite.client.content.treeGeneration.TreeGenerationActivity;
import edu.arizona.biosemantics.etcsite.client.content.visualization.VisualizationActivity;
import edu.arizona.biosemantics.etcsite.client.menu.IMenuView;

public class EtcSitePresenter implements IEtcSiteView.Presenter {

	private IEtcSiteView view;
	private MyActivityManager contentActivityManager;
	private IMenuView menuView;

	@Inject
	public EtcSitePresenter(IEtcSiteView view, @Named("Content") MyActivityManager contentActivityManager, 
			IMenuView menuView) {
		this.view = view;
		view.setPresenter(this);
		this.contentActivityManager = contentActivityManager;
		this.menuView = menuView;
	}
	
	@Override
	public void onMouseOverHeader(MouseOverEvent event) {
		if(view.getMenuContainer().getWidget() instanceof IMenuView) {
			MyActivity activity = contentActivityManager.getCurrentActivity();
			if(activity instanceof SemanticMarkupActivity) {
				menuView.highlightSemanticMarkup();
			}
			if(activity instanceof MatrixGenerationActivity) {
				menuView.highlightMatrixGeneration();
			}
			if(activity instanceof TaxonomyComparisonActivity) {
				menuView.highlightTaxonomyComparison();
			}
			if(activity instanceof VisualizationActivity) {
				menuView.highlightVisualization();
			}
			if(activity instanceof TreeGenerationActivity) {
				menuView.highlightTreeGeneration();
			}
			view.setHeaderSize(100, true);
		}
	}

	@Override
	public void onMouseOutHeader(MouseOutEvent event) {
		if(view.getMenuContainer().getWidget() instanceof IMenuView) {
			//not in the area above of browser content, e.g. tabls, url
			//if(event.getY() >= 0) {
			//	collapseMenu();
			//}
			/*System.out.println("x/y" + event.getX() + " " +  event.getY());
			System.out.println("relative x/y" + event.getRelativeX(this.getElement()) + " " +  event.getRelativeY(this.getElement()));
			System.out.println("client x/y" + event.getClientX() + " " +  event.getClientY());
			System.out.println("screen x/y" + event.getScreenX() + " " +  event.getScreenY()); */
			view.setHeaderSize(38, true);
		}
	}

	@Override
	public IEtcSiteView getView() {
		return view;
	}

	@Override
	public void setHeaderSize(int size, boolean animated) {
		view.setHeaderSize(size, animated);
	}


}
