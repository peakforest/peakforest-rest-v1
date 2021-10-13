package fr.metaboHub.peakforest.ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.map.ObjectMapper;

import fr.metaboHub.peakforest.security.TokenManagementService;
import fr.metaboHub.peakforest.ws.struct.ObjectError;
import fr.metabohub.peakforest.mapper.LCcolumnMapper;
import fr.metabohub.peakforest.services.metadata.LiquidChromatographyMetadataManagementService;
import fr.metabohub.peakforest.utils.Utils;

/**
 * 
 * @author Nils Paulhe
 * 
 */
@Path("/metadata/lc")
public class LiquidChromatographyMetadataImpl extends SpectralDatabaseImpl {

	// /////////////////////

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/list-columns")
	public String getListLCcolumns(@QueryParam("callback") String callback,
			@QueryParam("token") String token) {
		// check token
		if (token == null || token.trim().length() == 0 || !TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		// based on POST method
		String json = getListLCcolumns();
		// returning JSON or JSONP
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	/**
	 * @param min
	 * @param max
	 * @return
	 */
	public String getListLCcolumns() {
		// init
		String dbName = Utils.getBundleConfElement("hibernate.connection.database.dbName");
		String login = Utils.getBundleConfElement("hibernate.connection.database.username");
		String password = Utils.getBundleConfElement("hibernate.connection.database.password");
		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;
		try {
			// // search
			List<LCcolumnMapper> results = LiquidChromatographyMetadataManagementService
					.readDistinctColumnByBasicProperties(dbName, login, password);
			// // prune
			// results = (List<LCcolumnMapper>) Utils.pruneMetadata(results);
			// // return
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

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/list-code-columns")
	public String getListLCcolumnsCode(@QueryParam("callback") String callback,
			@QueryParam("molids") String molids, @QueryParam("token") String token) {
		// check token
		if (token == null || token.trim().length() == 0 || !TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		List<String> cpdIDs = new ArrayList<String>();
		if (molids != null)
			cpdIDs = Arrays.asList(molids.split(","));
		// based on POST method
		String json = null;
		if (cpdIDs.isEmpty())
			json = getListLCcolumnsCode();
		else
			json = getListLCcolumnsCode(cpdIDs);
		// returning JSON or JSONP
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	public String getListLCcolumnsCode() {
		// init
		String dbName = Utils.getBundleConfElement("hibernate.connection.database.dbName");
		String login = Utils.getBundleConfElement("hibernate.connection.database.username");
		String password = Utils.getBundleConfElement("hibernate.connection.database.password");
		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;
		try {
			// // search
			Map<String, Object> results = LiquidChromatographyMetadataManagementService
					.readDistinctColumnByCode(dbName, login, password);
			// // prune
			// results = (List<LCcolumnMapper>) Utils.pruneMetadata(results);
			// // return
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

	public String getListLCcolumnsCode(List<String> molIDs) {
		// init
		String dbName = Utils.getBundleConfElement("hibernate.connection.database.dbName");
		String login = Utils.getBundleConfElement("hibernate.connection.database.username");
		String password = Utils.getBundleConfElement("hibernate.connection.database.password");
		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;

		List<Long> cpdIDasLong = new ArrayList<Long>();
		boolean useInChIKey = true;

		for (String s : molIDs) {
			try {
				cpdIDasLong.add(Long.parseLong(s));
				useInChIKey = false;
			} catch (NumberFormatException e) {
			}
		}

		try {
			// search
			Map<String, Object> results = new HashMap<String, Object>();
			if (useInChIKey)
				results = LiquidChromatographyMetadataManagementService
						.readDistinctColumnByCodeFilterMolInChIKeys(molIDs, dbName, login, password);
			else
				results = LiquidChromatographyMetadataManagementService
						.readDistinctColumnByCodeFilterMolIDs(cpdIDasLong, dbName, login, password);
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

}
