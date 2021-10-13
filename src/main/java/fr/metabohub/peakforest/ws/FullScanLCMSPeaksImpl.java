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

import fr.metabohub.peakforest.dao.spectrum.MassPeakDao;
import fr.metabohub.peakforest.mapper.MassPeakMapper;
import fr.metabohub.peakforest.model.compound.Compound;
import fr.metabohub.peakforest.model.spectrum.CompoundSpectrum;
import fr.metabohub.peakforest.model.spectrum.FullScanLCSpectrum;
import fr.metabohub.peakforest.model.spectrum.MassPeak;
import fr.metabohub.peakforest.model.spectrum.MassSpectrum;
import fr.metabohub.peakforest.security.TokenManagementService;
import fr.metabohub.peakforest.services.spectrum.MassPeakManagementService;
import fr.metabohub.peakforest.utils.PeakForestPruneUtils;
import fr.metabohub.peakforest.ws.utils.JsonDumperTools;

/**
 * 
 * @author Nils Paulhe
 */
@Path("/spectra/lcms/peaks")
public class FullScanLCMSPeaksImpl extends SpectralDatabaseImpl {

	// /////////////////////

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
		return JsonDumperTools.returnCallback(callback, json);
	}

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
		return JsonDumperTools.returnCallback(callback, json);
	}

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
		return JsonDumperTools.returnCallback(callback, json);
	}

	private String getPeaksByRange(double min, double max, String mode, boolean light) {
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
			List<MassPeak> results = MassPeakManagementService.getAllInRange(min, max, modeS, FullScanLCSpectrum.class);

			if (light) {
				List<MassPeakMapper> listMapper = new ArrayList<MassPeakMapper>();
				// mapping
				for (MassPeak ori : results) {
					MassPeakMapper mapped = new MassPeakMapper(ori.getMassToChargeRatio(), ori.getRelativeIntensity(),
							ori.getTheoricalMass(), ori.getDeltaPPM(), ori.getComposition(),
							ori.getAttributionAsString(), ori.getSource().getId());
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
				results = (List<MassPeak>) PeakForestPruneUtils.pruneMassPeaks(results);
				// return
				ret = mapper.writeValueAsString(results);
			}
		} catch (final Exception e) {
			ret = JsonDumperTools.extractErrorAsJson(mapper, e);
		}
		return ret;
	}

	private String listPeaks(String mode) {
		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;

		final Short modeS = MassSpectrum.getStandardizedPolarity(mode);
		try {
			// search
			List<Double> results = MassPeakDao.listMZ(modeS, FullScanLCSpectrum.class);
			// return
			ret = mapper.writeValueAsString(results);
		} catch (final Exception e) {
			ret = JsonDumperTools.extractErrorAsJson(mapper, e);
		}
		return ret;
	}

}
