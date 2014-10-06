package edu.arizona.biosemantics.etcsite.server.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import edu.arizona.biosemantics.etcsite.shared.model.Glossary;

public class GlossaryDAO {
	
	public Glossary getGlossary(int id) {
		Glossary glossary = null;
		try (Query query = new Query("SELECT * FROM etcsite_glossaries WHERE id = ?")) {
			query.setParameter(1, id);
			ResultSet result = query.execute();
			while(result.next()) {
				id = result.getInt(1);
				String name = result.getString(2);
				Date created = result.getTimestamp(3);
				glossary = new Glossary(id, name, created);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return glossary;
	}
	
	public Glossary getGlossary(String name) {		
		Glossary glossary = null;
		try (Query query = new Query("SELECT * FROM etcsite_glossaries WHERE name = ?")) {
			query.setParameter(1, name);
			ResultSet result = query.execute();
			while(result.next()) {
				int id = result.getInt(1);
				name = result.getString(2);
				Date created = result.getTimestamp(3);
				glossary = new Glossary(id, name, created);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return glossary;
	}
		
}
