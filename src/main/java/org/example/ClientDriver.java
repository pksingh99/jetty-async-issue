package org.example;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * @author Arjen Poutsma
 */
public class ClientDriver {

	private static final int CHUNK_LENGTH = 10;

	private static final int DELAY = 100;

	private static final String REQUEST_BODY =
			"string, string, string, string, string, string, string, string, string, string, end.";

	private final URL url;

	public ClientDriver(URL url) {
		this.url = url;
	}

	public void execute() throws Exception {
		HttpURLConnection connection = null;
		try {
			connection = createHttpURLConnection(url);
			connection.connect();

			byte[] bytes = REQUEST_BODY.getBytes(StandardCharsets.UTF_8);
			sendRequest(connection, bytes);

			StringBuilder result = new StringBuilder();
			readResponse(connection, result);
		}
		finally {
			if (connection != null) {
				connection.disconnect();
			}
		}

	}

	private HttpURLConnection createHttpURLConnection(URL url) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setChunkedStreamingMode(CHUNK_LENGTH);
		return connection;
	}

	private void sendRequest(HttpURLConnection connection, byte[] bytes)
			throws IOException, InterruptedException {
		try (OutputStream os = connection.getOutputStream()) {
			int offset = 0;
			while (offset < bytes.length) {
				int length = Math.min(bytes.length - offset, CHUNK_LENGTH);
				os.write(bytes, offset, length);
				os.flush();
				offset += length;
				Thread.sleep(DELAY);
			}
		}
	}

	private int readResponse(HttpURLConnection connection, StringBuilder result)
			throws Exception {

		return connection.getResponseCode();
	}

	public static void main(String[] args) throws Exception {

		ClientDriver driver = new ClientDriver(new URL("http://localhost:8080/"));
		driver.execute();

	}

}
