package edu.arizona.sirls.etc.site.client.view.taskManager;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.shared.rpc.db.ShortUser;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;

public interface TaskManagerView {

	public interface Presenter {
		void onShare(TaskData taskData);
		void onDelete(TaskData taskData);
		void onRewind(TaskData taskData);
		void onResume(TaskData taskData);
	}
	  
	void setPresenter(Presenter presenter);
	Widget asWidget();
	void setTaskData(List<TaskData> taskData);
	void updateTaskData(TaskData taskData);
	void removeTaskData(TaskData taskData);
	void addTaskData(TaskData taskData);
	TaskData getSelectedTaskData();
	void resetSelection();
	
}
