package edu.arizona.biosemantics.etcsite.server.db;

import java.sql.ResultSet;
import java.util.Date;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.shared.model.TaxonGroup;

public class TaxonGroupDAO {
	
	public TaxonGroup getTaxonGroup(int id) {
		TaxonGroup taxonGroup = null;
		try (Query query = new Query("SELECT * FROM etcsite_taxon_group WHERE id = ?")) {
			query.setParameter(1, id);
			ResultSet result = query.execute();
			while(result.next()) {
				id = result.getInt(1);
				String name = result.getString(2);
				Date created = result.getTimestamp(3);
				taxonGroup = new TaxonGroup(id, name, created);
			}
		}catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't get taxon_group", e);
		}
		return taxonGroup;
	}
	
	public TaxonGroup getTaxonGroup(String name) {		
		TaxonGroup taxonGroup = null;
		try (Query query = new Query("SELECT * FROM etcsite_taxon_group WHERE name = ?")) {
			query.setParameter(1, name);
			ResultSet result = query.execute();
			while(result.next()) {
				int id = result.getInt(1);
				name = result.getString(2);
				Date created = result.getTimestamp(3);
				taxonGroup = new TaxonGroup(id, name, created);
			}
		} catch (Exception e) {
			log(LogLevel.ERROR, "Couldn't get taxon_group of name " + name, e);
		}
		return taxonGroup;
	}
		
}
