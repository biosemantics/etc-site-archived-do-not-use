package edu.arizona.biosemantics.etcsite.shared.db.otolite;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import edu.arizona.biosemantics.etcsite.shared.db.Query;

public class TermCategoryPairDAO {
	
	private static TermCategoryPairDAO instance;

	public static TermCategoryPairDAO getInstance() {
		if(instance == null)
			instance= new TermCategoryPairDAO();
		return instance;
	}
	
	public void addTermCategoryPair(int uploadId, String term, String category, List<String> synonyms) throws SQLException, ClassNotFoundException, IOException {
		StringBuilder synonymBuilder = new StringBuilder();
		for(String synonym : synonyms) 
			synonymBuilder.append(synonym + ",");
		String synonymString = synonymBuilder.toString();
		if(synonymString.length() > 0)
			synonymString = synonymString.substring(0, synonymString.length() - 1);
		//only if not contained anyway. Make {term, category, uploadID} unique in DB
		Query query = new Query("SELECT ID FROM `term_category_pair` WHERE term = ? AND category = ? AND uploadID = ?", "otolite");
		query.setParameter(1, term);
		query.setParameter(2, category);
		query.setParameter(3, uploadId);
		ResultSet resultSet = query.execute();
		if(!resultSet.next()) {
			Query insertQuery = new Query("INSERT INTO `term_category_pair` (`term`, `category`, `synonyms`, `uploadID`) VALUES (?, ?, ?, ?)", "otolite");
			insertQuery.setParameter(1, term);
			insertQuery.setParameter(2, category);
			insertQuery.setParameter(3, synonymString);
			insertQuery.setParameter(4, uploadId);
			insertQuery.executeAndClose();
		}
		query.close();	
	}
	
}
