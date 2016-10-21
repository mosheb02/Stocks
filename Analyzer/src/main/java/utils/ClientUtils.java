package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import DAO.StocksDAO;

public class ClientUtils {
	final static private int NO_OF_QUERY_RETRIES = 3;

	public static String getJsonResponse(final StocksDAO stocksDAO, final String errorDB, final String errorCollection,
			final URL url) throws IOException {

		final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		int cuurentAttempt = 0;
		try {
			while (cuurentAttempt < NO_OF_QUERY_RETRIES) {
				final String responseFromConnection = getResponseFromConnection(conn);
				if (responseFromConnection != null) {
					return responseFromConnection;
				}
				cuurentAttempt++;
			}

			stocksDAO.insert(errorDB, errorCollection, "{\"errorUrl\":\""+url.toString()+"\"}");
			return null;
		} finally {
			conn.disconnect();
		}

	}

	private static String getResponseFromConnection(final HttpURLConnection conn) {
		try
		{
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");

		if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
			return null;
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader((conn.getInputStream())));

		StringBuilder stringBuilder = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
		}
		return stringBuilder.toString();
		}
		catch (Exception e)
		{
			return null;
		}
	}
}
