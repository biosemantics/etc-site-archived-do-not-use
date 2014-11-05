package edu.arizona.biosemantics.etcsite.server.db;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.persistence.exceptions.QueryException;

import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.shared.model.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.model.User;

public class UserDAO {

	public ShortUser getShortUser(int userId) {
		User user = this.getUser(userId);
		return createShortUser(user);
	}

	private ShortUser createShortUser(User user) {
		return new ShortUser(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), 
				user.getAffiliation(), user.getOpenIdProvider(), user.getOpenIdProviderId(), 
				user.getBioportalUserId(), user.getBioportalAPIKey());
	}

	public User getUser(int id) {
		User user = null;
		try(Query query = new Query("SELECT * FROM etcsite_users WHERE id = ?")) {
			query.setParameter(1, id);
			ResultSet result = query.execute();
			while (result.next()) {
				user = createUser(result);
			}
		}catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't get user", e);
		}
		return user;
	}

	private User createUser(ResultSet result) throws SQLException {
		int id = result.getInt(1);
		String openIdProviderId = result.getString(2);
		String openIdProvider = result.getString(3);
		String password = result.getString(4);
		String firstName = result.getString(5);
		String lastName = result.getString(6);
		String email = result.getString(7);
		String affiliation = result.getString(8);
		String bioportalUserId = result.getString(9);
		String bioportalAPIKey = result.getString(10);
		Date created = result.getTimestamp(11);

		return new User(id, openIdProviderId, openIdProvider, password,
				firstName, lastName, email, affiliation, bioportalUserId,
				bioportalAPIKey, created);
	}

	public User getUser(String email) {
		User user = null;
		try(Query query = new Query("SELECT * FROM etcsite_users WHERE email = ?")) {
			query.setParameter(1, email);
			ResultSet result = query.execute();
			while (result.next()) {
				user = createUser(result);
			}
		}catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't get user", e);
		}
		return user;
	}
	
	public ShortUser getShortUser(String email) {
		User user = getUser(email);
		if(user != null) {
			ShortUser shortUser = new ShortUser(user);
			return shortUser;
		}
		return null;
	}
	
	public User insert(User user) {
		User result = null;
		try(Query query = new Query(
			"INSERT INTO `etcsite_users`(`openIdProviderId`, `openidprovider`, `password`, `firstname`, `lastname`, " +
			"`email`, `affiliation`, `bioportaluserid`, `bioportalapikey`) VALUES" +
			" (?, ?, ?, ?, ?, ?, ?, ?, ?)");) {
			query.setParameter(1, user.getOpenIdProviderId());
			query.setParameter(2, user.getOpenIdProvider());
			query.setParameter(3, user.getPassword());
			query.setParameter(4, user.getFirstName());
			query.setParameter(5, user.getLastName());
			query.setParameter(6, user.getEmail());
			query.setParameter(7, user.getAffiliation());
			query.setParameter(8, user.getBioportalUserId());
			query.setParameter(9, user.getBioportalAPIKey());
			query.execute();
			ResultSet generatedKeys = query.getGeneratedKeys();
			if(generatedKeys.next()) {
				result = this.getUser(generatedKeys.getInt(1));
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't add user", e);
		}
		
		if(result != null) {
			String filename = Configuration.fileBase + File.separator + result.getId();
			new File(filename).mkdirs();
		}
			
		return result;
	}
	
	public void update(User user) {
		try (Query query = new Query("UPDATE etcsite_users SET openidproviderid = ?, openidprovider = ?, password = ?, "
				+ "firstname = ?, lastname = ?, email = ?, affiliation = ?, bioportaluserid = ?, bioportalapikey = ? WHERE id = ?")) {
			query.setParameter(1, user.getOpenIdProviderId());
			query.setParameter(2, user.getOpenIdProvider());
			query.setParameter(3, user.getPassword());
			query.setParameter(4, user.getFirstName());
			query.setParameter(5, user.getLastName());
			query.setParameter(6, user.getEmail());
			query.setParameter(7, user.getAffiliation());
			query.setParameter(8, user.getBioportalUserId());
			query.setParameter(9, user.getBioportalAPIKey());
			query.setParameter(10, user.getId());
			query.execute();
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't update matrix generation configuration", e);
		}
	}
	
	public List<ShortUser> getUsers() {
		List<ShortUser> result = new LinkedList<ShortUser>();
		try(Query query = new Query("SELECT * FROM etcsite_users")) {
			ResultSet resultSet = query.execute();
			while (resultSet.next()) {
				result.add(createShortUser(createUser(resultSet)));
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't get users", e);
		}
		return result;
	}

	public List<ShortUser> getUsersWithout(int userId) {
		List<ShortUser> result = new LinkedList<ShortUser>();
		try(Query query = new Query("SELECT * FROM etcsite_users WHERE id != ?")) {
			query.setParameter(1, userId);
			ResultSet resultSet = query.execute();
			while (resultSet.next()) {
				result.add(createShortUser(createUser(resultSet)));
			}
		}catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't get users without given", e);
		}
		return result;
	}

	public boolean hasUser(String email) {
		return this.getUser(email) != null;
	}

}
