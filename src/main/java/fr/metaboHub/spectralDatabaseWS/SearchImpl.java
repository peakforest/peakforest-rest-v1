package fr.metaboHub.spectralDatabaseWS;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.map.ObjectMapper;

import fr.metaboHub.spectralDatabaseWS.struct.ObjectError;
import fr.metabohub.spectralDatabaseAPI.services.SearchService;
import fr.metabohub.spectralDatabaseAPI.utils.Utils;

/**
 * 
 * @author Nils Paulhe
 * 
 */
@Path("/search")
public class SearchImpl extends SpectralDatabaseImpl {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/all")
	public String searchAll(@QueryParam("query") String query, @QueryParam("max") Integer max,
			@QueryParam("callback") String callback) {

		// based on POST method
		String json = searchAll(query, max);

		// returning JSON or JSONP
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/all")
	public String searchAll(@QueryParam("query") String query, @QueryParam("max") Integer max) {

		// init
		String dbName = Utils.getBundleConfElement("hibernate.connection.database.dbName");
		String login = Utils.getBundleConfElement("hibernate.connection.database.username");
		String password = Utils.getBundleConfElement("hibernate.connection.database.password");

		ObjectMapper mapper = new ObjectMapper();
		String ret;
		try {
			// search
			// TODO impl max
			Map<String, Object> results = SearchService.search(query, max, dbName, login, password);
			ret = mapper.writeValueAsString(results);
		} catch (Exception e) {
			// Error
			ObjectError err = new ObjectError(e.getMessage());
			try {
				ret = mapper.writeValueAsString(err);
			} catch (IOException e1) {
				ret = "{error: \"" + e1.getMessage() + "\"}";
			}
		}
		return ret;
	}

	// ////////////////////////////////////////////////////////////////////////////////////
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/compound")
	public String searchCompounds(@QueryParam("query") String query, @QueryParam("max") Integer max,
			@QueryParam("callback") String callback) {

		// based on POST method
		String json = searchCompounds(query, max);

		// returning JSON or JSONP
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/compound")
	public String searchCompounds(@QueryParam("query") String query, @QueryParam("max") Integer max) {

		// init
		String dbName = Utils.getBundleConfElement("hibernate.connection.database.dbName");
		String login = Utils.getBundleConfElement("hibernate.connection.database.username");
		String password = Utils.getBundleConfElement("hibernate.connection.database.password");

		ObjectMapper mapper = new ObjectMapper();
		String ret;
		try {
			// search
			List<Object> results = SearchService.search(query, "Compound", max, dbName, login, password);
			ret = mapper.writeValueAsString(results);
		} catch (Exception e) {
			// Error
			ObjectError err = new ObjectError(e.getMessage());
			try {
				ret = mapper.writeValueAsString(err);
			} catch (IOException e1) {
				ret = "{error: \"" + e1.getMessage() + "\"}";
			}
		}
		return ret;
	}
}
