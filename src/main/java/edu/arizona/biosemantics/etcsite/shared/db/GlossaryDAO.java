package edu.arizona.biosemantics.etcsite.shared.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class GlossaryDAO {

	private static GlossaryDAO instance;
	
	public Glossary getGlossary(int id) throws SQLException, ClassNotFoundException, IOException {
		Glossary glossary = null;
		Query query = new Query("SELECT * FROM glossaries WHERE id = ?");
		query.setParameter(1, id);
		ResultSet result = query.execute();
		while(result.next()) {
			id = result.getInt(1);
			String name = result.getString(2);
			Date created = result.getTimestamp(3);
			glossary = new Glossary(id, name, created);
		}
		query.close();
		return glossary;
	}
	
	public Glossary getGlossary(String name) throws SQLException, ClassNotFoundException, IOException {		
		Glossary glossary = null;
		Query query = new Query("SELECT * FROM glossaries WHERE name = ?");
		query.setParameter(1, name);
		ResultSet result = query.execute();
		while(result.next()) {
			int id = result.getInt(1);
			name = result.getString(2);
			Date created = result.getTimestamp(3);
			glossary = new Glossary(id, name, created);
		}
		query.close();
		return glossary;
	}
		
	public static GlossaryDAO getInstance() {
		if(instance == null)
			instance = new GlossaryDAO();
		return instance;
	}
}
