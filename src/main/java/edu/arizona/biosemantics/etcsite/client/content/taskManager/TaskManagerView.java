package edu.arizona.biosemantics.etcsite.client.content.taskManager;

import java.util.Collections;
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
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.OrderedMultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SelectionModel;

import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.shared.model.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum;

public class TaskManagerView extends Composite implements ITaskManagerView, Handler {

	private static TaskManagerViewUiBinder uiBinder = GWT.create(TaskManagerViewUiBinder.class);

	@UiTemplate("TaskManagerView.ui.xml")
	interface TaskManagerViewUiBinder extends UiBinder<Widget, TaskManagerView> {
	}

	@UiField(provided=true)
	CellTable<TaskData> taskTable;
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
	//@UiField
	//ListBox rewindListBox;
	
	private Presenter presenter;
	private ListDataProvider<TaskData> dataProvider;
	private ProvidesKey<TaskData> taskKeyProvider = new ProvidesKey<TaskData>() {
		@Override
		public Object getKey(TaskData item) {
			return item == null ? null : item.getTask().getId();
		}
	};
	private OrderedMultiSelectionModel<TaskData> selectionModel;

	public TaskManagerView() {
		dataProvider = new ListDataProvider<TaskData>();
		taskTable = createTaskTable();
	    dataProvider.addDataDisplay(taskTable);
		pager = createPager(taskTable);
		pager.setDisplay(taskTable);
		initWidget(uiBinder.createAndBindUi(this));
		resumeButton.setHeight("20px");
		rewindButton.setHeight("20px");
		deleteButton.setHeight("20px");
		shareButton.setHeight("20px");
		//rewindListBox.setEnabled(false);
	}
	
	private SimplePager createPager(CellTable<TaskData> cellTable) {
	    SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
	    SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		return pager;
	}
	
	private CellTable<TaskData> createTaskTable() {
		CellTable<TaskData> taskTable = new CellTable<TaskData>(taskKeyProvider);
		taskTable.setAutoHeaderRefreshDisabled(true);
		taskTable.setAutoFooterRefreshDisabled(true);
	    ListHandler<TaskData> sortHandler = new ListHandler<TaskData>(dataProvider.getList());
	    taskTable.addColumnSortHandler(sortHandler);
	    selectionModel = new OrderedMultiSelectionModel<TaskData>(taskKeyProvider);
	    taskTable.setSelectionModel(selectionModel);//, DefaultSelectionEventManager.<Task> createCheckboxManager());
	    initTableColumns(taskTable, selectionModel, sortHandler);
		selectionModel.addSelectionChangeHandler(this);
	    return taskTable;
	}

