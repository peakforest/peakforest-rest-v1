package fr.metaboHub.peakforest.ws;

import java.io.IOException;
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

import fr.metaboHub.peakforest.security.TokenManagementService;
import fr.metaboHub.peakforest.ws.struct.ObjectError;
import fr.metabohub.peakforest.model.AbstractDatasetObject;
import fr.metabohub.peakforest.model.spectrum.FullScanLCSpectrum;
import fr.metabohub.peakforest.services.spectrum.FullScanLCSpectrumManagementService;
import fr.metabohub.peakforest.utils.Utils;

/**
 * 
 * @author Nils Paulhe
 * 
 */
@Path("/spectra/lcms")
public class FullScanLCMSSpectrumImpl extends SpectralDatabaseImpl {

	// ////////////////////////////////////////////////////////////////////////

	/**
	 * @param id
	 * @param callback
	 * @param token
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}")
	public String get(@PathParam("id") long id, @QueryParam("callback") String callback,
			@QueryParam("token") String token) {
		// check token
		if (token == null || token.trim().length() == 0 || !TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}

		// based on POST method
		String json = get(id);
		// returning JSON or JSONP
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	/**
	 * @param ids
	 * @param callback
	 * @param token
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/ids/{ids}")
	public String get(@PathParam("ids") String ids, @QueryParam("callback") String callback,
			@QueryParam("token") String token) {
		// check token
		if (token == null || token.trim().length() == 0 || !TokenManagementService.isTokenValide(token)) {
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
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	/**
	 * @param mode
	 * @param molids
	 * @param callback
	 * @param token
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/count-peaks")
	public String countPeak(@QueryParam("mode") String mode, @QueryParam("molids") String molids,
			@QueryParam("callback") String callback, @QueryParam("token") String token) {
		// check token
		if (token == null || token.trim().length() == 0 || !TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		// based on POST method
		String json = countPeak(molids, mode);
		// returning JSON or JSONP
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/search")
	public String naturalLanguageSearch(@QueryParam("query") String query, @QueryParam("max") Integer max,
			@QueryParam("molids") String molids, @QueryParam("callback") String callback,
			@QueryParam("token") String token) {
		// check token
		if (token == null || token.trim().length() == 0 || !TokenManagementService.isTokenValide(token)) {
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
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/range-rt-min/{min}/{max}")
	public String getRangeRTspectra(@PathParam("min") double min, @PathParam("max") double max,
			@QueryParam("callback") String callback, @QueryParam("columns") String columnCode,
			@QueryParam("token") String token) {
		// check token
		if (token == null || token.trim().length() == 0 || !TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		String[] columns = null;
		if (columnCode != null && !columnCode.equals(""))
			columns = columnCode.split(",");
		// List<Double> peakListDouble = new ArrayList<Double>();

		// based on POST method
		String json = getLCMSspectraRangeRTmin(min, max, columns);
		// returning JSON or JSONP
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/search-naive/{peaklist}/{delta}")
	public String getNMRspectra(@PathParam("peaklist") String peaklist, @PathParam("delta") double delta,
			@QueryParam("callback") String callback, @QueryParam("matchAll") Boolean matchAll,
			@QueryParam("polarity") String polarity, @QueryParam("resolution") String resolution,
			@QueryParam("rt_min") Double rtMin, @QueryParam("rt_max") Double rtMax,
			@QueryParam("column") String columnCode, @QueryParam("rt_meoh_min") Double rtMeOHMin,
			@QueryParam("rt_meoh_max") Double rtMeOHMax, @QueryParam("token") String token) {
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
		String json = getLCMSspectraNaive(peakListDouble, delta, matchAll, polarity, resolution, rtMin, rtMax,
				rtMeOHMin, rtMeOHMax, columnCode);
		// returning JSON or JSONP
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	/**
	 * @param id
	 * @param callback
	 * @param token
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/all/ids")
	public String getAllIds(@QueryParam("callback") String callback, @QueryParam("token") String token) {
		// check token
		if (token == null || token.trim().length() == 0 || !TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}

		// based on POST method
		String json = getAllIds();
		// returning JSON or JSONP
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	/**
	 * @param id
	 * @param callback
	 * @param token
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/all/count")
	public String count(@QueryParam("callback") String callback, @QueryParam("token") String token) {
		// check token
		if (token == null || token.trim().length() == 0 || !TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}

		// based on POST method
		String json = count();
		// returning JSON or JSONP
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	///////////////////////////////////////////////////////////////////////////
	// PRIVATE
	///////////////////////////////////////////////////////////////////////////

	/**
	 * @param id
	 * @return
	 */
	private String get(long id) {
		// init
		String dbName = Utils.getBundleConfElement("hibernate.connection.database.dbName");
		String login = Utils.getBundleConfElement("hibernate.connection.database.username");
		String password = Utils.getBundleConfElement("hibernate.connection.database.password");

		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;
		try {
			FullScanLCSpectrum resultSp = FullScanLCSpectrumManagementService.read(id, dbName, login,
					password);
			// if (resultsNMR.size() > max)
			// resultsNMR = resultsNMR.subList(0, max);
			Object data = (Object) resultSp;
			ret = mapper.writeValueAsString(Utils.prune((AbstractDatasetObject) data));
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

	/**
	 * @param ids
	 * @return
	 */
	private String getList(List<Long> ids) {
		// init
		String dbName = Utils.getBundleConfElement("hibernate.connection.database.dbName");
		String login = Utils.getBundleConfElement("hibernate.connection.database.username");
		String password = Utils.getBundleConfElement("hibernate.connection.database.password");

		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;
		try {
			List<Object> dataRet = new ArrayList<Object>();
			for (long id : ids) {
				FullScanLCSpectrum resultSp = FullScanLCSpectrumManagementService.read(id, dbName, login,
						password);
				Object data = (Object) resultSp;
				dataRet.add(Utils.prune((AbstractDatasetObject) data));
			}
			ret = mapper.writeValueAsString(dataRet);
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

	/**
	 * @param molids
	 * @param mode
	 * @return
	 */
	private String countPeak(String molids, String mode) {
		// init
		String dbName = Utils.getBundleConfElement("hibernate.connection.database.dbName");
		String login = Utils.getBundleConfElement("hibernate.connection.database.username");
		String password = Utils.getBundleConfElement("hibernate.connection.database.password");
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
					modeStd = FullScanLCSpectrum.MASS_SPECTRUM_POLARITY_POSITIVE;
					break;
				case "neg":
				case "negative":
					modeStd = FullScanLCSpectrum.MASS_SPECTRUM_POLARITY_NEGATIVE;
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
							long result = FullScanLCSpectrumManagementService.countPeaks(id, modeStd, dbName,
									login, password);
							results.add(result);
						}
						// case list inchikey
						catch (NumberFormatException e) {
							long result = FullScanLCSpectrumManagementService.countPeaks(s, modeStd, dbName,
									login, password);
							results.add(result);
						}
					ret = mapper.writeValueAsString(results);
				} else { // case uniq
					long result = -1;
					// case uniq id
					try {
						long id = Long.parseLong(molids);
						result = FullScanLCSpectrumManagementService.countPeaks(id, modeStd, dbName, login,
								password);
					}
					// case uniq inchikey
					catch (NumberFormatException e) {
						result = FullScanLCSpectrumManagementService.countPeaks(molids, modeStd, dbName,
								login, password);
					}
					ret = mapper.writeValueAsString(result);
				}

			} else {
				long result = FullScanLCSpectrumManagementService.countPeaks(molids, modeStd, dbName, login,
						password);
				ret = mapper.writeValueAsString(result);
			}
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

	@SuppressWarnings("unchecked")
	private String searchByMolId(List<String> cpdIDs, Integer max) {
		// init
		String dbName = Utils.getBundleConfElement("hibernate.connection.database.dbName");
		String login = Utils.getBundleConfElement("hibernate.connection.database.username");
		String password = Utils.getBundleConfElement("hibernate.connection.database.password");

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
			List<FullScanLCSpectrum> resultsNMR = new ArrayList<FullScanLCSpectrum>();
			if (useInChIKey)
				resultsNMR = FullScanLCSpectrumManagementService.searchCompoundSpectraByInChIKey(cpdIDs,
						dbName, login, password);
			else
				resultsNMR = FullScanLCSpectrumManagementService.searchCompoundSpectra(cpdIDasLong, dbName,
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
			List<FullScanLCSpectrum> resultsNMR = FullScanLCSpectrumManagementService.searchKeywords(query,
					dbName, login, password);
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

	/**
	 * @param min
	 * @param max
	 * @return
	 */
	public String getLCMSspectraNaive(List<Double> peaklist, double delta, Boolean matchAll, String polarity,
			String resolution, Double rtMin, Double rtMax, Double rtMeOHmin, Double rtMeOHmmax,
			String column) {
		Short pola = null;
		Short reso = null;
		if (matchAll == null)
			matchAll = true;
		if (polarity != null)
			switch (polarity.trim().toUpperCase()) {
			case "POS":
			case "POSITIVE":
				pola = FullScanLCSpectrum.MASS_SPECTRUM_POLARITY_POSITIVE;
				break;
			case "NEG":
			case "NEGATIVE":
				pola = FullScanLCSpectrum.MASS_SPECTRUM_POLARITY_NEGATIVE;
				break;
			default:
				break;
			}

		if (resolution != null)
			switch (resolution.trim().toUpperCase()) {
			case "HIGH":
			case "HIGHT":
				reso = FullScanLCSpectrum.MASS_SPECTRUM_RESOLUTION_HIGH;
				break;
			case "LOW":
				reso = FullScanLCSpectrum.MASS_SPECTRUM_RESOLUTION_LOW;
				break;
			default:
				break;
			}

		// init
		String dbName = Utils.getBundleConfElement("hibernate.connection.database.dbName");
		String login = Utils.getBundleConfElement("hibernate.connection.database.username");
		String password = Utils.getBundleConfElement("hibernate.connection.database.password");
		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;
		try {
			// search
			List<FullScanLCSpectrum> results = FullScanLCSpectrumManagementService.search(
					peaklist.toArray(new Double[peaklist.size()]), delta, matchAll, pola, reso, dbName, login,
					password);
			// extra filter
			results = FullScanLCSpectrumManagementService.filter(results, rtMin, rtMax, rtMeOHmin, rtMeOHmmax,
					column);
			// prune
			results = (List<FullScanLCSpectrum>) Utils.pruneFullScanLCMSspectra(results);
			// return
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

	@SuppressWarnings("unchecked")
	private String getLCMSspectraRangeRTmin(double min, double max, String[] columns) {
		// init
		String dbName = Utils.getBundleConfElement("hibernate.connection.database.dbName");
		String login = Utils.getBundleConfElement("hibernate.connection.database.username");
		String password = Utils.getBundleConfElement("hibernate.connection.database.password");

		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;
		List<String> colAsList = new ArrayList<>();
		if (columns != null)
			colAsList = Arrays.asList(columns);
		try {
			List<FullScanLCSpectrum> resultsNMR = FullScanLCSpectrumManagementService.getRangeRTmin(min, max,
					colAsList, dbName, login, password);
			// if (resultsNMR.size() > max)
			// resultsNMR = resultsNMR.subList(0, 50);
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

	///////////////////////////////////////////////////////////////////////////
	private String getAllIds() {
		// init
		String dbName = Utils.getBundleConfElement("hibernate.connection.database.dbName");
		String login = Utils.getBundleConfElement("hibernate.connection.database.username");
		String password = Utils.getBundleConfElement("hibernate.connection.database.password");

		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;
		try {
			List<Long> resultSp = FullScanLCSpectrumManagementService.getAllIDs(dbName, login, password);
			ret = mapper.writeValueAsString((Object) resultSp);
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

	///////////////////////////////////////////////////////////////////////////
	private String count() {
		// init
		String dbName = Utils.getBundleConfElement("hibernate.connection.database.dbName");
		String login = Utils.getBundleConfElement("hibernate.connection.database.username");
		String password = Utils.getBundleConfElement("hibernate.connection.database.password");

		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;
		try {
			Long resultSp = FullScanLCSpectrumManagementService.count(dbName, login, password);
			ret = mapper.writeValueAsString((Object) resultSp);
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
