package fr.metabohub.peakforest.ws;

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

import fr.metabohub.peakforest.dao.metadata.LiquidChromatographyMetadataDao;
import fr.metabohub.peakforest.mapper.LCcolumnMapper;
import fr.metabohub.peakforest.security.TokenManagementService;
import fr.metabohub.peakforest.ws.utils.JsonDumperTools;

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
	public String getListLCcolumns(@QueryParam("callback") String callback, @QueryParam("token") String token) {
		// check token
		if (token == null || token.trim().length() == 0 || !TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		// based on POST method
		String json = getListLCcolumns();
		// returning JSON or JSONP
		return JsonDumperTools.returnCallback(callback, json);
	}

	public String getListLCcolumns() {
		// run
		final ObjectMapper mapper = new ObjectMapper();
		String ret;
		try {
			// search
			final List<LCcolumnMapper> results = LiquidChromatographyMetadataDao.readDistinctColumnByBasicProperties();
			// return
			ret = mapper.writeValueAsString(results);
		} catch (final Exception e) {
			ret = JsonDumperTools.extractErrorAsJson(mapper, e);
		}
		return ret;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/list-code-columns")
	public String getListLCcolumnsCode(@QueryParam("callback") String callback, @QueryParam("molids") String molids,
			@QueryParam("token") String token) {
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
		return JsonDumperTools.returnCallback(callback, json);
	}

	public String getListLCcolumnsCode() {
		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;
		try {
			// search
			Map<String, Object> results = LiquidChromatographyMetadataDao.getDistinctColumn();
			// return
			ret = mapper.writeValueAsString(results);
		} catch (final Exception e) {
			ret = JsonDumperTools.extractErrorAsJson(mapper, e);
		}
		return ret;
	}

	public String getListLCcolumnsCode(List<String> molIDs) {
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
				results = LiquidChromatographyMetadataDao.getDistinctColumnFilterMolInChIKeys(molIDs);
			else
				results = LiquidChromatographyMetadataDao.getDistinctColumnFilterMolIDs(cpdIDasLong);
			ret = mapper.writeValueAsString(results);
		} catch (final Exception e) {
			ret = JsonDumperTools.extractErrorAsJson(mapper, e);
		}
		return ret;
	}

}
