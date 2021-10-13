package fr.metabohub.peakforest.ws;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.map.ObjectMapper;

import fr.metabohub.peakforest.mapper.NMR2DspectrumMapper;
import fr.metabohub.peakforest.mapper.NMRspectraCleaner;
import fr.metabohub.peakforest.model.spectrum.NMR2DSpectrum;
import fr.metabohub.peakforest.security.TokenManagementService;
import fr.metabohub.peakforest.services.spectrum.NMR2DSpectrumManagementService;
import fr.metabohub.peakforest.utils.PeakForestPruneUtils;
import fr.metabohub.peakforest.ws.utils.JsonDumperTools;

/**
 * 
 * @author Nils Paulhe
 * 
 */
@Path("/spectra/nmr2d")
public class NMR2DSpectrumImpl extends SpectralDatabaseImpl {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}")
	public String getNMRspectra(@PathParam("id") Long id, @QueryParam("callback") String callback,
			@QueryParam("token") String token) {
		// check token
		if (!TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		// based on POST method
		ObjectMapper mapper = new ObjectMapper();
		String json;
		try {
			// search
			NMR2DSpectrum result = NMR2DSpectrumManagementService.read(id);
			// prune
			result = (NMR2DSpectrum) PeakForestPruneUtils.prune(result);
			// return
			json = mapper.writeValueAsString(result);
		} catch (final Exception e) {
			json = JsonDumperTools.extractErrorAsJson(mapper, e);
		}
		// returning JSON or JSONP
		return JsonDumperTools.returnCallback(callback, json);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/search-naive/{peaklistF1}/{deltaF1}/{peaklistF2}/{deltaF2}")
	public String getNMRspectra(@PathParam("peaklistF1") String peaklistF1, @PathParam("deltaF1") double deltaF1,
			@PathParam("peaklistF2") String peaklistF2, @PathParam("deltaF2") double deltaF2,
			@QueryParam("callback") String callback, @QueryParam("isJRES") Boolean isJRES,
			@QueryParam("matchAll") Boolean matchAll, @QueryParam("token") String token) {
		// check token
		if (!TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		//
		String[] dataPeakListF1 = peaklistF1.split(",");
		List<Double> peakListDoubleF1 = new ArrayList<Double>();
		for (String s : dataPeakListF1)
			try {
				peakListDoubleF1.add(Double.parseDouble(s));
			} catch (NumberFormatException e) {
			}
		String[] dataPeakListF2 = peaklistF2.split(",");
		List<Double> peakListDoubleF2 = new ArrayList<Double>();
		for (String s : dataPeakListF2)
			try {
				peakListDoubleF2.add(Double.parseDouble(s));
			} catch (NumberFormatException e) {
			}
		// based on POST method
		String json = getNMRspectraNaive(peakListDoubleF1, deltaF1, peakListDoubleF2, deltaF2, matchAll); // ,isJRES
		// returning JSON or JSONP
		return JsonDumperTools.returnCallback(callback, json);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/search-naive-clean/{peaklistF1}/{deltaF1}/{peaklistF2}/{deltaF2}")
	public String getNMRspectraClean(@PathParam("peaklistF1") String peaklistF1, @PathParam("deltaF1") double deltaF1,
			@PathParam("peaklistF2") String peaklistF2, @PathParam("deltaF2") double deltaF2,
			@QueryParam("callback") String callback, @QueryParam("matchAll") Boolean matchAll,
			@QueryParam("token") String token) {
		// check token
		if (!TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		String[] dataPeakListF1 = peaklistF1.split(",");
		List<Double> peakListDoubleF1 = new ArrayList<Double>();
		for (String s : dataPeakListF1)
			try {
				peakListDoubleF1.add(Double.parseDouble(s));
			} catch (NumberFormatException e) {
			}
		String[] dataPeakListF2 = peaklistF2.split(",");
		List<Double> peakListDoubleF2 = new ArrayList<Double>();
		for (String s : dataPeakListF2)
			try {
				peakListDoubleF2.add(Double.parseDouble(s));
			} catch (NumberFormatException e) {
			}

		// based on POST method
		String json = getNMRspectraNaiveClean(peakListDoubleF1, deltaF1, peakListDoubleF2, deltaF2, matchAll);
		// returning JSON or JSONP
		return JsonDumperTools.returnCallback(callback, json);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/search")
	public String naturalLanguageSearch(@QueryParam("query") String query, @QueryParam("max") Integer max,
			@QueryParam("callback") String callback, @QueryParam("token") String token) {
		// check token
		if (!TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		// based on POST method
		if (max == null)
			max = 50;
		String json = naturalLanguageSearch(query, max);
		// returning JSON or JSONP
		return JsonDumperTools.returnCallback(callback, json);
	}

	private String getNMRspectraNaive(List<Double> peaklistF1, double deltaF1, List<Double> peaklistF2, double deltaF2,
			Boolean matchAll) { // , Boolean isJRES
		if (matchAll == null)
			matchAll = true;
		// if (isJRES == null)
		// isJRES = false;
		// init
		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;
		try {
			// search
			List<NMR2DSpectrum> results = NMR2DSpectrumManagementService.search(
					peaklistF1.toArray(new Double[peaklistF1.size()]), deltaF1,
					peaklistF2.toArray(new Double[peaklistF2.size()]), deltaF2, matchAll);
			// peaklist.toArray(new Double[peaklist.size()]), delta, matchAll, dbName,
			// login, password);
			// prune
			results = (List<NMR2DSpectrum>) PeakForestPruneUtils.pruneNMR2Dspectra(results);
			// // return
			ret = mapper.writeValueAsString(results);
		} catch (final Exception e) {
			ret = JsonDumperTools.extractErrorAsJson(mapper, e);
		}
		return ret;
	}

	private String getNMRspectraNaiveClean(List<Double> peaklistF1, double deltaF1, List<Double> peaklistF2,
			double deltaF2, Boolean matchAll) {
		if (matchAll == null)
			matchAll = true;
		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;
		try {
			// // search
			List<NMR2DSpectrum> results = NMR2DSpectrumManagementService.search(
					peaklistF1.toArray(new Double[peaklistF1.size()]), deltaF1,
					peaklistF2.toArray(new Double[peaklistF2.size()]), deltaF2, matchAll);
			// // prune
			List<NMR2DspectrumMapper> resultsClean = NMRspectraCleaner.clean2D(results);
			// // return
			ret = mapper.writeValueAsString(resultsClean);
		} catch (final Exception e) {
			ret = JsonDumperTools.extractErrorAsJson(mapper, e);
		}

		return ret;
	}

	private String naturalLanguageSearch(String query, int max) {
		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;
		try {

			List<NMR2DSpectrum> resultsNMR = NMR2DSpectrumManagementService.searchKeywords(query);
			if (resultsNMR.size() > max)
				resultsNMR = resultsNMR.subList(0, max);
//			Object data = (Object) resultsNMR;
			ret = mapper.writeValueAsString(PeakForestPruneUtils.pruneNMR2Dspectra(resultsNMR));
		} catch (final Exception e) {
			ret = JsonDumperTools.extractErrorAsJson(mapper, e);
		}
		return ret;
	}

}
