package fr.metabohub.peakforest.ws.utils;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;

import fr.metabohub.peakforest.ws.struct.ObjectError;

public class JsonDumperTools {

	/**
	 * Process the output response with an optional JSONP callback
	 * 
	 * @param callback the optional JSONP callback (set to NULL to ignore)
	 * @param json     the JSON string to return
	 * @return a processed JSON string
	 */
	public static String returnCallback(final String callback, final String json) {
		return (callback != null && callback.trim().length() > 0) ? //
				callback + "(" + json + ")"//
				: json;
	}

	/**
	 * Dump an {@link ObjectMapper} into a JSON string
	 * 
	 * @param mapper the mapper to process
	 * @param ex     the exception to dump
	 * @return a JSON formatted error string
	 */
	public static String extractErrorAsJson(//
			final ObjectMapper mapper, //
			final Exception ex) {
		String ret;
		final ObjectError err = new ObjectError(ex.getMessage());
		try {
			ret = mapper.writeValueAsString(err);
		} catch (final IOException e1) {
			ret = "{\"success\":false,\"error\":\"" + e1.getMessage() + "\"}";
		}
		return ret;
	}

}
