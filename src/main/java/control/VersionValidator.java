package control;

import java.awt.*;
import java.io.*;
import java.net.*;

import javax.swing.*;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.apache.http.util.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

@Slf4j
public class VersionValidator {
	private final int YES = 0;
	private final String VERSION = "v1.2.2";
	private String LATEST_VERSION;

	public boolean validate() {

		CloseableHttpClient client = HttpClientBuilder.create().build();
		final HttpGet request = new HttpGet(
				"https://github.com/DeOldSax/iliasDownloaderTool/releases/latest");
		HttpResponse response = null;
		try {
			response = client.execute(request);
			String content = EntityUtils.toString(response.getEntity());
			Document doc = Jsoup.parse(content);
			final Elements select = doc.select("span");
			for (Element element : select) {
				if (element.attr("class").equals("css-truncate-target")) {
					LATEST_VERSION = element.text();
					if (!LATEST_VERSION.startsWith("v")) {
						LATEST_VERSION = VERSION;
					}
				}
			}

		} catch (IOException e) {
			log.warn("HttpConnection failed --> no version check", e);
			return false;
		}
		request.releaseConnection();

		if (VERSION.equals(LATEST_VERSION)) {
			return false;
		}

		int answer = JOptionPane.showOptionDialog(null, "    Version Herunterladen?",
				"Neue Version Verfügbar!", JOptionPane.YES_NO_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, new Object[] { "Ja", "Später" }, null);
		if (answer == YES) {
			if (Desktop.isDesktopSupported()) {
				try {
					Desktop.getDesktop().browse(new URI("www.iliasdownloadertool.de"));
				} catch (IOException | URISyntaxException e) {
					log.warn(e.getMessage(), e);
				}
			} else {
				JOptionPane.showMessageDialog(null, "www.iliasdownloadertool.de", "follow link",
						JOptionPane.INFORMATION_MESSAGE);
			}
			return true;
		}
		return false;
	}

	public String getVersion() {
		return VERSION;
	}
}
