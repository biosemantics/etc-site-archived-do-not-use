package edu.arizona.biosemantics.etcsite.shared.db.otolite;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.arizona.biosemantics.etcsite.shared.db.Query;

public class OrderCategoriesDAO {

	private static OrderCategoriesDAO instance;

	public static OrderCategoriesDAO getInstance() {
		if(instance == null)
			instance= new OrderCategoriesDAO();
		return instance;
	}

	/**
	 * @param uploadId
	 * @param category
	 * @return categoryID of inserted row or already matching entry
	 * @throws SQLException 
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public int addOrderCategory(int uploadId, String categoryName) throws SQLException, ClassNotFoundException, IOException {
		int id = -1;
		Query query = new Query("SELECT categoryID FROM order_categories WHERE uploadID = ? AND categoryName = ?", "otolite");
		query.setParameter(1, uploadId);
		query.setParameter(2, categoryName);
		query.execute();
		ResultSet result = query.getResultSet();
		if(result.next()) {
			id = result.getInt(1);
		} else {
			Query insertQuery = new Query("INSERT INTO order_categories (`uploadID`, `categoryName`) VALUES (?, ?)", "otolite");
			insertQuery.setParameter(1, uploadId);
			insertQuery.setParameter(2, categoryName);
			insertQuery.execute();
			ResultSet keySet = insertQuery.getGeneratedKeys();
			if(keySet.next()) {
				id = keySet.getInt(1);
			}
			insertQuery.close();
		}
		query.close();
		return id;
	}
	
}
