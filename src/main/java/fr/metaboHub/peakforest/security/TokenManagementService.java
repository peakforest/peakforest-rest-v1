package fr.metaboHub.peakforest.security;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import fr.metabohub.peakforest.security.dao.MetaDbSessionFactoryManager;

/**
 * @author Nils Paulhe
 *
 */
public class TokenManagementService {

	/**
	 * @param token
	 * @return
	 */
	public static boolean isTokenValide(String token) {
		if (token == null || token.trim().length() == 0)
			return false;
		// init results
		boolean success = false;
		// init session
		SessionFactory sessionFactory = null;
		Session session = null;
		try {
			sessionFactory = MetaDbSessionFactoryManager.getInstance().getMetaDbSessionFactory();
			session = sessionFactory.openSession();
			// QUERY DB
			String queryString = "SELECT u.id FROM users AS u WHERE u.token = :token AND u.confirmed = 1";
			SQLQuery query = session.createSQLQuery(queryString);
			query.setString("token", token);
			@SuppressWarnings("unchecked")
			List<Long> allIDs = query.list();
			// SET RESULTS
			success = !allIDs.isEmpty();
		} catch (HibernateException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}

		return success;
	}

	public static String tokenError(String callback) {
		String error = "{success:false,error:\"token_required\"}";
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + error + ")";
		return error;
	}

}
