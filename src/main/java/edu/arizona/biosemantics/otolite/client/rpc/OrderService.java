package edu.arizona.biosemantics.otolite.client.rpc;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.otolite.shared.beans.orders.OrderCategory;
import edu.arizona.biosemantics.otolite.shared.beans.orders.OrderSet;

@RemoteServiceRelativePath("orders")
public interface OrderService extends RemoteService {
	ArrayList<OrderSet> getOrderSets(String uploadID) throws Exception; 
	
	ArrayList<OrderCategory> getOrderCategories(String uploadID) throws Exception;
	
	void saveOrderSet(OrderSet orserSet) throws Exception;
	
	OrderSet getOrderSetByID(String categoryID) throws Exception;
}
