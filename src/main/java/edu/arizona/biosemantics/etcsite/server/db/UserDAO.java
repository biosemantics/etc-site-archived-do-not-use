package edu.arizona.biosemantics.etcsite.server.db;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.server.db.Query.QueryException;
import edu.arizona.biosemantics.etcsite.shared.model.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.model.TinyUser;
import edu.arizona.biosemantics.etcsite.shared.model.User;
import edu.arizona.biosemantics.etcsite.shared.model.User.EmailPreference;

public class UserDAO {
	
	private File profilesDir;

	public UserDAO() {
		profilesDir = new File(Configuration.profiles);
		profilesDir.mkdir();
	}

	public ShortUser getShortUser(int userId) {
		User user = this.getUser(userId);
		return createShortUser(user);
	}

	private ShortUser createShortUser(User user) {
		return new ShortUser(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), 
				user.getAffiliation(), user.getOpenIdProvider(), user.getOpenIdProviderId(), 
				user.getBioportalUserId(), user.getBioportalAPIKey(), user.getOtoAccountEmail(),
			user.getProfile());	
	}

	public User getUser(int id) {
		User user = null;
		try (Query query = new Query("SELECT * FROM etcsite_users WHERE id = ?")) {
			query.setParameter(1, id);
			ResultSet result = query.execute();
			while (result.next()) {
				user = createUser(result);
			}
		} catch (Exception e) {
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
		String otoAccountEmail = result.getString(11);
		String otoAuthenticationToken = result.getString(12);
		Date created = result.getTimestamp(13);

		Map<String , Boolean> profile = getSerializedProfile(id);

		return new User(id, openIdProviderId, openIdProvider, password,
					firstName, lastName, email, affiliation, bioportalUserId,
					bioportalAPIKey, otoAccountEmail, otoAuthenticationToken, 
					profile, created);
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
			"`email`, `affiliation`, `bioportaluserid`, `bioportalapikey`, `otoaccountemail`, `otoauthenticationtoken`) VALUES" +
			" (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");) {
			query.setParameter(1, user.getOpenIdProviderId());
			query.setParameter(2, user.getOpenIdProvider());
			query.setParameter(3, user.getPassword());
			query.setParameter(4, user.getFirstName());
			query.setParameter(5, user.getLastName());
			query.setParameter(6, user.getEmail());
			query.setParameter(7, user.getAffiliation());
			query.setParameter(8, user.getBioportalUserId());
			query.setParameter(9, user.getBioportalAPIKey());
			query.setParameter(10, user.getOtoAccountEmail());
			query.setParameter(11, user.getOtoAuthenticationToken());
			query.execute();
			ResultSet generatedKeys = query.getGeneratedKeys();
			if(generatedKeys.next()) {
				result = this.getUser(generatedKeys.getInt(1));
			}
			storeSerializedProfile(result.getId(), result.getProfile());
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
				+ "firstname = ?, lastname = ?, email = ?, affiliation = ?, bioportaluserid = ?, bioportalapikey = ?, otoaccountemail = ?, otoauthenticationtoken = ? WHERE id = ?")) {
			query.setParameter(1, user.getOpenIdProviderId());
			query.setParameter(2, user.getOpenIdProvider());
			query.setParameter(3, user.getPassword());
			query.setParameter(4, user.getFirstName());
			query.setParameter(5, user.getLastName());
			query.setParameter(6, user.getEmail());
			query.setParameter(7, user.getAffiliation());
			query.setParameter(8, user.getBioportalUserId());
			query.setParameter(9, user.getBioportalAPIKey());
			query.setParameter(10, user.getOtoAccountEmail());
			query.setParameter(11, user.getOtoAuthenticationToken());
			query.setParameter(12, user.getId());
			query.execute();
			storeSerializedProfile(user.getId(), user.getProfile());
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't update matrix generation configuration", e);
		}
	}

	private void storeSerializedProfile(int userId, Map<String, Boolean> profile) {
		try (ObjectOutput output = new ObjectOutputStream(
				new BufferedOutputStream(new FileOutputStream(new File(profilesDir, userId + ".ser"))))) {
			output.writeObject(profile);
		} catch (Exception e) {
			log(LogLevel.ERROR, "Serialization of user failed", e);
		}
	}
	
	private Map<String, Boolean> getSerializedProfile(int userId) {
		try(ObjectInput input = new ObjectInputStream(new BufferedInputStream(new FileInputStream(new File(profilesDir, userId + ".ser"))))) {
			return (Map<String, Boolean>) input.readObject();
		} catch (ClassNotFoundException | IOException e) {
			log(LogLevel.ERROR, "Deserialization of user failed. Will instantiate default profile.", e);
			Map<String, Boolean> defaultProfile = new HashMap<String, Boolean>();
			defaultProfile.put(EmailPreference.TEXT_CAPTURE.getKey(), true);
			defaultProfile.put(EmailPreference.MATRIX_GENERATION.getKey(), true);
			defaultProfile.put(EmailPreference.TREE_GENERATION.getKey(), true);
			defaultProfile.put(EmailPreference.TAXONOMY_COMPARISON.getKey(), true);
			defaultProfile.put(EmailPreference.VISUALIZATION.getKey(), true);
			defaultProfile.put(EmailPreference.PIPELINE.getKey(), true);
			this.storeSerializedProfile(userId, defaultProfile);
			return defaultProfile;
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
		} catch(Exception e) {
			log(LogLevel.ERROR, "Couldn't get users without given", e);
		}
		return result;
	}

	public boolean hasUser(String email) {
		return this.getUser(email) != null;
	}

	public List<edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection> getOntologizeCollections(int userId) {
		List<edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection> collections = 
				new LinkedList<edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection>();
		try (Query query = new Query("SELECT * FROM etcsite_user_ontologize_collection WHERE user = ?")) {
			query.setParameter(1, userId);
			ResultSet result = query.execute();
			while (result.next()) {
				int collectionId = result.getInt("ontologize_collection_id");
				String collectionSecret = result.getString("ontologize_collection_secret");
				edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection collection = 
						new edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection();
				collection.setId(collectionId);
				collection.setSecret(collectionSecret);
				collections.add(collection);
			}
		} catch (Exception e) {
			log(LogLevel.ERROR, "Couldn't get user", e);
		}	
		return collections;
	}

	public void insertOntologizeCollection(int userId, int collectionId, String collectionSecret) throws QueryException {
		try(Query query = new Query(
				"INSERT INTO `etcsite_user_ontologize_collection`(`user`, `ontologize_collection_id`, `ontologize_collection_secret`) VALUES" +
				" (?, ?, ?)");) {
				query.setParameter(1, userId);
				query.setParameter(2, collectionId);
				query.setParameter(3, collectionSecret);
				query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Couldn't insert user ontologize collection", e);
			throw e;
		}
	}

	public TinyUser getTinyUser(int userId) {
		User user = this.getUser(userId);
		return createTinyUser(user);
	}

	private TinyUser createTinyUser(User user) {
		return new TinyUser(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), 
				user.getAffiliation());
	}

}

