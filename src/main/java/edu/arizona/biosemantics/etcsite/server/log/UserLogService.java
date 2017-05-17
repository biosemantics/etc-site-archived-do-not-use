package edu.arizona.biosemantics.etcsite.server.log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import edu.arizona.biosemantics.common.ling.know.IGlossary;
import edu.arizona.biosemantics.common.ling.transform.ITokenizer;
import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.semanticmarkup.db.ConnectionPool;

/**
 * for recording user operation logs
 * @author maojin
 *
 */
public class UserLogService {
	private ConnectionPool connectionPool;
	
	@Inject
	public UserLogService(ConnectionPool connectionPool) throws SQLException, ClassNotFoundException{
		this.connectionPool = connectionPool;
	}
	
	private void createUserLogTable() throws SQLException {
		try(Connection connection = connectionPool.getConnection()) {
			try(Statement statement = connection.createStatement()) {
		        String query = "create table if not exists user_log (word varchar(150) unique not null primary key, count int, dhword varchar(150), inbrackets int default 0)"
		        		            + " CHARACTER SET utf8 engine=innodb";
		        statement.execute(query);        
			}
		}
    }
	
	
	private void insertLog(Integer userid, String sessionid, String operation, String term){
		try(Connection connection = connectionPool.getConnection()) {
			try(PreparedStatement statement = connection.prepareStatement("insert into user_log (userid, sessionid, operation, term) value (?,?,?,?)")) {
				statement.setInt(1, userid);
				statement.setString(2, sessionid);
				statement.setString(3, operation);
				statement.setString(4, term);
				statement.executeUpdate();
			}
		} catch(SQLException e) {
			log(LogLevel.ERROR, "problem inserting taxon name to the taxonnames table", e);
		}
	}
	
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    }
}
