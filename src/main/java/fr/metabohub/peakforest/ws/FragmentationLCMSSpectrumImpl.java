package fr.metabohub.peakforest.ws;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.map.ObjectMapper;

import fr.metabohub.peakforest.dao.spectrum.FragmentationLCSpectrumDao;
import fr.metabohub.peakforest.model.AbstractDatasetObject;
import fr.metabohub.peakforest.model.spectrum.FragmentationLCSpectrum;
import fr.metabohub.peakforest.security.TokenManagementService;
import fr.metabohub.peakforest.services.spectrum.FragmentationLCSpectrumManagementService;
import fr.metabohub.peakforest.utils.PeakForestPruneUtils;
import fr.metabohub.peakforest.ws.utils.JsonDumperTools;

/**
 * 
 * @author Nils Paulhe
 * 
 */
@Path("/spectra/lcmsms")
public class FragmentationLCMSSpectrumImpl extends SpectralDatabaseImpl {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}")
	public String get(@PathParam("id") long id, @QueryParam("callback") String callback,
			@QueryParam("token") String token) {
		// check token
		if (!TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}

		// based on POST method
		String json = get(id);
		// returning JSON or JSONP
		return JsonDumperTools.returnCallback(callback, json);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/ids/{ids}")
	public String get(@PathParam("ids") String ids, @QueryParam("callback") String callback,
			@QueryParam("token") String token) {
		// check token
		if (!TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		// String -> List<Long>
		List<Long> idsL = new ArrayList<>();
		for (String s : ids.split(",")) {
			s = s.replace("PFs", "");
			try {
				idsL.add(Long.parseLong(s));
			} catch (NumberFormatException e) {
			}
		}
		String json = getList(idsL);
		// returning JSON or JSONP
		return JsonDumperTools.returnCallback(callback, json);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/count-peaks")
	public String countPeak(@QueryParam("mode") String mode, @QueryParam("molids") String molids,
			@QueryParam("callback") String callback, @QueryParam("token") String token) {
		// check token
		if (!TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		// based on POST method
		String json = countPeak(molids, mode);
		// returning JSON or JSONP
		return JsonDumperTools.returnCallback(callback, json);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/search")
	public String naturalLanguageSearch(@QueryParam("query") String query, @QueryParam("max") Integer max,
			@QueryParam("molids") String molids, @QueryParam("callback") String callback,
			@QueryParam("token") String token) {
		// check token
		if (!TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		List<String> cpdIDs = new ArrayList<String>();
		if (molids != null)
			cpdIDs = Arrays.asList(molids.split(","));
		// based on POST method
		if (max == null)
			max = 500;
		String json = null;
		if (cpdIDs.isEmpty())
			json = naturalLanguageSearch(query, max);
		else
			json = searchByMolId(cpdIDs, max);
		// returning JSON or JSONP
		return JsonDumperTools.returnCallback(callback, json);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/search-naive/{peaklist}/{delta}")
	public String getMSMSspectra(@PathParam("peaklist") String peaklist, @PathParam("delta") double delta,
			@QueryParam("callback") String callback, @QueryParam("matchAll") Boolean matchAll,
			@QueryParam("polarity") String polarity, @QueryParam("resolution") String resolution,
			@QueryParam("rt_min") Double rtMin, @QueryParam("rt_max") Double rtMax,
			@QueryParam("column") String columnCode, @QueryParam("rt_meoh_min") Double rtMeOHMin,
			@QueryParam("rt_meoh_max") Double rtMeOHMax, @QueryParam("token") String token) {
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
		String json = getLCMSspectraNaive(peakListDouble, delta, matchAll, polarity, resolution, rtMin, rtMax,
				rtMeOHMin, rtMeOHMax, columnCode);
		// returning JSON or JSONP
		return JsonDumperTools.returnCallback(callback, json);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/range-rt-min/{min}/{max}")
	public String getRangeRTspectra(@PathParam("min") double min, @PathParam("max") double max,
			@QueryParam("callback") String callback, @QueryParam("columns") String columnCode,
			@QueryParam("token") String token) {
		// check token
		if (!TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		String[] columns = null;
		if (columnCode != null && !columnCode.equals(""))
			columns = columnCode.split(",");
		// List<Double> peakListDouble = new ArrayList<Double>();

		// based on POST method
		String json = getLCMSspectraRangeRTmin(min, max, columns);
		// returning JSON or JSONP
		return JsonDumperTools.returnCallback(callback, json);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/from-precursor/{precursorMass}")
	public String getSpectraFromPrecursor(
			/* path param */
			@PathParam("precursorMass") double precursorMass,
			/* mandatory query param */
			@QueryParam("mode") String mode, // default: BOTH
			/* opt query param */
			@QueryParam("precursorMassDelta") Double precursorMassDelta, // default: 0.1
			// @QueryParam("precursorMassUnit") String precursorMassUnit, // default: MZ
			/* default query param */
			@QueryParam("callback") String callback, @QueryParam("token") String token) {

		// 0 - check token
		if (!TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}

		// I - check user params. (mandatory and optional)
		Short polarity = FragmentationLCSpectrum.getStandardizedPolarity(mode);
		double precursorMassDeltaChecked = 0.1;
		if (precursorMassDelta != null) {
			precursorMassDeltaChecked = precursorMassDelta;
		}

		// II - API processing

		// III - return
		// based on LOCAL method
		String json = getSpectraFromPrecursor(precursorMass, precursorMassDeltaChecked, polarity);

		// returning JSON or JSONP
		return JsonDumperTools.returnCallback(callback, json);

	}

	///////////////////////////////////////////////////////////////////////////
	// PRIVATE

	private String get(long id) {
		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;
		try {
			FragmentationLCSpectrum resultSp = FragmentationLCSpectrumManagementService.read(id);
			// if (resultsNMR.size() > max)
			// resultsNMR = resultsNMR.subList(0, max);
			Object data = (Object) resultSp;
			ret = mapper.writeValueAsString(PeakForestPruneUtils.prune((AbstractDatasetObject) data));
		} catch (final Exception e) {
			ret = JsonDumperTools.extractErrorAsJson(mapper, e);
		}
		return ret;
	}

	private String getList(List<Long> ids) {
		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;
		try {
			List<Object> dataRet = new ArrayList<Object>();
			for (long id : ids) {
				FragmentationLCSpectrum resultSp = FragmentationLCSpectrumManagementService.read(id);
				Object data = (Object) resultSp;
				dataRet.add(PeakForestPruneUtils.prune((AbstractDatasetObject) data));
			}
			ret = mapper.writeValueAsString(dataRet);
		} catch (final Exception e) {
			ret = JsonDumperTools.extractErrorAsJson(mapper, e);
		}
		return ret;
	}

	private String countPeak(String molids, String mode) {
		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;
		try {
			//
			Short modeStd = null;
			if (mode != null)
				switch (mode.trim().toLowerCase()) {
				case "pos":
				case "positive":
					modeStd = FragmentationLCSpectrum.MASS_SPECTRUM_POLARITY_POSITIVE;
					break;
				case "neg":
				case "negative":
					modeStd = FragmentationLCSpectrum.MASS_SPECTRUM_POLARITY_NEGATIVE;
					break;
				default:
					break;
				}
			// case uniq inchikey:
			if (molids != null && !molids.equals("")) {
				// case list

				if (molids.contains(",")) {
					List<Long> results = new ArrayList<Long>();
					for (String s : molids.split(","))
						// case list id
						try {
							long id = Long.parseLong(s);
							long result = FragmentationLCSpectrumDao.countPeaks(id, modeStd);
							results.add(result);
						}
						// case list inchikey
						catch (NumberFormatException e) {
							long result = FragmentationLCSpectrumDao.countPeaks(s, modeStd);
							results.add(result);
						}
					ret = mapper.writeValueAsString(results);
				} else { // case uniq
					long result = -1;
					// case uniq id
					try {
						long id = Long.parseLong(molids);
						result = FragmentationLCSpectrumDao.countPeaks(id, modeStd);
					}
					// case uniq inchikey
					catch (NumberFormatException e) {
						result = FragmentationLCSpectrumDao.countPeaks(molids, modeStd);
					}
					ret = mapper.writeValueAsString(result);
				}

			} else {
				long result = FragmentationLCSpectrumDao.countPeaks(molids, modeStd);
				ret = mapper.writeValueAsString(result);
			}
		} catch (final Exception e) {
			ret = JsonDumperTools.extractErrorAsJson(mapper, e);
		}
		return ret;
	}

	private String searchByMolId(List<String> cpdIDs, Integer max) {
		List<Long> cpdIDasLong = new ArrayList<Long>();
		boolean useInChIKey = true;

		for (String s : cpdIDs) {
			try {
				cpdIDasLong.add(Long.parseLong(s));
				useInChIKey = false;
			} catch (NumberFormatException e) {
			}
		}

		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;
		try {
			List<FragmentationLCSpectrum> resultsNMR = new ArrayList<FragmentationLCSpectrum>();
			if (useInChIKey)
				resultsNMR = FragmentationLCSpectrumManagementService.searchCompoundSpectraByInChIKey(cpdIDs);
			else
				resultsNMR = FragmentationLCSpectrumManagementService.searchCompoundSpectra(cpdIDasLong);
			if (resultsNMR.size() > max)
				resultsNMR = resultsNMR.subList(0, max);
//			Object data = (Object) resultsNMR;
			ret = mapper.writeValueAsString(PeakForestPruneUtils.pruneFragmentationLCMSspectra(resultsNMR));
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
			List<FragmentationLCSpectrum> resultsNMR = FragmentationLCSpectrumManagementService.searchKeywords(query);
			if (resultsNMR.size() > max)
				resultsNMR = resultsNMR.subList(0, max);
//			Object data = (Object) resultsNMR;
			ret = mapper.writeValueAsString(PeakForestPruneUtils.pruneFragmentationLCMSspectra(resultsNMR));
		} catch (final Exception e) {
			ret = JsonDumperTools.extractErrorAsJson(mapper, e);
		}
		return ret;
	}

	private String getLCMSspectraNaive(List<Double> peaklist, double delta, Boolean matchAll, String polarity,
			String resolution, Double rtMin, Double rtMax, Double rtMeOHmin, Double rtMeOHmmax, String column) {
		Short pola = null;
		Short reso = null;
		if (matchAll == null)
			matchAll = true;
		if (polarity != null)
			switch (polarity.trim().toUpperCase()) {
			case "POS":
			case "POSITIVE":
				pola = FragmentationLCSpectrum.MASS_SPECTRUM_POLARITY_POSITIVE;
				break;
			case "NEG":
			case "NEGATIVE":
				pola = FragmentationLCSpectrum.MASS_SPECTRUM_POLARITY_NEGATIVE;
				break;
			default:
				break;
			}

		if (resolution != null)
			switch (resolution.trim().toUpperCase()) {
			case "HIGH":
			case "HIGHT":
				reso = FragmentationLCSpectrum.MASS_SPECTRUM_RESOLUTION_HIGH;
				break;
			case "LOW":
				reso = FragmentationLCSpectrum.MASS_SPECTRUM_RESOLUTION_LOW;
				break;
			default:
				break;
			}

		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;
		try {
			// search
			List<FragmentationLCSpectrum> results = FragmentationLCSpectrumManagementService
					.search(peaklist.toArray(new Double[peaklist.size()]), delta, matchAll, pola, reso);
			// extra filter
			results = FragmentationLCSpectrumManagementService.filter(results, rtMin, rtMax, rtMeOHmin, rtMeOHmmax,
					column);
			// prune
			results = (List<FragmentationLCSpectrum>) PeakForestPruneUtils.pruneFragmentationLCMSspectra(results);
			// return
			ret = mapper.writeValueAsString(results);
		} catch (final Exception e) {
			ret = JsonDumperTools.extractErrorAsJson(mapper, e);
		}
		return ret;
	}

	private String getLCMSspectraRangeRTmin(double min, double max, String[] columns) {
		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;
		List<String> colAsList = new ArrayList<>();
		if (columns != null)
			colAsList = Arrays.asList(columns);
		try {
			List<FragmentationLCSpectrum> resultsNMR = FragmentationLCSpectrumManagementService.getRangeRTmin(min, max,
					colAsList);
			// if (resultsNMR.size() > max)
			// resultsNMR = resultsNMR.subList(0, 50);
//			Object data = (Object) resultsNMR;
			ret = mapper.writeValueAsString(PeakForestPruneUtils.pruneFragmentationLCMSspectra(resultsNMR));
		} catch (final Exception e) {
			ret = JsonDumperTools.extractErrorAsJson(mapper, e);
		}
		return ret;
	}

	private String getSpectraFromPrecursor(double precursorMass, double precursorMassDeltaChecked, Short polarity) {
		// zzbop
		String ret = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			List<FragmentationLCSpectrum> resultsNMR = FragmentationLCSpectrumDao.getFromPrecursor(precursorMass,
					precursorMassDeltaChecked, polarity);
			// if (resultsNMR.size() > max)
			// resultsNMR = resultsNMR.subList(0, 50);
//			Object data = (Object) resultsNMR;
			ret = mapper.writeValueAsString(PeakForestPruneUtils.pruneFragmentationLCMSspectra(resultsNMR));
		} catch (final Exception e) {
			ret = JsonDumperTools.extractErrorAsJson(mapper, e);
		}
		return ret;
	}
}
