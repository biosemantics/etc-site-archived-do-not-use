package edu.arizona.biosemantics.etcsite.server.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import edu.arizona.biosemantics.etcsite.server.db.Query.QueryException;
import edu.arizona.biosemantics.etcsite.shared.model.DatasetPrefix;


public class DatasetPrefixDAO {

	public DatasetPrefix getDatasetPrefix(String prefix) {
		DatasetPrefix datasetPrefix = null;
		try(Query query = new Query("SELECT * FROM datasetprefixes WHERE prefix = ?")) {
			query.setParameter(1, prefix);
			ResultSet result = query.execute();
			while(result.next()) {
				prefix = result.getString(1);
				String glossaryVersion = result.getString(2);
				int otoUploadId = result.getInt(3);
				String otoSecret = result.getString(4);
				Date created = result.getTimestamp(5);
				datasetPrefix = new DatasetPrefix(prefix, glossaryVersion, otoUploadId, otoSecret, created);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return datasetPrefix;
	}

}
