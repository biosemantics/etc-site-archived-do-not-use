package edu.arizona.biosemantics.etcsite.shared.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class ConfigurationDAO {

	private static ConfigurationDAO instance;
	
	public Configuration getConfiguration(int id) throws SQLException, ClassNotFoundException, IOException {
		Configuration configuration = null;
		Query query = new Query("SELECT * FROM configurations WHERE id = ?");
		query.setParameter(1, id);
		ResultSet result = query.execute();
		while(result.next()) {
			id = result.getInt(1);
			Date created = result.getTimestamp(2);
			configuration = new Configuration(id, created);
		}
		query.close();
		return configuration;
	}
		
	public static ConfigurationDAO getInstance() {
		if(instance == null)
			instance = new ConfigurationDAO();
		return instance;
	}

	public void remove(edu.arizona.biosemantics.etcsite.shared.db.Configuration configuration) throws ClassNotFoundException, SQLException, IOException {
		Query query = new Query("DELETE FROM configurations WHERE id = ?");
		query.setParameter(1, configuration.getId());
		query.executeAndClose();
	}
}
