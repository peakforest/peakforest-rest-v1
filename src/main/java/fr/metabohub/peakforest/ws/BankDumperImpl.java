package fr.metabohub.peakforest.ws;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fr.metabohub.peakforest.model.compound.ChemicalCompound;
import fr.metabohub.peakforest.model.compound.GenericCompound;
import fr.metabohub.peakforest.model.compound.StructureChemicalCompound;
import fr.metabohub.peakforest.model.metadata.AnalyzerLiquidMassIonization;
import fr.metabohub.peakforest.model.spectrum.FragmentationLCSpectrum;
import fr.metabohub.peakforest.model.spectrum.FullScanLCSpectrum;
import fr.metabohub.peakforest.model.spectrum.MassSpectrum;
import fr.metabohub.peakforest.security.TokenManagementService;
import fr.metabohub.peakforest.services.compound.ChemicalCompoundManagementService;
import fr.metabohub.peakforest.services.compound.GenericCompoundManagementService;
import fr.metabohub.peakforest.services.spectrum.FragmentationLCSpectrumManagementService;
import fr.metabohub.peakforest.services.spectrum.FullScanLCSpectrumManagementService;
import fr.metabohub.spectralibraries.dumper.BiHdumperBank;
import fr.metabohub.spectralibraries.mapper.PeakForestDataMapper;

@Path("/galaxy")
public class BankDumperImpl extends SpectralDatabaseImpl {

