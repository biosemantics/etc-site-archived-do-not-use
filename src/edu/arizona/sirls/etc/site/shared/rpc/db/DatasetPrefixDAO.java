package edu.arizona.sirls.etc.site.shared.rpc.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatasetPrefixDAO extends AbstractDAO {

	private static DatasetPrefixDAO instance;

	public DatasetPrefixDAO() throws ClassNotFoundException, SQLException, IOException {
		super();
	}

	public static DatasetPrefixDAO getInstance() throws ClassNotFoundException, SQLException, IOException {
		if(instance == null)
			instance = new DatasetPrefixDAO();
		return instance;
	}

	public DatasetPrefix getDatasetPrefix(String prefix) throws SQLException, ClassNotFoundException, IOException {
		DatasetPrefix datasetPrefix = null;
		Query query = new Query("SELECT * FROM datasetprefixes WHERE prefix = '" + prefix + "'");
		query.execute();
		ResultSet result = query.getResultSet();
		while(result.next()) {
			prefix = result.getString(1);
			String glossaryVersion = result.getString(2);
			int otoId = result.getInt(3);
			long created = result.getLong(4);
			datasetPrefix = new DatasetPrefix(prefix, glossaryVersion, otoId, created);
		}
		query.close();
		return datasetPrefix;
	}

}
