package fr.metabohub.peakforest.security;

import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;

import fr.metabohub.peakforest.utils.PeakForestApiHibernateUtils;
import fr.metabohub.peakforest.ws.utils.JsonDumperTools;

/**
 * Class used to check token and user credentials
 * 
 * @author Nils Paulhe
 *
 */
public class TokenManagementService {

	/**
	 * Check if the token is valid
	 * 
	 * @param token the user's token
	 * @return true if the token is valid and belong to a validated user, false
	 *         otherwise
	 */
	public static boolean isTokenValide(final String token) {
		if (token == null || token.trim().length() == 0) {
			return Boolean.FALSE;
		}
		// init results
		boolean success = Boolean.FALSE;
		try (final Session session = PeakForestApiHibernateUtils.getSessionFactory().openSession()) {
			// QUERY DB
			final String queryString = "SELECT u.id FROM users AS u WHERE u.token = :token AND u.confirmed = 1";
			final NativeQuery<?> query = session.createSQLQuery(queryString);
			query.setParameter("token", token);
			final List<?> allIDs = query.list();
			// SET RESULTS
			success = !allIDs.isEmpty();
		} catch (final HibernateException e) {
			e.printStackTrace();
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return success;
	}

	public static String tokenError(final String callback) {
		final String error = "{\"success\":false,\"error\":\"token_required\"}";
		return JsonDumperTools.returnCallback(callback, error);
	}

	public static Response tokenFileError() {
		return Response.status(Response.Status.UNAUTHORIZED).entity(null).type(MediaType.APPLICATION_JSON).build();
	}

}