	private static String BIH_FILES_PREFIX = "Bank_InHouse__";

	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path("/subbank")
	public Response getSubBank(@QueryParam("token") String token,
			// Global tech
			@QueryParam("spectraTechnology") String spectraTechnology,
			// MS tech
			@QueryParam("polarity") String polarity, //
			@QueryParam("resolution") String resolution, //
			// Ionization Method / Ion analyzer type
			@QueryParam("ionization_method") String ionizationMethod, //
			@QueryParam("ion_analyzer_type") String ionAnalyzerType //
	) throws IOException {

		// check token
		if (!TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenFileError();
		}

		// init
		if (spectraTechnology == null || spectraTechnology.isEmpty())
			spectraTechnology = "LCMS";

		// build file name
		String fileName = spectraTechnology;

		// build file name: MS / MSMS
		if (polarity != null && !polarity.trim().isEmpty()) {
			fileName += "_" + polarity;
		}
		if (resolution != null && !resolution.trim().isEmpty()) {
			fileName += "_" + resolution;
		}
		if (ionizationMethod != null && !ionizationMethod.trim().isEmpty()) {
			fileName += "_" + ionizationMethod;
		}
		if (ionAnalyzerType != null && !ionAnalyzerType.trim().isEmpty()) {
			fileName += "_" + ionAnalyzerType;
		}

		// todo: build file name: NMR

		// build file name: date
		fileName += "_" + (new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")).format(new Date());

		// build files objects
		File tmpDirectory = createTempDirectory();
		File csvFile = new File(tmpDirectory.getAbsolutePath() + File.separator + fileName + ".bank_in_house.tsv");
		// File zipFile = createTempFile(".bank_in_house.tsv.zip");
		List<PeakForestDataMapper> listOfDataMapper = new ArrayList<PeakForestDataMapper>();
		Map<String, StructureChemicalCompound> compoundCache = new HashMap<String, StructureChemicalCompound>();

		// check filters
		Short polarityStd = null, resolutionStd = null;

		// POLARITY
		if (polarity != null && !polarity.isEmpty())
			polarityStd = MassSpectrum.getStandardizedPolarity(polarity);

		// RESOLUTION
		if (resolution != null && !polarity.isEmpty())
			resolutionStd = MassSpectrum.getStandardizedResolution(resolution);

		// Ionization Method
		final Short ionizationMethodStd = AnalyzerLiquidMassIonization.getStandardizedIonization(ionizationMethod);

		// check techno
		switch (spectraTechnology.toLowerCase()) {
		case "nmr":
			// TODO case NMR
			break;
		case "lcms":
		case "lc-ms":
		default:
			// case LCMS
			try {
				List<FullScanLCSpectrum> dataMSMS = FullScanLCSpectrumManagementService.getSubBank(polarityStd,
						resolutionStd, ionizationMethodStd, ionAnalyzerType);
				for (FullScanLCSpectrum data : dataMSMS) {
					PeakForestDataMapper mapper = new PeakForestDataMapper(PeakForestDataMapper.DATA_TYPE_LC_MSMS);
					gatherCpdData(data, mapper, compoundCache);
					mapper.getListOfFullScanLCSpectrum().add(data);
					listOfDataMapper.add(mapper);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			break;
		case "lcmsms":
		case "lc-msms":
		case "msms":
			// case LCMSMS
			try {
				List<FragmentationLCSpectrum> dataMSMS = FragmentationLCSpectrumManagementService
						.getSubBank(polarityStd, resolutionStd, ionizationMethodStd, ionAnalyzerType);
				for (FragmentationLCSpectrum data : dataMSMS) {
					PeakForestDataMapper mapper = new PeakForestDataMapper(PeakForestDataMapper.DATA_TYPE_LC_MSMS);
					gatherCpdData(data, mapper, compoundCache);
					mapper.getListOfFragmentationLCSpectrum().add(data);
					listOfDataMapper.add(mapper);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			break;
		}

		// build file
		try {
			BiHdumperBank.dumpFile(csvFile, listOfDataMapper);
			// System.out.println("[info] '" + tmpDirectory + "' write: SUCCESS!");
		} catch (IOException e) {
			e.printStackTrace();
			// System.err.println("[fail] '" + tmpDirectory + "' write: FAIL!");
		}

		// zip current file
		// IOUtils.zipDirectory(tmpDirectory, zipFile);

		// clean old file (> 30 min)
		cleanOldFiles();

		// stream ZIP
		return Response.ok(csvFile, MediaType.APPLICATION_OCTET_STREAM)// zipFile
				.header("Content-Disposition", "attachment; filename=\"" + csvFile.getName() + "\"").build();
	}

	private void gatherCpdData(//
			MassSpectrum data, PeakForestDataMapper mapper, //
			Map<String, StructureChemicalCompound> compoundCache) throws Exception {
		if (data.getLabel() == MassSpectrum.SPECTRUM_LABEL_REFERENCE && data.getListOfCompounds().size() == 1) {
			StructureChemicalCompound rcc = (StructureChemicalCompound) data.getListOfCompounds().get(0);
			StructureChemicalCompound rccCatch = null;
			if (compoundCache.containsKey(rcc.getInChIKey())) {
				rccCatch = compoundCache.get(rcc.getInChIKey());
			} else {
				if (rcc instanceof ChemicalCompound) {
					rccCatch = ChemicalCompoundManagementService.read(rcc.getId());
				} else if (rcc instanceof GenericCompound) {
					rccCatch = GenericCompoundManagementService.read(rcc.getId());
				}
				compoundCache.put(rcc.getInChIKey(), rccCatch);
			}
			((StructureChemicalCompound) data.getListOfCompounds().get(0))
					.setListOfCompoundNames(rcc.getListOfCompoundNames());
			mapper.setInChIKey(rccCatch.getInChIKey());
			mapper.setInChI(rccCatch.getInChI());
			mapper.setCommonName(rccCatch.getMainName());
		}
	}

	private static File createTempDirectory() throws IOException {
		final File temp = createTempFile("");
		if (!(temp.delete()))
			throw new IOException("Could not delete tmp file: " + temp.getAbsolutePath());
		if (!(temp.mkdir()))
			throw new IOException("Could not create tmp directory: " + temp.getAbsolutePath());
		return (temp);
	}

	private static File createTempFile(String ext) throws IOException {
		return File.createTempFile(BIH_FILES_PREFIX, Long.toString(System.nanoTime()) + ext);
	}

	private static void cleanOldFiles() throws IOException {

		long timeCut = 30 * 60 * 1000;
		long eligibleForDeletion = System.currentTimeMillis() - timeCut;
		String absolutePath = (createTempFile("")).getAbsolutePath();
		String tempFilePath = absolutePath.substring(0, absolutePath.lastIndexOf(File.separator));
		File path = new File(tempFilePath);

		// clean old files
		for (File fileToDelete : path.listFiles()) {
			if (fileToDelete.getName().startsWith(BIH_FILES_PREFIX)
					&& fileToDelete.lastModified() < eligibleForDeletion) {
				if (fileToDelete.isDirectory()) {
					// remove files in folder
					for (File subFileToDelete : fileToDelete.listFiles()) {
						subFileToDelete.delete();
					}
					// remove foled
					fileToDelete.delete();
				} else {
					// ZIP
					fileToDelete.delete();
				}
			}
		}
	}

}
