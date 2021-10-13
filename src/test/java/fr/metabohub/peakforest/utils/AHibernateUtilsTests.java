package fr.metabohub.peakforest.utils;

import java.util.ResourceBundle;

import org.junit.BeforeClass;

public abstract class AHibernateUtilsTests {

	/**
	 * Test fingerprint: use to get unic IDs in database
	 */
	protected static final String test_fingerprint = "_fp_" + System.currentTimeMillis();

	/**
	 * Set class' static param.
	 * 
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// set config file
		PeakForestUtils.setBundleConf(ResourceBundle.getBundle("confTest"));
	}

}
