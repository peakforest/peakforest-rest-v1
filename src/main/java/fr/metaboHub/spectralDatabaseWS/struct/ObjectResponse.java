package fr.metaboHub.spectralDatabaseWS.struct;

/**
 * Create an object response use to for XML / Json response
 * 
 * @author Nils Paulhe
 * 
 */
public class ObjectResponse {

	/* ********************************************************************* */
	/* Class parameters */
	/* ********************************************************************* */
	boolean success = false;
	String sddURL = null;
	String error = "";
	String htmlZipURL = "";

	/* ********************************************************************* */
	/* Class constructors */
	/* ********************************************************************* */
	/**
	 * basic empty constructor
	 */
	public ObjectResponse() {
		super();
	}

	/* ********************************************************************* */
	/* Class getters / setters */
	/* ********************************************************************* */
	/**
	 * @return the success
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * @param success
	 *            the success to set
	 */
	public void setSuccess(boolean success) {
		this.success = success;
	}

	/**
	 * @return the sddURL
	 */
	public String getSddURL() {
		return sddURL;
	}

	/**
	 * @param sddURL
	 *            the sddURL to set
	 */
	public void setSddURL(String sddURL) {
		this.sddURL = sddURL;
	}

	/**
	 * @return the error
	 */
	public String getError() {
		return error;
	}

	/**
	 * @param error
	 *            the error to set
	 */
	public void setError(String error) {
		this.error = error;
	}

	/**
	 * @return the htmlZipURL
	 */
	public String getHtmlZipURL() {
		return htmlZipURL;
	}

	/**
	 * @param htmlZipURL
	 *            the url of generated file to set
	 */
	public void setHtmlZipURL(String htmlZipURL) {
		this.htmlZipURL = htmlZipURL;
	}

}
