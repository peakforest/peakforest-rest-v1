/**
 * 
 */
package fr.metabohub.peakforest.ws;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import fr.metabohub.peakforest.dao.spectrum.NMR2DPeakDao;
import fr.metabohub.peakforest.dao.spectrum.NMR2DSpectrumDao;
import fr.metabohub.peakforest.model.spectrum.NMR2DPeak;
import fr.metabohub.peakforest.model.spectrum.NMR2DSpectrum;
import fr.metabohub.peakforest.security.model.User;
import fr.metabohub.peakforest.utils.AHibernateUtilsTests;
import fr.metabohub.peakforest.utils.PeakForestApiHibernateUtils;

/**
 * Test class for {@link NMR2DSpectrumImpl} service methods
 * 
 * @author Carlos Cepeda
 * 
 */
public class NMR2DSpectrumImplTest extends AHibernateUtilsTests {

	/**
	 * Tests if a REST API search for a NMR2DSpectrum returns a non-empty peak list
	 * 
	 * @throws Exception
	 */
	@Test
	public void peakListNotEmpty() throws Exception {

		boolean success = false;

		// add user who can use REST API with a generated token
		String token = null;

		try (final Session session = PeakForestApiHibernateUtils.getSessionFactory().openSession()) {

			// generate a unique token (that does not exist in test db)
			String queryString = "SELECT * FROM users WHERE token = :token LIMIT 1 ;";
			NativeQuery<?> query = session.createSQLQuery(queryString);
			int triesCpt = 0, triesMax = 3;
			while (!success && triesCpt < triesMax) {
				token = new BigInteger(130, new SecureRandom()).toString(32);
				query.setParameter("token", token);
				Object result = query.uniqueResult();
				success = (result == null);
				System.out.println("[junit test] generate unique token (try #" + (++triesCpt) + ") =" + success);
				System.out.println("[junit test] generate unique token (try #" + (triesCpt) + ") =" + success);
			}

			Assert.assertTrue("[fail] could not generate a unique token", success);

			success = false;

			// add user (native query is simpler than a DAO copy)
//			queryString = "INSERT INTO users (confirmed, token, admin, curator, email, login, password) VALUES ('1', :token, '0', '0', :mail, :username, '');";
//			query = session.createSQLQuery(queryString);
//			query.setParameter("token", token);
//			query.setParameter("username", "user_api_test_" + token);
//			query.setParameter("mail", "mail_api_test_" + token);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("check dummy token " + e.getMessage());
		}

		PeakForestApiHibernateUtils.restart();

		// insert user
		insertUser(token);

		// add data which will be returned by the tested method
		NMR2DSpectrum spectrum = null;

		spectrum = new NMR2DSpectrum();
		// list of peaks that must be returned
		final long count1 = NMR2DPeakDao.count();
		final List<NMR2DPeak> peakList = new ArrayList<NMR2DPeak>();
		peakList.add(new NMR2DPeak(spectrum, 13.503, 0.898, 5467647.42));
		peakList.add(new NMR2DPeak(spectrum, 16.653, 1.103, 444425.39));
		peakList.add(new NMR2DPeak(spectrum, 27.337, 1.463, 1059010.48));
		peakList.add(new NMR2DPeak(spectrum, 27.36, 1.704, 1104749.03));
		peakList.add(new NMR2DPeak(spectrum, 46.628, 2.938, 111449.86));
		NMR2DPeakDao.create(peakList);
		final long count2 = NMR2DPeakDao.count();

		Assert.assertEquals("[fail] could not add peaks for test", 5, count2 - count1);
		success = false;

		// get results, request is equivalent to following URL path :
		// "/spectra/nmr2d/search-naive/13.4/0.2/0.8/0.2"

		final String response = (new NMR2DSpectrumImpl()).getNMRspectra("13.4", 0.2, "0.8", 0.2, null, null, null,
				token);

		JSONArray results = new JSONArray(response);

		// check if results are not empty and the peaks are retrieved (only on the first
		// result, and without verifying the values)
		JSONObject currentResult;
		JSONArray peaks;
		JSONObject currentPeak;
		int peaksLength;
		if (!results.isEmpty()) {
			currentResult = results.getJSONObject(0);
			peaks = currentResult.getJSONArray("peaks");
			if (!peaks.isEmpty()) {
				peaksLength = peaks.length();
				for (int peaksCpt = 0; peaksCpt < peaksLength; peaksCpt++) {
					currentPeak = peaks.getJSONObject(peaksCpt);
					if (!currentPeak.has("ppm_f1") || !currentPeak.has("ppm_f2") || !currentPeak.has("i")
							|| !currentPeak.has("annotation"))
						break;
				}
				success = true;
			}
		}
		System.out.println("[junit test] peaks list retrieved and not empty =" + success);

		Assert.assertTrue("[fail] could not get peak list", success);

		// delete previously-added data
		if (spectrum != null) {
			NMR2DSpectrumDao.delete(spectrum.getId());
			success = true;
			System.out.println("[junit test] clear peaks result =" + success);
		}

	}

	private void insertUser(final String token) {
		try (final Session session = PeakForestApiHibernateUtils.getSessionFactory().openSession()) {
			// create dummy user
			final User u = new User();
			u.setToken(token);
			u.setConfirmed(true);
			u.setEmail("mail_api_test_" + token);
			u.setLogin("user_api_test_" + token);
			u.setPassword("passwd_api_test_" + token);

			// insert user in db
			Transaction t = session.beginTransaction();
			final long idUser = (Long) session.save(u);
			t.commit();
			session.close();
//			int result = query.executeUpdate();
			Assert.assertTrue("[fail] could not add user for test", idUser > 0);
			System.out.println("[junit test] add user result =" + idUser);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("could not add dummy user " + e.getMessage());
		}
	}

}
