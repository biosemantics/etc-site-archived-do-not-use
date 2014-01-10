package edu.arizona.biosemantics.etcsite.shared.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class DatasetPrefixDAO {

	private static DatasetPrefixDAO instance;

	public static DatasetPrefixDAO getInstance() {
		if(instance == null)
			instance = new DatasetPrefixDAO();
		return instance;
	}

	public DatasetPrefix getDatasetPrefix(String prefix) throws SQLException, ClassNotFoundException, IOException {
		DatasetPrefix datasetPrefix = null;
		Query query = new Query("SELECT * FROM datasetprefixes WHERE prefix = ?");
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
		query.close();
		return datasetPrefix;
	}

}
