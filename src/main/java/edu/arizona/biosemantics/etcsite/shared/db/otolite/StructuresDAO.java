package edu.arizona.biosemantics.etcsite.shared.db.otolite;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.arizona.biosemantics.etcsite.shared.db.Query;

public class StructuresDAO {

	private static StructuresDAO instance;

	public static StructuresDAO getInstance() {
		if(instance == null)
			instance= new StructuresDAO();
		return instance;
	}
	
	public void addStructure(int uploadID, String term) throws ClassNotFoundException, SQLException, IOException {
		//only if not contained anyway. Make {uploadId, term} unique in DB
		Query query = new Query("SELECT ID FROM `structures` WHERE uploadID = ? AND term = ?", "otolite");
		query.setParameter(1, uploadID);
		query.setParameter(2, term);
		ResultSet resultSet = query.execute();
		if(!resultSet.next()) {
			Query insertQuery = new Query("INSERT INTO `structures` (`uploadID`, `term`) VALUES (?, ?)", "otolite");
			insertQuery.setParameter(1, uploadID);
			insertQuery.setParameter(2, term);
			insertQuery.executeAndClose();
		}
		query.close();
	}
}
