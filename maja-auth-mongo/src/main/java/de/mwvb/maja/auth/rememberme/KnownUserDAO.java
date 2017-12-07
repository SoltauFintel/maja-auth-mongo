package de.mwvb.maja.auth.rememberme;

import de.mwvb.maja.mongo.AbstractDAO;
import de.mwvb.maja.mongo.Database;

public class KnownUserDAO extends AbstractDAO<KnownUser> {

	public KnownUserDAO(Database database) {
		super(database, KnownUser.class);
	}

	/**
	 * @param id entity id
	 * @return KnownUser or null if not found
	 */
	public KnownUser get(String id) {
		return ds.createQuery(cls).field("id").equal(id).get();
	}
	
	/**
	 * @param userId contains service name and foreign user id
	 */
	public void delete(String userId) {
		ds.delete(ds.createQuery(cls).field("userId").equal(userId));
	}
}
