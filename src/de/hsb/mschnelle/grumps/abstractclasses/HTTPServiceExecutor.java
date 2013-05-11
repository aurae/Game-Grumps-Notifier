package de.hsb.mschnelle.grumps.abstractclasses;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.client.ClientProtocolException;

/**
 * Subclass for executor methods that use HTTP connections to retrieve data.
 * @author Marcel
 *
 */
public abstract class HTTPServiceExecutor extends ServiceExecutor {

	/**
	 * Execute API request
	 * @param request
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String executeRequest(String request) throws ClientProtocolException, IOException {
		
		// Open connection
		URL url = new URL(request);
		HttpURLConnection urlConnection = (HttpURLConnection) url
				.openConnection();
		urlConnection.setConnectTimeout(20000);
		urlConnection.setReadTimeout(20000);
		
		// Retrieve the input stream
		BufferedReader in = new BufferedReader(new InputStreamReader(
				urlConnection.getInputStream()));

		// Read the input stream line by line until it's empty
		// (in this version, the xml is probably only one line, but w/e)
		String read = null;
		StringBuilder responseBuilder = new StringBuilder();
		do {
			read = in.readLine();
			if (read != null)
				responseBuilder.append(read);
		} while (read != null);
		
		// Build the response that was retrieved and return it
		String response = responseBuilder.toString();
		return response;
	}
}
