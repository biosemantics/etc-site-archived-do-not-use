package edu.arizona.biosemantics.etcsite.shared.model;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface TaskProperties extends PropertyAccess<Task> {
	
  @Path("id")
  ModelKeyProvider<Task> key();
   
  @Path("name")
  LabelProvider<Task> nameLabel();
   
  ValueProvider<Task, String> name();
  
  ValueProvider<Task, TaskStage> taskStage();
  
}