package edu.arizona.sirls.etc.site.shared.rpc.db;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GlossaryDAO extends AbstractDAO {

	private static GlossaryDAO instance;

	public GlossaryDAO() throws IOException, ClassNotFoundException {
		super();
	}
	
	public Glossary getGlossary(int id) throws SQLException {
		Glossary glossary = null;
		this.openConnection();
		PreparedStatement statement = this.executeSQL("SELECT * FROM glossaries WHERE id = " + id);
		ResultSet result = statement.getResultSet();
		
		while(result.next()) {
			id = result.getInt(0);
			String name = result.getString(1);
			long created = result.getLong(2);
			glossary = new Glossary(id, name, created);
		}
		this.closeConnection();
		return glossary;
	}
	
	public Glossary getGlossary(String name) throws SQLException {		
		Glossary glossary = null;
		this.openConnection();
		PreparedStatement statement = this.executeSQL("SELECT * FROM glossaries WHERE name = '" + name + "'");
		ResultSet result = statement.getResultSet();
		
		while(result.next()) {
			int id = result.getInt(0);
			name = result.getString(1);
			long created = result.getLong(2);
			glossary = new Glossary(id, name, created);
		}
		this.closeConnection();
		return glossary;
	}
		
	public static GlossaryDAO getInstance() throws ClassNotFoundException, IOException {
		if(instance == null)
			instance = new GlossaryDAO();
		return instance;
	}
}
