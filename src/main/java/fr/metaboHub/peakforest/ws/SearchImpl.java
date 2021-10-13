package fr.metaboHub.peakforest.ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.map.ObjectMapper;

import fr.metaboHub.peakforest.security.TokenManagementService;
import fr.metaboHub.peakforest.ws.struct.ObjectError;
import fr.metabohub.peakforest.model.AbstractDatasetObject;
import fr.metabohub.peakforest.model.compound.ReferenceChemicalCompound;
import fr.metabohub.peakforest.services.SearchService;
import fr.metabohub.peakforest.utils.Utils;

/**
 * 
 * @author Nils Paulhe
 * 
 */
@Path("/search")
public class SearchImpl extends SpectralDatabaseImpl {

	/**
	 * @param query
	 * @param max
	 * @param callback
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/all/{query}")
	public String searchAll(@PathParam("query") String query, @QueryParam("max") Integer max,
			@QueryParam("callback") String callback, @QueryParam("token") String token) {
		// check token
		if (token == null || token.trim().length() == 0 || !TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		if (max == null)
			max = 100;
		String json = searchAll(query, max);
		// returning JSON or JSONP
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	/**
	 * @param query
	 * @param max
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String searchAll(String query, Integer max) {
		// init
		String dbName = Utils.getBundleConfElement("hibernate.connection.database.dbName");
		String login = Utils.getBundleConfElement("hibernate.connection.database.username");
		String password = Utils.getBundleConfElement("hibernate.connection.database.password");
		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;
		try {
			// search
			Map<String, Object> results = SearchService.search(query, max, dbName, login, password);
			results.put("compounds", Utils.prune((List<AbstractDatasetObject>) results.get("compounds")));
			results.put("compoundNames",
					Utils.prune((List<AbstractDatasetObject>) results.get("compoundNames")));
			ret = mapper.writeValueAsString(results);
		} catch (Exception e) {
			// Error
			ObjectError err = new ObjectError(e.getMessage());
			try {
				ret = mapper.writeValueAsString(err);
			} catch (IOException e1) {
				ret = "{ success: false, error: \"" + e1.getMessage() + "\"}";
			}
		}
		return ret;
	}

	// ////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @param query
	 * @param max
	 * @param callback
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/compounds/{query}")
	public String searchCompounds(@PathParam("query") String query, @QueryParam("max") Integer max,
			@QueryParam("callback") String callback, @QueryParam("token") String token) {
		// check token
		if (token == null || token.trim().length() == 0 || !TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		if (max == null)
			max = 100;
		String json = searchCompounds(query, max);
		// returning JSON or JSONP
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	/**
	 * @param query
	 * @param max
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String searchCompounds(String query, Integer max) {
		// init
		Map<String, Object> searchResults = new HashMap<String, Object>();
		String dbName = Utils.getBundleConfElement("hibernate.connection.database.dbName");
		String login = Utils.getBundleConfElement("hibernate.connection.database.username");
		String password = Utils.getBundleConfElement("hibernate.connection.database.password");
		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;
		try {
			// search
			searchResults = SearchService.search(query, false, max, dbName, login, password);
			// prune
			searchResults.put("compounds",
					Utils.prune((List<AbstractDatasetObject>) searchResults.get("compounds")));
			// searchResults.put("compoundNames",
			// Utils.prune((List<AbstractDatasetObject>) searchResults.get("compoundNames")));
			ret = mapper.writeValueAsString(searchResults);
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

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/compounds/name/{query}")
	public String searchCompoundNames(@PathParam("query") String query, @QueryParam("max") Integer max,
			@QueryParam("callback") String callback, @QueryParam("token") String token) {
		// check token
		if (token == null || token.trim().length() == 0 || !TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		if (max == null)
			max = 100;
		String json = searchCompoundNames(query, max);
		// returning JSON or JSONP
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	/**
	 * @param query
	 * @param max
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String searchCompoundNames(String query, Integer max) {
		// init
		Map<String, Object> searchResults = new HashMap<String, Object>();
		String dbName = Utils.getBundleConfElement("hibernate.connection.database.dbName");
		String login = Utils.getBundleConfElement("hibernate.connection.database.username");
		String password = Utils.getBundleConfElement("hibernate.connection.database.password");
		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;
		try {
			// search
			searchResults = SearchService.search(query, false, max, dbName, login, password);
			// prune
			// searchResults.put("compounds",
			// Utils.prune((List<AbstractDatasetObject>) searchResults.get("compounds")));
			searchResults.put("compoundNames",
					Utils.prune((List<AbstractDatasetObject>) searchResults.get("compoundNames")));
			ret = mapper.writeValueAsString(searchResults);
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
	@Path("/compounds/monoisotopicmass/{mass}/{delta}")
	public String searchCompoundsMonoisoMass(@PathParam("mass") String mass, @PathParam("delta") String delta,
			@QueryParam("max") Integer max, @QueryParam("callback") String callback,
			@QueryParam("token") String token) {
		// check token
		if (token == null || token.trim().length() == 0 || !TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		if (max == null)
			max = 100;
		String json = searchCompoundsMonoisoMass(mass, delta, max);
		// returning JSON or JSONP
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	/**
	 * @param mass
	 * @param delta
	 * @param max
	 * @return
	 */
	public String searchCompoundsMonoisoMass(String mass, String delta, Integer max) {
		// init
		Map<String, Object> searchResults = new HashMap<String, Object>();

		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;
		try {
			// search
			searchResults = advancedCompoundSearch("monoisotopicmass", mass, delta);
			ret = mapper.writeValueAsString(searchResults);
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

	/**
	 * @param mass
	 * @param delta
	 * @param max
	 * @param callback
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/compounds/averagemass/{mass}/{delta}")
	public String searchCompoundsAverageMass(@PathParam("mass") String mass, @PathParam("delta") String delta,
			@QueryParam("max") Integer max, @QueryParam("callback") String callback,
			@QueryParam("token") String token) {
		// check token
		if (token == null || token.trim().length() == 0 || !TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		if (max == null)
			max = 100;
		String json = searchCompoundsAverageMass(mass, delta, max);
		// returning JSON or JSONP
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	/**
	 * @param mass
	 * @param delta
	 * @param max
	 * @return
	 */
	public String searchCompoundsAverageMass(String mass, String delta, Integer max) {
		// init
		Map<String, Object> searchResults = new HashMap<String, Object>();

		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;
		try {
			// search
			searchResults = advancedCompoundSearch("averagemass", mass, delta);
			ret = mapper.writeValueAsString(searchResults);
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

	// //
	/**
	 * @param formula
	 * @param max
	 * @param callback
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/compounds/formula/{formula}")
	public String searchCompoundsFormula(@PathParam("formula") String formula, @QueryParam("max") Integer max,
			@QueryParam("callback") String callback, @QueryParam("token") String token) {
		// check token
		if (token == null || token.trim().length() == 0 || !TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		if (max == null)
			max = 100;
		String json = searchCompoundsFormula(formula, max);
		// returning JSON or JSONP
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	public String searchCompoundsFormula(String formula, Integer max) {
		// init
		Map<String, Object> searchResults = new HashMap<String, Object>();

		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;
		try {
			// search
			searchResults = advancedCompoundSearch("formula", formula, null);
			ret = mapper.writeValueAsString(searchResults);
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

	// /////// private methods
	/**
	 * @param filter
	 * @param massOrFormula
	 * @param delta
	 * @param dbName
	 * @param login
	 * @param password
	 * @throws Exception
	 */
	private Map<String, Object> advancedCompoundSearch(String filter, String massOrFormula, String delta)
			throws Exception {
		Map<String, Object> searchResults = new HashMap<String, Object>();
		// init
		String dbName = Utils.getBundleConfElement("hibernate.connection.database.dbName");
		String login = Utils.getBundleConfElement("hibernate.connection.database.username");
		String password = Utils.getBundleConfElement("hibernate.connection.database.password");

		switch (filter.toLowerCase()) {
		case "monoisotopicmass":
			List<AbstractDatasetObject> primitiveData = new ArrayList<AbstractDatasetObject>();
			for (ReferenceChemicalCompound rcc : SearchService.searchCompound("",
					Utils.SEARCH_COMPOUND_MONOISOTOPIC_MASS, massOrFormula, delta, null, 50, dbName, login,
					password)) {
				primitiveData.add(rcc);
			}
			// prune
			searchResults.put("compounds", Utils.prune(primitiveData));
			break;
		case "averagemass":
			List<AbstractDatasetObject> primitiveData2 = new ArrayList<AbstractDatasetObject>();
			for (ReferenceChemicalCompound rcc : SearchService.searchCompound("",
					Utils.SEARCH_COMPOUND_AVERAGE_MASS, massOrFormula, delta, null, 50, dbName, login,
					password)) {
				primitiveData2.add(rcc);
			}
			// prune
			searchResults.put("compounds", Utils.prune(primitiveData2));
			break;
		case "formula":
			List<AbstractDatasetObject> primitiveData3 = new ArrayList<AbstractDatasetObject>();
			for (ReferenceChemicalCompound rcc : SearchService.searchCompound("",
					Utils.SEARCH_COMPOUND_FORMULA, massOrFormula, delta, null, 50, dbName, login, password)) {
				primitiveData3.add(rcc);
			}
			// prune
			searchResults.put("compounds", Utils.prune(primitiveData3));
			break;
		}
		return searchResults;
	}

}
