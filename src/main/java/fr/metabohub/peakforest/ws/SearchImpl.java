package fr.metabohub.peakforest.ws;

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

import fr.metabohub.peakforest.model.AbstractDatasetObject;
import fr.metabohub.peakforest.model.compound.ReferenceChemicalCompound;
import fr.metabohub.peakforest.security.TokenManagementService;
import fr.metabohub.peakforest.services.SearchService;
import fr.metabohub.peakforest.utils.PeakForestPruneUtils;
import fr.metabohub.peakforest.utils.PeakForestUtils;
import fr.metabohub.peakforest.ws.utils.JsonDumperTools;

/**
 * 
 * @author Nils Paulhe
 * 
 */
@Path("/search")
public class SearchImpl extends SpectralDatabaseImpl {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/all/{query}")
	public String searchAll(@PathParam("query") String query, @QueryParam("max") Integer max,
			@QueryParam("callback") String callback, @QueryParam("token") String token) {
		// check token
		if (!TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		if (max == null)
			max = 100;
		String json = searchAll(query, max);
		// returning JSON or JSONP
		return JsonDumperTools.returnCallback(callback, json);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/compounds/{query}")
	public String searchCompounds(@PathParam("query") String query, @QueryParam("max") Integer max,
			@QueryParam("callback") String callback, @QueryParam("token") String token) {
		// check token
		if (!TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		if (max == null)
			max = 100;
		String json = searchCompounds(query, max);
		// returning JSON or JSONP
		return JsonDumperTools.returnCallback(callback, json);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/compounds/name/{query}")
	public String searchCompoundNames(@PathParam("query") String query, @QueryParam("max") Integer max,
			@QueryParam("callback") String callback, @QueryParam("token") String token) {
		// check token
		if (!TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		if (max == null)
			max = 100;
		String json = searchCompoundNames(query, max);
		// returning JSON or JSONP
		return JsonDumperTools.returnCallback(callback, json);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/compounds/monoisotopicmass/{mass}/{delta}")
	public String searchCompoundsMonoisoMass(@PathParam("mass") String mass, @PathParam("delta") String delta,
			@QueryParam("max") Integer max, @QueryParam("callback") String callback,
			@QueryParam("token") String token) {
		// check token
		if (!TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		if (max == null)
			max = 100;
		String json = searchCompoundsMonoisoMass(mass, delta, max);
		// returning JSON or JSONP
		return JsonDumperTools.returnCallback(callback, json);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/compounds/averagemass/{mass}/{delta}")
	public String searchCompoundsAverageMass(@PathParam("mass") String mass, @PathParam("delta") String delta,
			@QueryParam("max") Integer max, @QueryParam("callback") String callback,
			@QueryParam("token") String token) {
		// check token
		if (!TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		if (max == null)
			max = 100;
		String json = searchCompoundsAverageMass(mass, delta, max);
		// returning JSON or JSONP
		return JsonDumperTools.returnCallback(callback, json);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/compounds/formula/{formula}")
	public String searchCompoundsFormula(@PathParam("formula") String formula, @QueryParam("max") Integer max,
			@QueryParam("callback") String callback, @QueryParam("token") String token) {
		// check token
		if (!TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		if (max == null)
			max = 100;
		String json = searchCompoundsFormula(formula, max);
		// returning JSON or JSONP
		return JsonDumperTools.returnCallback(callback, json);
	}

	@SuppressWarnings("unchecked")
	private String searchAll(String query, Integer max) {
		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;
		try {
			// search
			Map<String, Object> results = SearchService.search(query, max);
			results.put("compounds",
					PeakForestPruneUtils.prune((List<AbstractDatasetObject>) results.get("compounds")));
			results.put("compoundNames",
					PeakForestPruneUtils.prune((List<AbstractDatasetObject>) results.get("compoundNames")));
			if (results.containsKey("nmrSpectra"))
				results.put("nmrSpectra",
						PeakForestPruneUtils.prune((List<AbstractDatasetObject>) results.get("nmrSpectra")));
			ret = mapper.writeValueAsString(results);
		} catch (final Exception e) {
			ret = JsonDumperTools.extractErrorAsJson(mapper, e);
		}

		return ret;
	}

	@SuppressWarnings("unchecked")
	private String searchCompounds(String query, Integer max) {
		// init
		Map<String, Object> searchResults = new HashMap<String, Object>();
		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;
		try {
			// search
			Map<String, Object> searchResultsRaw = SearchService.search(query, false, max);
			// prune
			searchResults.put("compounds",
					PeakForestPruneUtils.prune((List<AbstractDatasetObject>) searchResultsRaw.get("compounds")));
			searchResults.put("compoundNames",
					PeakForestPruneUtils.prune((List<AbstractDatasetObject>) searchResultsRaw.get("compoundNames")));
			// searchResults.put("compoundNames",
			// PeakForestPruneUtils.prune((List<AbstractDatasetObject>)
			// searchResults.get("compoundNames")));
			ret = mapper.writeValueAsString(searchResults);
		} catch (final Exception e) {
			ret = JsonDumperTools.extractErrorAsJson(mapper, e);
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	public String searchCompoundNames(String query, Integer max) {
		// init
		Map<String, Object> searchResults = new HashMap<String, Object>();
		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;
		try {
			// search
			Map<String, Object> rawSearchResults = SearchService.search(query, false, max);
			// prune
			// searchResults.put("compounds",
			// PeakForestPruneUtils.prune((List<AbstractDatasetObject>)
			// searchResults.get("compounds")));
			searchResults.put("compoundNames",
					PeakForestPruneUtils.prune((List<AbstractDatasetObject>) rawSearchResults.get("compoundNames")));
			searchResults.put("compounds",
					PeakForestPruneUtils.prune((List<AbstractDatasetObject>) rawSearchResults.get("compounds")));
			ret = mapper.writeValueAsString(searchResults);
		} catch (final Exception e) {
			ret = JsonDumperTools.extractErrorAsJson(mapper, e);
		}

		return ret;
	}

	private String searchCompoundsMonoisoMass(String mass, String delta, Integer max) {
		// init
		Map<String, Object> searchResults = new HashMap<String, Object>();
		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;
		try {
			// search
			searchResults = advancedCompoundSearch("monoisotopicmass", mass, delta);
			ret = mapper.writeValueAsString(searchResults);
		} catch (final Exception e) {
			ret = JsonDumperTools.extractErrorAsJson(mapper, e);
		}

		return ret;
	}

	private String searchCompoundsAverageMass(String mass, String delta, Integer max) {
		// init
		Map<String, Object> searchResults = new HashMap<String, Object>();
		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;
		try {
			// search
			searchResults = advancedCompoundSearch("averagemass", mass, delta);
			ret = mapper.writeValueAsString(searchResults);
		} catch (final Exception e) {
			ret = JsonDumperTools.extractErrorAsJson(mapper, e);
		}
		return ret;
	}

	private String searchCompoundsFormula(String formula, Integer max) {
		// init
		Map<String, Object> searchResults = new HashMap<String, Object>();
		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;
		try {
			// search
			searchResults = advancedCompoundSearch("formula", formula, null);
			ret = mapper.writeValueAsString(searchResults);
		} catch (final Exception e) {
			ret = JsonDumperTools.extractErrorAsJson(mapper, e);
		}
		return ret;
	}

	private Map<String, Object> advancedCompoundSearch(String filter, String massOrFormula, String delta)
			throws Exception {
		Map<String, Object> searchResults = new HashMap<String, Object>();
		// init
		final int max = 50;
		// search
		switch (filter.toLowerCase()) {
		case "monoisotopicmass":
			List<AbstractDatasetObject> primitiveData = new ArrayList<AbstractDatasetObject>();
			for (ReferenceChemicalCompound rcc : SearchService.searchCompound("",
					PeakForestUtils.SEARCH_COMPOUND_MONOISOTOPIC_MASS, massOrFormula, delta, null, max)) {
				primitiveData.add(rcc);
			}
			// prune
			searchResults.put("compounds", PeakForestPruneUtils.prune(primitiveData));
			break;
		case "averagemass":
			List<AbstractDatasetObject> primitiveData2 = new ArrayList<AbstractDatasetObject>();
			for (ReferenceChemicalCompound rcc : SearchService.searchCompound("",
					PeakForestUtils.SEARCH_COMPOUND_AVERAGE_MASS, massOrFormula, delta, null, max)) {
				primitiveData2.add(rcc);
			}
			// prune
			searchResults.put("compounds", PeakForestPruneUtils.prune(primitiveData2));
			break;
		case "formula":
			List<AbstractDatasetObject> primitiveData3 = new ArrayList<AbstractDatasetObject>();
			for (ReferenceChemicalCompound rcc : SearchService.searchCompound("",
					PeakForestUtils.SEARCH_COMPOUND_FORMULA, massOrFormula, delta, null, max)) {
				primitiveData3.add(rcc);
			}
			// prune
			searchResults.put("compounds", PeakForestPruneUtils.prune(primitiveData3));
			break;
		}
		return searchResults;
	}

}
