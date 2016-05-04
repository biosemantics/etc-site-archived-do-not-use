package edu.arizona.biosemantics.etcsite.client.content.taskManager;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public interface ITaskManagerView extends IsWidget {

	public interface Presenter {
		void onShare(TaskData taskData);
		void onDelete(TaskData taskData);
		void onDelete(List<TaskData> list);
		void onRewind(TaskData taskData);
		//void onRewind(TaskData taskData, String string);
		void onResume(TaskData taskData);
		IsWidget getView();
		void refresh();
	}
	  
	void setPresenter(Presenter presenter);
	Widget asWidget();
	void updateTaskData(TaskData taskData);
	void setTaskData(List<TaskData> taskData);
	void removeTaskData(TaskData taskData);
	void addTaskData(TaskData taskData);
	List<TaskData> getSelectedTaskData();
	void resetSelection();
	
}