	private void initTableColumns(CellTable<TaskData> taskTable, SelectionModel<TaskData> selectionModel, ListHandler<TaskData> sortHandler) {
		TextCell nameCell = new TextCell();
		Column<TaskData, String> nameColumn = new Column<TaskData, String>(nameCell) {
		      @Override
		      public String getValue(TaskData object) {
		    	  return object.getTask().getName();
		      }
	    };
	    nameColumn.setSortable(true);
	    nameColumn.setDefaultSortAscending(false);
	    sortHandler.setComparator(nameColumn, new Comparator<TaskData>() {
	      @Override
	      public int compare(TaskData o1, TaskData o2) {
	        return o1.getTask().getName().compareTo(o2.getTask().getName());
	      }
	    });
	    taskTable.addColumn(nameColumn, "Name");
	    
		DateCell dateCell = new DateCell();
		Column<TaskData, Date> createdColumn = new Column<TaskData, Date>(dateCell) {
		      @Override
		      public Date getValue(TaskData object) {
		    	  return object.getTask().getCreated();
		      }
	    };
	    createdColumn.setSortable(true);
	    createdColumn.setDefaultSortAscending(false);
	    sortHandler.setComparator(createdColumn, new Comparator<TaskData>() {
	      @Override
	      public int compare(TaskData o1, TaskData o2) {
	        return o1.getTask().getCreated().compareTo(o2.getTask().getCreated());
	      }
	    });
	    taskTable.addColumn(createdColumn, "Created");
	    
		TextCell accessCell = new TextCell();
		Column<TaskData, String> accessColumn = new Column<TaskData, String>(accessCell) {
		      @Override
		      public String getValue(TaskData object) {
		    	  if(object.getTask().getUser().getId() == Authentication.getInstance().getUserId()) {
		    		  if(object.getInvitees() != null && !object.getInvitees().isEmpty()) {
		    			  String shared = "Shared with ";
		    			  for(ShortUser shortUser : object.getInvitees()) {
		    				  shared += shortUser.getFullNameEmailAffiliation() + ", ";
		    			  }
		    			  return "Owned. " + shared.substring(0, shared.length() - 2);
		    		  } else {
		    			  return ("Owned");
		    		  }
		    	  } else {
		    		  return "Shared by " + object.getTask().getUser().getFullNameEmailAffiliation();
		    	  }
		      }
	    };
	    accessColumn.setSortable(true);
	    accessColumn.setDefaultSortAscending(false);
	    sortHandler.setComparator(accessColumn, new Comparator<TaskData>() {
	      @Override
	      public int compare(TaskData o1, TaskData o2) {
	    	boolean sameUser = o1.getTask().getUser().getId() == Authentication.getInstance().getUserId();
	    	if(sameUser)
	    		return 0;
	    	return -1;
	      }
	    });
	    taskTable.addColumn(accessColumn, "Access");
		
	    TextCell taskTypeCell = new TextCell();
		Column<TaskData, String> taskTypeColumn = new Column<TaskData, String>(taskTypeCell) {
		      @Override
		      public String getValue(TaskData object) {
		    	  return object.getTask().getTaskType().getTaskTypeEnum().displayName();
		      }
	    };
	    taskTypeColumn.setSortable(true);
	    taskTypeColumn.setDefaultSortAscending(false);
	    sortHandler.setComparator(taskTypeColumn, new Comparator<TaskData>() {
	      @Override
	      public int compare(TaskData o1, TaskData o2) {
	        return o1.getTask().getTaskType().getTaskTypeEnum().compareTo(o2.getTask().getTaskType().getTaskTypeEnum());
	      }
	    });
	    taskTable.addColumn(taskTypeColumn, "Task Type");
	    
		TextCell statusTextCell = new TextCell();
		Column<TaskData, String> statusTextColumn = new Column<TaskData, String>(statusTextCell) {
			@Override
			public String getValue(TaskData object) {
				if(object.getTask().isComplete() && !object.getTask().isFailed())
					return "Completed";
				if(object.getTask().isFailed())
					return "Failed";
				return "Step " + object.getTask().getTaskStage().getTaskStageNumber() + " of " + object.getTask().getTaskStage().getMaxTaskStageNumber() + ": " + object.getTask().getTaskStage().getDisplayName();
			}
		};
		
		ImageCell statusImageCell = new ImageCell() {
			@Override
			public void render(Context context, String value, SafeHtmlBuilder sb) {
				if (value != null) 
					sb.appendHtmlConstant("<img src = '" + value + "' height = '20px' width = '20px' />");
			}
		};
		Column<TaskData, String> statusImageColumn = new Column<TaskData, String>(statusImageCell) {
			@Override
			public String getValue(TaskData object) {
				if(!object.getTask().isComplete() && !object.getTask().isResumable())
					return "images/loader3.gif";
				else return null;
			} 
		};
		List<HasCell<TaskData, ?>> columns = new LinkedList<HasCell<TaskData, ?>>();
		columns.add(statusTextColumn);
		columns.add(statusImageColumn);
		CompositeCell<TaskData> statusCell = new CompositeCell<TaskData>(columns);
		Column<TaskData, TaskData> statusColumn = new Column<TaskData, TaskData>(statusCell) {
			@Override
			public TaskData getValue(TaskData object) {
				return object;
			}
	    };
	    statusColumn.setSortable(true);
	    statusColumn.setDefaultSortAscending(false);
	    sortHandler.setComparator(statusColumn, new Comparator<TaskData>() {
		      @Override
		      public int compare(TaskData o1, TaskData o2) {
		        return o1.getTask().getTaskStage().compareTo(o2.getTask().getTaskStage());
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
	public void setTaskData(List<TaskData> taskData) {
		List<TaskData> taskDataList = dataProvider.getList();
		taskDataList.clear();
		taskDataList.addAll(taskData);
	}
	

	@Override
	public void updateTaskData(TaskData taskData) {
		List<TaskData> tasks = dataProvider.getList();
		Iterator<TaskData> tasksIterator = tasks.iterator();
		
		boolean select = false;
		TaskData foundTask = null;
		while(tasksIterator.hasNext()) {
			TaskData listTask = tasksIterator.next();
			if(listTask.getTask().getId()==taskData.getTask().getId()) {
				//tasksIterator.remove();
				foundTask = listTask;
				select = selectionModel.isSelected(listTask);
			}
		}
		if(foundTask != null) {
			Collections.replaceAll(tasks, foundTask, taskData);
			selectionModel.setSelected(taskData, select);
		}
		if (this.selectionModel.getSelectedSet().contains(taskData)) {
			this.onSelectionChange(null);
		}
	}

	@Override
	public void removeTaskData(TaskData taskData) {
		List<TaskData> tasks = dataProvider.getList();
		Iterator<TaskData> tasksIterator = tasks.iterator();
		while(tasksIterator.hasNext()) {
			TaskData listTask = tasksIterator.next();
			if(listTask.getTask().getId()==taskData.getTask().getId()) {
				tasksIterator.remove();
			}
		}
	}

	@Override
	public void addTaskData(TaskData taskData) {
		List<TaskData> taskDataList = dataProvider.getList();
		taskDataList.add(taskData);
	}

	@Override
	public List<TaskData> getSelectedTaskData() {
		return selectionModel.getSelectedList();
	}
	
	@UiHandler("resumeButton")
	public void onResume(ClickEvent e) {
		List<TaskData> list = this.getSelectedTaskData();
		if (list.size() == 1){
			presenter.onResume(list.get(0));
		} else {
			//multiple selections. Show an error message or do nothing. 
		}
	}
	
	
	@UiHandler("rewindButton")
	public void onRewind(ClickEvent e) {
		List<TaskData> list = this.getSelectedTaskData();
		if (list.size() == 1){
			presenter.onRewind(list.get(0));
			//presenter.onRewind(list.get(0), rewindListBox.getValue(rewindListBox.getSelectedIndex()));
		} else {
			//multiple selections. Show an error message or do nothing. 
		}
	}
	
	@UiHandler("deleteButton")
	public void onDelete(ClickEvent e) {
		List<TaskData> list = this.getSelectedTaskData();
		if (list.size() == 1){
			presenter.onDelete(list.get(0));
		} else if (list.size() != 0){
			presenter.onDelete(list);
		}
	}
	
	@UiHandler("shareButton")
	public void onShare(ClickEvent e) {
		List<TaskData> list = this.getSelectedTaskData();
		if (list.size() == 1){
			presenter.onShare(list.get(0));
		} else {
			//multiple selections. Show an error message or do nothing.
		}
	}

	@Override
	public void onSelectionChange(SelectionChangeEvent event) {
		if(this.dataProvider.getList().isEmpty()) {
			this.resumeButton.setEnabled(false);
			setEnabledRewind(false);
			this.deleteButton.setEnabled(false);
			this.shareButton.setEnabled(false);
		} else {
			this.resumeButton.setEnabled(true);
			setEnabledRewind(true);
			this.deleteButton.setEnabled(true);
			this.shareButton.setEnabled(true);
		}
		
		if(this.getSelectedTaskData() != null) {
			List<TaskData> list = this.getSelectedTaskData();
			if (list.size() == 1){
				Task task = this.getSelectedTaskData().get(0).getTask();
				if(!task.isFailed()) {
					if(task.getUser().getId() != Authentication.getInstance().getUserId()) {
						this.shareButton.setEnabled(false);
					} else {
						this.shareButton.setEnabled(true);
					}
					if(task.isComplete()) {
						initRewind(task.getTaskType().getTaskTypeEnum());
						this.resumeButton.setEnabled(false);
					} else {
						setEnabledRewind(false);
						this.resumeButton.setEnabled(task.isResumable());
					}
				} else {
					this.resumeButton.setEnabled(false);
					setEnabledRewind(false);
					this.deleteButton.setEnabled(true);
					this.shareButton.setEnabled(true);
				}
			} else {
				//multiple selections. Only allow delete. 
				this.resumeButton.setEnabled(false);
				setEnabledRewind(false);
				this.deleteButton.setEnabled(true);
				this.shareButton.setEnabled(false);
			}
		}
	}

	private void setEnabledRewind(boolean value) {
		this.rewindButton.setEnabled(value);
		//if(!value)
		//	this.rewindListBox.clear();
		//this.rewindListBox.setEnabled(value);
	}

	private void initRewind(TaskTypeEnum taskTypeEnum) {
		//this.rewindListBox.clear();
		switch(taskTypeEnum) {
		case ONTOLOGIZE:
			this.rewindButton.setEnabled(true);
			break;
		case MATRIX_GENERATION:
			this.rewindButton.setEnabled(true);
			//this.rewindListBox.addItem(edu.arizona.biosemantics.etcsite.shared.model.matrixgeneration.TaskStageEnum.REVIEW.displayName());
			//this.rewindListBox.setEnabled(true);
			break;
		case SEMANTIC_MARKUP:
			this.rewindButton.setEnabled(true);
			//this.rewindListBox.addItem(
			//		edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.TaskStageEnum.REVIEW_TERMS.displayName());
			//this.rewindListBox.setEnabled(true);
			break;
		case TREE_GENERATION:
			this.rewindButton.setEnabled(true);
			//this.rewindListBox.addItem(edu.arizona.biosemantics.etcsite.shared.model.treegeneration.TaskStageEnum.VIEW.displayName());
			//this.rewindListBox.setEnabled(true);
			break;
		case TAXONOMY_COMPARISON:
			this.rewindButton.setEnabled(true);
			//this.rewindListBox.addItem(edu.arizona.biosemantics.etcsite.shared.model.taxonomycomparison.TaskStageEnum.ALIGN.displayName());
			//this.rewindListBox.setEnabled(true);
		case VISUALIZATION:
		default:
			this.rewindButton.setEnabled(false);
			//this.rewindListBox.clear();
			//this.rewindListBox.setEnabled(false);
			break;
		}
	}

	@Override
	public void resetSelection() {
		this.selectionModel.clear();
	}


	
}
