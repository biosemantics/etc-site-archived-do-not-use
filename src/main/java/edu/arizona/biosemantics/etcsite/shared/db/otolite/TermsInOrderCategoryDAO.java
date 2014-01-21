package edu.arizona.biosemantics.etcsite.shared.db.otolite;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.arizona.biosemantics.etcsite.shared.db.Query;

public class TermsInOrderCategoryDAO {

	private static TermsInOrderCategoryDAO instance;

	public static TermsInOrderCategoryDAO getInstance() {
		if(instance == null)
			instance= new TermsInOrderCategoryDAO();
		return instance;
	}
	
	public void addTermsInOrderCategory(int categoryId, String termName) throws SQLException, ClassNotFoundException, IOException {
		//only if not contained anyway. Make {categoryID, termName} unique in DB
		Query query = new Query("SELECT termID FROM `terms_in_order_category` WHERE categoryId = ? AND termName = ?", "otolite");
		query.setParameter(1, categoryId);
		query.setParameter(2, termName);
		ResultSet resultSet = query.execute();
		if(!resultSet.next()) {
			Query insertQuery = new Query("INSERT INTO `terms_in_order_category` (`categoryID`, `termName`) VALUES (?, ?)", "otolite");
			insertQuery.setParameter(1, categoryId);
			insertQuery.setParameter(2, termName);
			insertQuery.executeAndClose();
		}
		query.close();
	}
}
