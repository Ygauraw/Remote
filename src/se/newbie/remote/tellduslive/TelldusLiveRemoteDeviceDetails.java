package se.newbie.remote.tellduslive;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.scribe.model.Token;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlSerializer;

import se.newbie.remote.device.RemoteDeviceDetails;
import android.util.Log;
import android.util.Xml;

public class TelldusLiveRemoteDeviceDetails implements RemoteDeviceDetails {
	private static final String TAG = "TelldusLiveRemoteDeviceDetails";

	private String identifier;
	private String name;	
	private String accessToken;
	private String accessSecret;

	public TelldusLiveRemoteDeviceDetails() {
		identifier = TelldusLiveRemoteDeviceDiscoverer.APPLICATION + "-" + UUID.randomUUID().toString();
	}

	public TelldusLiveRemoteDeviceDetails(String details) throws Exception {
		this.deserialize(details);
	}

	public void setAccessToken(String token, String secret) {
		this.accessToken = token;
		this.accessSecret = secret;
	}

	public void setAccessToken(Token accessToken) {
		this.accessToken = accessToken.getToken();
		this.accessSecret = accessToken.getSecret();
	}

	public Token getAccessToken() {
		Token token = null;
		if (accessToken != null && accessSecret != null
				&& accessToken.length() > 0) {
			token = new Token(accessToken, accessSecret);
		}
		return token;
	}

	public String serialize() {
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		try {
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			serializer.startTag("",
					TelldusLiveRemoteDeviceDiscoverer.APPLICATION);
			serializer.attribute("", "identifier", getIdentifier());
			serializer.attribute("", "name", (name != null) ? name : "");
			if (accessToken != null) {
				serializer.attribute("", "token", accessToken);
				serializer.attribute("", "secret", accessSecret);
			}
			serializer
					.endTag("", TelldusLiveRemoteDeviceDiscoverer.APPLICATION);
			serializer.endDocument();
		} catch (Exception e) {
			Log.e(TAG, "Not able to serialize:\n " + e.getMessage());
		}
		return writer.toString();
	}

	public void deserialize(String details) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		DocumentBuilder builder = factory.newDocumentBuilder();
		Document dom = builder
				.parse(new InputSource(new StringReader(details)));
		Element root = dom.getDocumentElement();

		if (!root.getNodeName().equals(
				TelldusLiveRemoteDeviceDiscoverer.APPLICATION)) {
			throw new NullPointerException();
		}
		accessToken = root.getAttribute("token");
		accessSecret = root.getAttribute("secret");
		name = root.getAttribute("name");
		identifier = root.getAttribute("identifier");		
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}	
}