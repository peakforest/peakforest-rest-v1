package fr.metabohub.peakforest.ws;

import java.lang.management.ManagementFactory;

public class SpectralDatabaseImpl {

	private static double DEFAULT_CPU_LOAD_THRESHOLD = 0.8;

	public static boolean serverReady() {
		return ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage() < DEFAULT_CPU_LOAD_THRESHOLD;
	}

}
