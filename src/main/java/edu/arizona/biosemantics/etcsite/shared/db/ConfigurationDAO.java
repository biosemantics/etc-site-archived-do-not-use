package edu.arizona.biosemantics.etcsite.shared.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import edu.arizona.biosemantics.etcsite.shared.db.Query.QueryException;

public class ConfigurationDAO {

	private static ConfigurationDAO instance;
	
	public Configuration getConfiguration(int id) {
		Configuration configuration = null;
		try(Query query = new Query("SELECT * FROM configurations WHERE id = ?")) {
			query.setParameter(1, id);
			ResultSet result = query.execute();
			while(result.next()) {
				id = result.getInt(1);
				Date created = result.getTimestamp(2);
				configuration = new Configuration(id, created);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return configuration;
	}
		
	public static ConfigurationDAO getInstance() {
		if(instance == null)
			instance = new ConfigurationDAO();
		return instance;
	}

	public void remove(edu.arizona.biosemantics.etcsite.shared.db.Configuration configuration) {
		try(Query query = new Query("DELETE FROM configurations WHERE id = ?")) {
			query.setParameter(1, configuration.getId());
			query.execute();
		} catch(QueryException e) {
			e.printStackTrace();
		}
	}
}
