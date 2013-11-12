package edu.arizona.sirls.etc.site.client.view;

import java.util.List;

import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.shared.rpc.db.Task;

public interface TaskManagerView {

	public interface Presenter {
		void onShare(Task task);
		void onDelete(Task task);
		void onRewind(Task task);
		void onResume(Task task);
	}
	  
	void setPresenter(Presenter presenter);
	Widget asWidget();
	void setTasks(List<Task> tasks);
	void updateTask(Task task);
	void removeTask(Task tasks);
	void addTask(Task task);
	Task getSelectedTask();
	void resetSelection();
	
}
