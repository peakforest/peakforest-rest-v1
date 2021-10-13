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

import fr.metabohub.peakforest.mapper.NMR1DspectrumMapper;
import fr.metabohub.peakforest.mapper.NMRspectraCleaner;
import fr.metabohub.peakforest.model.spectrum.NMR1DSpectrum;
import fr.metabohub.peakforest.security.TokenManagementService;
import fr.metabohub.peakforest.services.spectrum.NMR1DSpectrumManagementService;
import fr.metabohub.peakforest.utils.PeakForestPruneUtils;
import fr.metabohub.peakforest.ws.utils.JsonDumperTools;

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
		if (!TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		//

		// based on POST method
		ObjectMapper mapper = new ObjectMapper();
		String json;
		try {
			// search
			NMR1DSpectrum result = NMR1DSpectrumManagementService.read(id);
			// prune
			result = (NMR1DSpectrum) PeakForestPruneUtils.prune(result);
			// return
			json = mapper.writeValueAsString(result);
		} catch (final Exception e) {
			json = JsonDumperTools.extractErrorAsJson(mapper, e);
		}
		// returning JSON or JSONP
		return JsonDumperTools.returnCallback(callback, json);
	}

	// /////////////////////

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/search-naive/{peaklist}/{delta}")
	public String getNMRspectra(@PathParam("peaklist") String peaklist, @PathParam("delta") double delta,
			@QueryParam("callback") String callback, @QueryParam("matchAll") Boolean matchAll,
			@QueryParam("token") String token) {
		// check token
		if (!TokenManagementService.isTokenValide(token)) {
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
		return JsonDumperTools.returnCallback(callback, json);
	}

	public String getNMRspectraNaive(List<Double> peaklist, double delta, Boolean matchAll) {
		if (matchAll == null)
			matchAll = true;
		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;
		try {
			// // search
			List<NMR1DSpectrum> results = NMR1DSpectrumManagementService
					.search(peaklist.toArray(new Double[peaklist.size()]), delta, matchAll);
			// // prune
			results = (List<NMR1DSpectrum>) PeakForestPruneUtils.pruneNMR1Dspectra(results);
			// // return
			ret = mapper.writeValueAsString(results);
		} catch (final Exception e) {
			ret = JsonDumperTools.extractErrorAsJson(mapper, e);
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
		if (!TokenManagementService.isTokenValide(token)) {
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
		return JsonDumperTools.returnCallback(callback, json);
	}

	public String getNMRspectraNaiveClean(List<Double> peaklist, double delta, Boolean matchAll) {
		if (matchAll == null)
			matchAll = true;
		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;
		try {
			// search
			List<NMR1DSpectrum> results = NMR1DSpectrumManagementService
					.search(peaklist.toArray(new Double[peaklist.size()]), delta, matchAll);
			// prune
			List<NMR1DspectrumMapper> resultsClean = NMRspectraCleaner.clean1D(results);
			// return
			ret = mapper.writeValueAsString(resultsClean);
		} catch (final Exception e) {
			ret = JsonDumperTools.extractErrorAsJson(mapper, e);
		}
		return ret;
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

	private String naturalLanguageSearch(String query, int max) {
		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;
		try {
			List<NMR1DSpectrum> resultsNMR = NMR1DSpectrumManagementService.searchKeywords(query);
			if (resultsNMR.size() > max)
				resultsNMR = resultsNMR.subList(0, max);
//			Object data = (Object) resultsNMR;
			ret = mapper.writeValueAsString(PeakForestPruneUtils.pruneNMR1Dspectra(resultsNMR));
		} catch (final Exception e) {
			ret = JsonDumperTools.extractErrorAsJson(mapper, e);
		}
		return ret;
	}

}
