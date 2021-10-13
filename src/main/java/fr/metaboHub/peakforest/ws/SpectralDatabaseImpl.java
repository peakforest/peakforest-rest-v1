package fr.metaboHub.peakforest.ws;

import java.lang.management.ManagementFactory;

/**
 * 
 * @author Nils Paulhe
 * 
 */
public class SpectralDatabaseImpl {

	private static double DEFAULT_CPU_LOAD_THRESHOLD = 0.8;

	/**
	 * @return
	 */
	public static boolean serverReady() {
		return ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage() < DEFAULT_CPU_LOAD_THRESHOLD;
	}

}
