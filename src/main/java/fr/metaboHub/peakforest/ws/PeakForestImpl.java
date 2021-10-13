package fr.metaboHub.peakforest.ws;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.map.ObjectMapper;

import fr.metaboHub.peakforest.ws.struct.ObjectError;
import fr.metabohub.peakforest.utils.Utils;

/**
 * 
 * @author Nils Paulhe
 * 
 */
@Path("/infos")
public class PeakForestImpl extends SpectralDatabaseImpl {

	/**
	 * @param query
	 * @param max
	 * @param callback
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/release")
	public String release(@QueryParam("callback") String callback) {

		String json = release();
		// returning JSON or JSONP
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	/**
	 * @param query
	 * @param max
	 * @return
	 */
	public String release() {
		// init
		String version = Utils.getBundleConfElement("build.version");
		String timestamp = Utils.getBundleConfElement("build.timestamp");
		String sha1 = Utils.getBundleConfElement("build.sha1");

		// short sha1
		String shortSha1 = null;
		if (sha1 != null && sha1.length() > 7)
			shortSha1 = sha1.substring(0, 8);

		// date human readable
		String date = "" + timestamp;
		try {
			Long timeStamp = Long.parseLong(timestamp);
			java.util.Date time = new java.util.Date((long) timeStamp);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd - HH:mm");
			date = dateFormat.format(time);
		} catch (Exception e) {
		}

		// run
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> data = new HashMap<String, String>();
		String ret;
		try {
			//
			data.put("version", version);
			data.put("timestamp", timestamp);
			data.put("sha1", sha1);
			//
			data.put("shortSha1", shortSha1);
			data.put("date", date);
			//
			ret = mapper.writeValueAsString(data);
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
	@Path("/galaxy")
	public String groot(@QueryParam("callback") String callback) {
		String json = "{\"groot\":\"I AM GROOT!\"}";
		// returning JSON or JSONP
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

}
