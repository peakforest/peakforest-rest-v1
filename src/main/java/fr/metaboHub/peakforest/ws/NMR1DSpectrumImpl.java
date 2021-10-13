package fr.metaboHub.peakforest.ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.map.ObjectMapper;

import fr.metaboHub.peakforest.security.TokenManagementService;
import fr.metaboHub.peakforest.ws.struct.ObjectError;
import fr.metabohub.peakforest.mapper.NMR1DspectrumMapper;
import fr.metabohub.peakforest.mapper.NMRspectraCleaner;
import fr.metabohub.peakforest.model.AbstractDatasetObject;
import fr.metabohub.peakforest.model.spectrum.NMR1DSpectrum;
import fr.metabohub.peakforest.services.spectrum.NMR1DSpectrumManagementService;
import fr.metabohub.peakforest.utils.Utils;

/**
 * 
 * @author Nils Paulhe
 * 
 */
@Path("/spectra/nmr1d")
public class NMR1DSpectrumImpl extends SpectralDatabaseImpl {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}")
	public String getNMRspectra(@PathParam("id") Long id, @QueryParam("callback") String callback,
			@QueryParam("token") String token) {
		// check token
		if (token == null || token.trim().length() == 0 || !TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		//

		// based on POST method
		String dbName = Utils.getBundleConfElement("hibernate.connection.database.dbName");
		String login = Utils.getBundleConfElement("hibernate.connection.database.username");
		String password = Utils.getBundleConfElement("hibernate.connection.database.password");
		// run
		ObjectMapper mapper = new ObjectMapper();
		String json;
		try {
			// search
			NMR1DSpectrum result = NMR1DSpectrumManagementService.read(id, dbName, login, password);
			// prune
			result = (NMR1DSpectrum) Utils.prune(result);
			// return
			json = mapper.writeValueAsString(result);
		} catch (Exception e) {
			// Error
			ObjectError err = new ObjectError(e.getMessage());
			try {
				json = mapper.writeValueAsString(err);
			} catch (IOException e1) {
				json = "{ success: false, error: \"" + e1.getMessage() + "\"}";
			}
		}
		// returning JSON or JSONP
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	// /////////////////////

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/search-naive/{peaklist}/{delta}")
	public String getNMRspectra(@PathParam("peaklist") String peaklist, @PathParam("delta") double delta,
			@QueryParam("callback") String callback, @QueryParam("matchAll") Boolean matchAll,
			@QueryParam("token") String token) {
		// check token
		if (token == null || token.trim().length() == 0 || !TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		//
		String[] dataPeakList = peaklist.split(",");
		List<Double> peakListDouble = new ArrayList<Double>();
		for (String s : dataPeakList)
			try {
				peakListDouble.add(Double.parseDouble(s));
			} catch (NumberFormatException e) {
			}
		// based on POST method
		String json = getNMRspectraNaive(peakListDouble, delta, matchAll);
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
	public String getNMRspectraNaive(List<Double> peaklist, double delta, Boolean matchAll) {
		if (matchAll == null)
			matchAll = true;
		// init
		String dbName = Utils.getBundleConfElement("hibernate.connection.database.dbName");
		String login = Utils.getBundleConfElement("hibernate.connection.database.username");
		String password = Utils.getBundleConfElement("hibernate.connection.database.password");
		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;
		try {
			// // search
			List<NMR1DSpectrum> results = NMR1DSpectrumManagementService.search(
					peaklist.toArray(new Double[peaklist.size()]), delta, matchAll, dbName, login, password);
			// // prune
			results = (List<NMR1DSpectrum>) Utils.pruneNMR1Dspectra(results);
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
	@Path("/search-naive-clean/{peaklist}/{delta}")
	public String getNMRspectraClean(@PathParam("peaklist") String peaklist, @PathParam("delta") double delta,
			@QueryParam("callback") String callback, @QueryParam("matchAll") Boolean matchAll,
			@QueryParam("token") String token) {
		// check token
		if (token == null || token.trim().length() == 0 || !TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		String[] dataPeakList = peaklist.split(",");
		List<Double> peakListDouble = new ArrayList<Double>();
		for (String s : dataPeakList)
			try {
				peakListDouble.add(Double.parseDouble(s));
			} catch (NumberFormatException e) {
			}
		// based on POST method
		String json = getNMRspectraNaiveClean(peakListDouble, delta, matchAll);
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
	public String getNMRspectraNaiveClean(List<Double> peaklist, double delta, Boolean matchAll) {
		if (matchAll == null)
			matchAll = true;
		// init
		String dbName = Utils.getBundleConfElement("hibernate.connection.database.dbName");
		String login = Utils.getBundleConfElement("hibernate.connection.database.username");
		String password = Utils.getBundleConfElement("hibernate.connection.database.password");
		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;
		try {
			// // search
			List<NMR1DSpectrum> results = NMR1DSpectrumManagementService.search(
					peaklist.toArray(new Double[peaklist.size()]), delta, matchAll, dbName, login, password);
			// // prune
			List<NMR1DspectrumMapper> resultsClean = NMRspectraCleaner.clean1D(results);
			// // return
			ret = mapper.writeValueAsString(resultsClean);
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

	//
	//
	// @GET
	// @Produces(MediaType.APPLICATION_JSON)
	// // @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	// @Path("/getInChIRange/{start}/{end}")
	// public String getInChIRange(@PathParam("start") String start, @PathParam("end") String end,
	// @QueryParam("callback") String callback) {
	// // based on POST method
	// String json = getInChIRange(start, end);
	// // returning JSON or JSONP
	// if (callback != null && callback.trim().length() > 0)
	// return callback + "(" + json + ")";
	// return json;
	// }
	//
	// /**
	// * @param query
	// * @param max
	// * @return
	// */
	// @POST
	// @Produces(MediaType.APPLICATION_JSON)
	// @Path("/getInChIRange")
	// public String getInChIRange(@FormParam("start") String start, @FormParam("end") String end) {
	// //
	// int from = Integer.parseInt(start);
	// int to = Integer.parseInt(end);
	// // init
	// String dbName = Utils.getBundleConfElement("hibernate.connection.database.dbName");
	// String login = Utils.getBundleConfElement("hibernate.connection.database.username");
	// String password = Utils.getBundleConfElement("hibernate.connection.database.password");
	// // run
	// ObjectMapper mapper = new ObjectMapper();
	// String ret;
	// try {
	// ArrayList<String> listOfInChI = new ArrayList<String>();
	// // get all Compounds
	// List<StructureChemicalCompound> listOfStructCC = StructuralCompoundManagementService.readAll(
	// dbName, login, password);
	// // substring
	// if (to > listOfStructCC.size())
	// to = listOfStructCC.size();
	// listOfStructCC = listOfStructCC.subList(from, to);
	// // prune
	// for (StructureChemicalCompound scc : listOfStructCC)
	// listOfInChI.add(scc.getInChI());
	// // return
	// ret = mapper.writeValueAsString(listOfInChI);
	// } catch (Exception e) {
	// // Error
	// ObjectError err = new ObjectError(e.getMessage());
	// try {
	// ret = mapper.writeValueAsString(err);
	// } catch (IOException e1) {
	// ret = "{ success: false, error: \"" + e1.getMessage() + "\"}";
	// }
	// }
	// return ret;
	// }
	//
	// @GET
	// @Produces(MediaType.APPLICATION_JSON)
	// // @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	// @Path("/getInChIKeyRange/{start}/{end}")
	// public String getInChIKeyRange(@PathParam("start") String start, @PathParam("end") String end,
	// @QueryParam("callback") String callback) {
	// // based on POST method
	// String json = getInChIKeyRange(start, end);
	// // returning JSON or JSONP
	// if (callback != null && callback.trim().length() > 0)
	// return callback + "(" + json + ")";
	// return json;
	// }
	//
	// /**
	// * @param query
	// * @param max
	// * @return
	// */
	// @POST
	// @Produces(MediaType.APPLICATION_JSON)
	// @Path("/getInChIKeyRange")
	// public String getInChIKeyRange(@FormParam("start") String start, @FormParam("end") String end) {
	// //
	// int from = Integer.parseInt(start);
	// int to = Integer.parseInt(end);
	// // init
	// String dbName = Utils.getBundleConfElement("hibernate.connection.database.dbName");
	// String login = Utils.getBundleConfElement("hibernate.connection.database.username");
	// String password = Utils.getBundleConfElement("hibernate.connection.database.password");
	// // run
	// ObjectMapper mapper = new ObjectMapper();
	// String ret;
	// try {
	// ArrayList<String> listOfInChI = new ArrayList<String>();
	// // get all Compounds
	// List<StructureChemicalCompound> listOfStructCC = StructuralCompoundManagementService.readAll(
	// dbName, login, password);
	// // substring
	// if (to > listOfStructCC.size())
	// to = listOfStructCC.size();
	// listOfStructCC = listOfStructCC.subList(from, to);
	// // prune
	// for (StructureChemicalCompound scc : listOfStructCC)
	// listOfInChI.add(scc.getInChIKey());
	// // return
	// ret = mapper.writeValueAsString(listOfInChI);
	// } catch (Exception e) {
	// // Error
	// ObjectError err = new ObjectError(e.getMessage());
	// try {
	// ret = mapper.writeValueAsString(err);
	// } catch (IOException e1) {
	// ret = "{ success: false, error: \"" + e1.getMessage() + "\"}";
	// }
	// }
	// return ret;
	// }

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/search")
	public String naturalLanguageSearch(@QueryParam("query") String query, @QueryParam("max") Integer max,
			@QueryParam("callback") String callback, @QueryParam("token") String token) {
		// check token
		if (token == null || token.trim().length() == 0 || !TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		// based on POST method
		if (max == null)
			max = 50;
		String json = naturalLanguageSearch(query, max);
		// returning JSON or JSONP
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	@SuppressWarnings("unchecked")
	private String naturalLanguageSearch(String query, int max) {

		// init
		String dbName = Utils.getBundleConfElement("hibernate.connection.database.dbName");
		String login = Utils.getBundleConfElement("hibernate.connection.database.username");
		String password = Utils.getBundleConfElement("hibernate.connection.database.password");

		// run
		ObjectMapper mapper = new ObjectMapper();

		String ret;
		try {

			List<NMR1DSpectrum> resultsNMR = NMR1DSpectrumManagementService.searchKeywords(query, dbName,
					login, password);
			if (resultsNMR.size() > max)
				resultsNMR = resultsNMR.subList(0, max);

			Object data = (Object) resultsNMR;
			ret = mapper.writeValueAsString(Utils.prune((List<AbstractDatasetObject>) data));
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
