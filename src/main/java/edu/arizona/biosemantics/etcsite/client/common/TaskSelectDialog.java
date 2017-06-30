package edu.arizona.biosemantics.etcsite.client.common;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.TaskProperties;

public class TaskSelectDialog extends Dialog {
	
	public static interface ISelectListener {
		void onSelect(List<Task> selection);

		void onCancel();
	}
	
	private ISelectListener currentListener;
	private final TaskProperties taskProperties = GWT.create(TaskProperties.class);
	private ListStore<Task> taskStore = new ListStore<Task>(taskProperties.key());
	private ListView<Task, String> listView;
	
	@Inject
	public TaskSelectDialog(SelectionMode selectionMode) {
		setPredefinedButtons(PredefinedButton.OK);
		setHeading("Select Task");
		setBodyBorder(false);
		setPixelSize(-1, -1);
		setMinWidth(0);
		setMinHeight(0);
	    setResizable(true);
	    setShadow(true);
	    setClosable(true);
		setHideOnButtonClick(true);
		setWidth(250);
		setHeight(300);
		getButton(PredefinedButton.OK).setText("Share with selected");

		taskStore.addSortInfo(new StoreSortInfo<Task>(taskProperties.name(), SortDir.ASC));
		
		listView = new ListView<Task, String>(taskStore, new ValueProvider<Task, String>() {
			@Override
			public String getValue(Task object) {
				return object.getName() + " at step " + object.getTaskStage().getDisplayName();
			}
			@Override
			public void setValue(Task object, String value) {}
			@Override
			public String getPath() {
				return "task";
			}			
		});
		listView.getSelectionModel().setSelectionMode(selectionMode);
		add(listView);

		this.setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL);
		this.getButton(PredefinedButton.OK).addSelectHandler(
				new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						if(listView.getSelectionModel().getSelectedItems().isEmpty()) {
							Alerter.selectATask();
							return;
						}
						if(currentListener != null)
							currentListener.onSelect(getSelectedUsers());
						hide();
					}
				});
		this.getButton(PredefinedButton.CANCEL).addSelectHandler(
				new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						if(currentListener != null)
							currentListener.onCancel();
						hide();
					}
				});
	}
		
	public List<Task> getSelectedUsers() {
		return new LinkedList<Task>(listView.getSelectionModel().getSelectedItems());
	}

	public void show(List<Task> tasks, ISelectListener listener) {
		taskStore.clear();
		taskStore.addAll(tasks);
		this.currentListener = listener;
		show();
	}

}