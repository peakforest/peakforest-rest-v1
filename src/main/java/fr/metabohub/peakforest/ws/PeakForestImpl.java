package fr.metabohub.peakforest.ws;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.map.ObjectMapper;

import fr.metabohub.peakforest.utils.PeakForestUtils;
import fr.metabohub.peakforest.ws.utils.JsonDumperTools;

/**
 * 
 * @author Nils Paulhe
 * 
 */
@Path("/infos")
public class PeakForestImpl extends SpectralDatabaseImpl {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/release")
	public String release(@QueryParam("callback") String callback) {
		String json = release();
		// returning JSON or JSONP
		return JsonDumperTools.returnCallback(callback, json);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/galaxy")
	public String groot(@QueryParam("callback") String callback) {
		String json = "{\"groot\":\"I AM GROOT!\"}";
		// returning JSON or JSONP
		return JsonDumperTools.returnCallback(callback, json);
	}

	private String release() {
		// init
		final String version = PeakForestUtils.getBundleConfElement("build.version");
		final String timestamp = PeakForestUtils.getBundleConfElement("build.timestamp");
		final String sha1 = PeakForestUtils.getBundleConfElement("build.sha1");

		// short sha1
		String shortSha1 = null;
		if (sha1 != null && sha1.length() > 7)
			shortSha1 = sha1.substring(0, 8);

		// run
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> data = new HashMap<String, String>();
		String ret;
		try {
			// raw in properties
			data.put("version", version);
			data.put("timestamp", timestamp);
			data.put("sha1", sha1);
			// added
			data.put("shortSha1", shortSha1);
			//
			ret = mapper.writeValueAsString(data);
		} catch (final Exception e) {
			ret = JsonDumperTools.extractErrorAsJson(mapper, e);
		}
		return ret;
	}

}
