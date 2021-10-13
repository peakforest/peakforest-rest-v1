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
import fr.metabohub.peakforest.mapper.MassPeakMapper;
import fr.metabohub.peakforest.model.compound.Compound;
import fr.metabohub.peakforest.model.spectrum.CompoundSpectrum;
import fr.metabohub.peakforest.model.spectrum.FragmentationLCSpectrum;
import fr.metabohub.peakforest.model.spectrum.MassPeak;
import fr.metabohub.peakforest.model.spectrum.MassSpectrum;
import fr.metabohub.peakforest.services.spectrum.MassPeakManagementService;
import fr.metabohub.peakforest.utils.Utils;

/**
 * 
 * @author Nils Paulhe
 * 
 */
@Path("/spectra/lcmsms/peaks")
public class FragmentationLCMSPeaksImpl extends SpectralDatabaseImpl {

	// /////////////////////

	/**
	 * @param min
	 * @param max
	 * @param callback
	 * @param mode
	 * @param token
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/get-range/{min}/{max}")
	public String getPeaksByRange(@PathParam("min") double min, @PathParam("max") double max,
			@QueryParam("callback") String callback, @QueryParam("mode") String mode,
			@QueryParam("token") String token) {
		// check token
		if (!TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}

		// based on POST method
		String json = getPeaksByRange(min, max, mode, false);
		// returning JSON or JSONP
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	/**
	 * @param min
	 * @param max
	 * @param callback
	 * @param mode
	 * @param token
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/get-range-clean/{min}/{max}")
	public String getPeaksByRangeClean(@PathParam("min") double min, @PathParam("max") double max,
			@QueryParam("callback") String callback, @QueryParam("mode") String mode,
			@QueryParam("token") String token) {
		// check token
		if (!TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		// based on POST method
		String json = getPeaksByRange(min, max, mode, true);
		// returning JSON or JSONP
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	//
	/**
	 * @param min
	 * @param max
	 * @param mode
	 * @param light
	 * @return
	 */
	public String getPeaksByRange(double min, double max, String mode, boolean light) {
		// init
		String dbName = Utils.getBundleConfElement("hibernate.connection.database.dbName");
		String login = Utils.getBundleConfElement("hibernate.connection.database.username");
		String password = Utils.getBundleConfElement("hibernate.connection.database.password");
		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;
		Short modeS = null;
		if (mode != null)
			switch (mode.toLowerCase()) {
			case "pos":
			case "positive":
				modeS = MassSpectrum.MASS_SPECTRUM_POLARITY_POSITIVE;
				break;
			case "neg":
			case "negative":
				modeS = MassSpectrum.MASS_SPECTRUM_POLARITY_NEGATIVE;
				break;
			default:
				modeS = null;
				break;
			}
		try {
			// search
			List<MassPeak> results = MassPeakManagementService.getAllInRange(min, max, modeS,
					FragmentationLCSpectrum.class, dbName, login, password);

			if (light) {
				List<MassPeakMapper> listMapper = new ArrayList<MassPeakMapper>();
				// mapping
				for (MassPeak ori : results) {
					MassPeakMapper mapped = new MassPeakMapper(ori.getMassToChargeRatio(),
							ori.getRelativeIntensity(), ori.getTheoricalMass(), ori.getDeltaPPM(),
							ori.getComposition(), ori.getAttributionAsString(), ori.getSource().getId());
					if (ori.getSource() instanceof CompoundSpectrum)
						for (Compound c : ((CompoundSpectrum) ori.getSource()).getListOfCompounds()) {
							mapped.addCpd(c.getId());
						}
					listMapper.add(mapped);
				}
				// return
				ret = mapper.writeValueAsString(listMapper);
			} else {
				// prune
				results = (List<MassPeak>) Utils.pruneMassPeaks(results);
				// return
				ret = mapper.writeValueAsString(results);
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

	/**
	 * @param callback
	 * @param mode
	 * @param token
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/list-mz")
	public String listPeaksMZ(@QueryParam("callback") String callback, @QueryParam("mode") String mode,
			@QueryParam("token") String token) {
		// check token
		if (!TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		// based on POST method
		String json = listPeaks(mode);
		// returning JSON or JSONP
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	/**
	 * @param mode
	 * @return
	 */
	private String listPeaks(String mode) {
		// init
		String dbName = Utils.getBundleConfElement("hibernate.connection.database.dbName");
		String login = Utils.getBundleConfElement("hibernate.connection.database.username");
		String password = Utils.getBundleConfElement("hibernate.connection.database.password");
		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;

		Short modeS = null;
		if (mode != null)
			switch (mode.toLowerCase()) {
			case "positive":
			case "pos":
				modeS = MassSpectrum.MASS_SPECTRUM_POLARITY_POSITIVE;
				break;
			case "negative":
			case "neg":
				modeS = MassSpectrum.MASS_SPECTRUM_POLARITY_NEGATIVE;
				break;
			case "neutral":
			case "net":
				// modeS = MassSpectrum.MASS_SPECTRUM_POLARITY_;
				// break;
			default:
				break;
			}
		try {
			// search
			List<Double> results = MassPeakManagementService.listMZ(modeS, FragmentationLCSpectrum.class,
					dbName, login, password);
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

}
