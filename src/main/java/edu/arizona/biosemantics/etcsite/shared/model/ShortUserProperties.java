package edu.arizona.biosemantics.etcsite.shared.model;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface ShortUserProperties extends PropertyAccess<ShortUser> {
	
  @Path("id")
  ModelKeyProvider<ShortUser> key();
   
  @Path("email")
  LabelProvider<ShortUser> emailLabel();
 
  ValueProvider<ShortUser, String> email();
  
  ValueProvider<ShortUser, String> firstName();
  
  ValueProvider<ShortUser, String> lastName();
  
  ValueProvider<ShortUser, String> affiliation();

  ValueProvider<ShortUser, String> openIdProvider();
  
  ValueProvider<ShortUser, String> openIdProviderId();
  
  ValueProvider<ShortUser, String> bioportalUserId();
  
  ValueProvider<ShortUser, String> bioportalApiKey();
  
  ValueProvider<ShortUser, String> otoAccountEmail();
  
  ValueProvider<ShortUser, String> fullName();
  
  ValueProvider<ShortUser, String> fullNameEmail();
  
}