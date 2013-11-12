package edu.arizona.sirls.etc.site.client.view;

import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.ImageCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.TaskStageEnum;

public class TaskManagerViewImpl extends Composite implements TaskManagerView, Handler {

	private static TaskManagerViewUiBinder uiBinder = GWT.create(TaskManagerViewUiBinder.class);

	@UiTemplate("TaskManagerView.ui.xml")
	interface TaskManagerViewUiBinder extends UiBinder<Widget, TaskManagerViewImpl> {
	}

	@UiField(provided=true)
	CellTable<Task> taskTable;
	@UiField(provided=true)
	SimplePager pager;
	@UiField
	Button resumeButton;
	@UiField
	Button rewindButton;
	@UiField
	Button deleteButton;
	@UiField
	Button shareButton;
	
	private Presenter presenter;
	private ListDataProvider<Task> dataProvider;
	private ProvidesKey<Task> taskKeyProvider = new ProvidesKey<Task>() {
		@Override
		public Object getKey(Task item) {
			return item == null ? null : item.getId();
		}
	};
	private SingleSelectionModel<Task> selectionModel;

	public TaskManagerViewImpl() {
		dataProvider = new ListDataProvider<Task>();
		taskTable = createTaskTable();
	    dataProvider.addDataDisplay(taskTable);
		pager = createPager(taskTable);
		pager.setDisplay(taskTable);
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	private SimplePager createPager(CellTable<Task> cellTable) {
	    SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
	    SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		return pager;
	}
	
	private CellTable<Task> createTaskTable() {
		CellTable<Task> taskTable = new CellTable<Task>(taskKeyProvider);
		taskTable.setAutoHeaderRefreshDisabled(true);
		taskTable.setAutoFooterRefreshDisabled(true);
	    ListHandler<Task> sortHandler = new ListHandler<Task>(dataProvider.getList());
	    taskTable.addColumnSortHandler(sortHandler);
	    selectionModel = new SingleSelectionModel<Task>(taskKeyProvider);
	    taskTable.setSelectionModel(selectionModel);//, DefaultSelectionEventManager.<Task> createCheckboxManager());
	    initTableColumns(taskTable, selectionModel, sortHandler);
		selectionModel.addSelectionChangeHandler(this);
	    return taskTable;
	}

	private void initTableColumns(CellTable<Task> taskTable, SelectionModel<Task> selectionModel, ListHandler<Task> sortHandler) {
		TextCell nameCell = new TextCell();
		Column<Task, String> nameColumn = new Column<Task, String>(nameCell) {
		      @Override
		      public String getValue(Task object) {
		    	  return object.getName();
		      }
	    };
	    nameColumn.setSortable(true);
	    nameColumn.setDefaultSortAscending(false);
	    sortHandler.setComparator(nameColumn, new Comparator<Task>() {
	      @Override
	      public int compare(Task o1, Task o2) {
	        return o1.getName().compareTo(o2.getName());
	      }
	    });
	    taskTable.addColumn(nameColumn, "Name");
	    
		DateCell dateCell = new DateCell();
		Column<Task, Date> createdColumn = new Column<Task, Date>(dateCell) {
		      @Override
		      public Date getValue(Task object) {
		    	  return object.getCreated();
		      }
	    };
	    createdColumn.setSortable(true);
	    createdColumn.setDefaultSortAscending(false);
	    sortHandler.setComparator(createdColumn, new Comparator<Task>() {
	      @Override
	      public int compare(Task o1, Task o2) {
	        return o1.getCreated().compareTo(o2.getCreated());
	      }
	    });
	    taskTable.addColumn(createdColumn, "Created");
	    
		TextCell accessCell = new TextCell();
		Column<Task, String> accessColumn = new Column<Task, String>(accessCell) {
		      @Override
		      public String getValue(Task object) {
		    	  return object.getUser().getName().equals(Authentication.getInstance().getUsername()) ? "Owned" : "Shared";
		      }
	    };
	    accessColumn.setSortable(true);
	    accessColumn.setDefaultSortAscending(false);
	    sortHandler.setComparator(accessColumn, new Comparator<Task>() {
	      @Override
	      public int compare(Task o1, Task o2) {
	    	boolean sameUser = o1.getUser().equals(Authentication.getInstance().getUsername());
	    	if(sameUser)
	    		return 0;
	    	return -1;
	      }
	    });
	    taskTable.addColumn(accessColumn, "Access");
		
	    TextCell taskTypeCell = new TextCell();
		Column<Task, String> taskTypeColumn = new Column<Task, String>(taskTypeCell) {
		      @Override
		      public String getValue(Task object) {
		    	  return object.getTaskType().getTaskTypeEnum().displayName();
		      }
	    };
	    taskTypeColumn.setSortable(true);
	    taskTypeColumn.setDefaultSortAscending(false);
	    sortHandler.setComparator(taskTypeColumn, new Comparator<Task>() {
	      @Override
	      public int compare(Task o1, Task o2) {
	        return o1.getTaskType().getTaskTypeEnum().compareTo(o2.getTaskType().getTaskTypeEnum());
	      }
	    });
	    taskTable.addColumn(taskTypeColumn, "Task Type");
	    
		TextCell statusTextCell = new TextCell();
		Column<Task, String> statusTextColumn = new Column<Task, String>(statusTextCell) {
			@Override
			public String getValue(Task object) {
				if(object.isComplete())
					return "Completed";
				int j = 0;
				int x = 0;
				for (TaskStageEnum step : TaskStageEnum.values()) {
					j++;
					if (step.equals(object.getTaskStage().getTaskStageEnum())) {
						x = j;
					}
				}
				return "Step " + x + " of " + j + ": " + object.getTaskStage().getTaskStageEnum().displayName();
			}
		};
		Column<Task, String> statusImageColumn = new Column<Task, String>(new ImageCell()) {
			@Override
			public String getValue(Task object) {
				if(!object.isComplete() && !object.isResumable())
					return "images/loader3.gif";
				else return null;
			} 
		};
		List<HasCell<Task, ?>> columns = new LinkedList<HasCell<Task, ?>>();
		columns.add(statusTextColumn);
		columns.add(statusImageColumn);
		CompositeCell<Task> statusCell = new CompositeCell<Task>(columns);
		Column<Task, Task> statusColumn = new Column<Task, Task>(statusCell) {
			@Override
			public Task getValue(Task object) {
				return object;
			}
	    };
	    statusColumn.setSortable(true);
	    statusColumn.setDefaultSortAscending(false);
	    sortHandler.setComparator(statusColumn, new Comparator<Task>() {
		      @Override
		      public int compare(Task o1, Task o2) {
		        return o1.getTaskStage().getTaskStageEnum().compareTo(o2.getTaskStage().getTaskStageEnum());
		      }
	    });
	    taskTable.addColumn(statusColumn, "Status");
	    
	    /*Column<Task, String> actionResumeColumn = new Column<Task, String>(new ImageCell()) {
			@Override
			public String getValue(Task object) {
				if(object.isResumable())
					return "images/play.png";
				else return null;
			} 
		};
	    Column<Task, String> actionRewindColumn = new Column<Task, String>(new ImageCell()) {
			@Override
			public String getValue(Task object) {
				if(object.isComplete())
					switch(object.getTaskStage().getTaskType().getTaskTypeEnum()) {
						case MATRIX_GENERATION:
							return "images/rewind.png";
						case TAXONOMY_COMPARISON:
							break;
						case TREE_GENERATION:
							break;
						case VISUALIZATION:
							break;
						default:
					}
				return null;
			} 
		};
	    Column<Task, String> actionCancelColumn = new Column<Task, String>(new ImageCell()) {
			@Override
			public String getValue(Task object) {
				return "images/revoke.jpg";
			} 
		};
	    Column<Task, String> actionShareColumn = new Column<Task, String>(new ImageCell()) {
			@Override
			public String getValue(Task object) {
				if(object.getUser().getName().equals(Authentication.getInstance().getUsername()))
					return "images/share.png";
				else return null;
			} 
		};
		columns = new LinkedList<HasCell<Task, ?>>();
		columns.add(actionResumeColumn);
		columns.add(actionRewindColumn);
		columns.add(actionCancelColumn);
		columns.add(actionShareColumn);
		
		CompositeCell<Task> actionsCell = new CompositeCell<Task>(columns);
		Column<Task, Task> actionsColumn = new Column<Task, Task>(actionsCell) {
			@Override
			public Task getValue(Task object) {
				return object;
			}
	    };
	    taskTable.addColumn(actionsColumn, "Actions");
	    */
	    		
		/*taskService.getAllTasks(Authentication.getInstance().getAuthenticationToken(), new AsyncCallback<RPCResult<List<Task>>>() {
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
			@Override
			public void onSuccess(RPCResult<List<Task>> result) {
				if(result.isSucceeded()) {
					taskTable.setRowCount(result.getData().size(), true);
					taskTable.setRowData(0, result.getData());
				}
			}
		}); */
	}
	

	
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void setTasks(List<Task> tasks) {
		List<Task> tasksList = dataProvider.getList();
		tasksList.clear();
		tasksList.addAll(tasks);
	}

	@Override
	public void updateTask(Task task) {
		List<Task> tasks = dataProvider.getList();
		Iterator<Task> tasksIterator = tasks.iterator();
		
		boolean found = false;
		while(tasksIterator.hasNext()) {
			Task listTask = tasksIterator.next();
			if(listTask.getId()==task.getId()) {
				tasksIterator.remove();
				found = true;
			}
		}
		if(found)
			tasks.add(task);
	}

	@Override
	public void removeTask(Task task) {
		List<Task> tasks = dataProvider.getList();
		Iterator<Task> tasksIterator = tasks.iterator();
		while(tasksIterator.hasNext()) {
			Task listTask = tasksIterator.next();
			if(listTask.getId()==task.getId()) {
				tasksIterator.remove();
			}
		}
	}

	@Override
	public void addTask(Task task) {
		List<Task> tasks = dataProvider.getList();
		tasks.add(task);
	}

	@Override
	public Task getSelectedTask() {
		return selectionModel.getSelectedObject();
	}
	
	@UiHandler("resumeButton")
	public void onResume(ClickEvent e) {
		presenter.onResume(this.getSelectedTask());
	}
	
	
	@UiHandler("rewindButton")
	public void onRewind(ClickEvent e) {
		presenter.onRewind(this.getSelectedTask());
	}
	
	@UiHandler("deleteButton")
	public void onDelete(ClickEvent e) {
		presenter.onDelete(this.getSelectedTask());
	}
	
	@UiHandler("shareButton")
	public void onShare(ClickEvent e) {
		presenter.onShare(this.getSelectedTask());
	}

	@Override
	public void onSelectionChange(SelectionChangeEvent event) {
		if(!this.getSelectedTask().getUser().getName().equals(Authentication.getInstance().getUsername())) {
			this.shareButton.setEnabled(false);
		} else {
			this.shareButton.setEnabled(true);
		}
		if(this.getSelectedTask().isComplete()) {
			this.rewindButton.setEnabled(true);
			this.resumeButton.setEnabled(false);
		} else {
			this.rewindButton.setEnabled(false);
			this.resumeButton.setEnabled(true);
		}
	}

	@Override
	public void resetSelection() {
		this.selectionModel.clear();
	}
	
}
