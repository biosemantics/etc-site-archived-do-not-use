package edu.arizona.sirls.etc.site.shared.rpc.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GlossaryDAO extends AbstractDAO {

	private static GlossaryDAO instance;

	public GlossaryDAO() throws ClassNotFoundException, SQLException, IOException {
		super();
	}
	
	public Glossary getGlossary(int id) throws SQLException, ClassNotFoundException, IOException {
		Glossary glossary = null;
		Query query = new Query("SELECT * FROM glossaries WHERE id = " + id);
		query.execute();
		ResultSet result = query.getResultSet();
		while(result.next()) {
			id = result.getInt(1);
			String name = result.getString(2);
			long created = result.getLong(3);
			glossary = new Glossary(id, name, created);
		}
		query.close();
		return glossary;
	}
	
	public Glossary getGlossary(String name) throws SQLException, ClassNotFoundException, IOException {		
		Glossary glossary = null;
		Query query = new Query("SELECT * FROM glossaries WHERE name = '" + name + "'");
		query.execute();
		ResultSet result = query.getResultSet();
		while(result.next()) {
			int id = result.getInt(1);
			name = result.getString(2);
			long created = result.getLong(3);
			glossary = new Glossary(id, name, created);
		}
		query.close();
		return glossary;
	}
		
	public static GlossaryDAO getInstance() throws ClassNotFoundException, IOException, SQLException {
		if(instance == null)
			instance = new GlossaryDAO();
		return instance;
	}
}
