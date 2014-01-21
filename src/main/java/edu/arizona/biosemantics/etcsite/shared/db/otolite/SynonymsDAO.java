package edu.arizona.biosemantics.etcsite.shared.db.otolite;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import edu.arizona.biosemantics.etcsite.shared.db.Query;

public class SynonymsDAO {

	private static SynonymsDAO instance;

	public static SynonymsDAO getInstance() {
		if(instance == null)
			instance= new SynonymsDAO();
		return instance;
	}
	
	public List<String> getSynonyms(int uploadId, String mainTerm, String category) throws ClassNotFoundException, SQLException, IOException {
		List<String> result = new LinkedList<String>();
		Query query = new Query("SELECT synonym FROM synonyms WHERE uploadID = ? AND mainTerm = ? AND category = ?", "otolite");
		query.setParameter(1, uploadId);
		query.setParameter(2, mainTerm);
		query.setParameter(3, category);
		query.execute();
		ResultSet resultSet = query.getResultSet();
		while(resultSet.next()) {
			String synonym = result.get(1);
			result.add(synonym);
		}
		query.close();
		return result;
	}
	
}
