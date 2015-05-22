package edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.content.taskManager.TaskManagerPlace;
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.IProcessingView.IOnTaskManagerListener;

public class ProcessingPresenter implements IProcessingView.Presenter {

	private IProcessingView view;
	private PlaceController placeController;
	private List<IOnTaskManagerListener> onTaskManagerListerners = new LinkedList<IOnTaskManagerListener>();

	@Inject
	public ProcessingPresenter(IProcessingView view, PlaceController placeController) {
		this.view = view;
		view.setPresenter(this);
		this.placeController = placeController;
	}

	
	@Override
	public IProcessingView getView() {
		return view;
	}


	@Override
	public void onTaskManager() {
		for(IOnTaskManagerListener listener : onTaskManagerListerners)
			listener.onTaskManager();
		placeController.goTo(new TaskManagerPlace());
	}


	@Override
	public void addOnTaskManagerListener(IOnTaskManagerListener listener) {
		onTaskManagerListerners .add(listener);
	}

}
