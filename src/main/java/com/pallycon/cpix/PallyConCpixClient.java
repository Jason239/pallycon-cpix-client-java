package com.pallycon.cpix;

import com.pallycon.cpix.dto.ContentPackagingInfo;
import com.pallycon.cpix.dto.DrmType;
import com.pallycon.cpix.dto.EncryptionScheme;
import com.pallycon.cpix.dto.MultiDrmInfo;
import com.pallycon.cpix.dto.TrackType;
import com.pallycon.cpix.exception.CpixClientException;
import com.pallycon.cpix.util.StringUtil;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class PallyConCpixClient implements CpixClient{
	private static final String WIDEVINE_SYSTEM_ID = "EDEF8BA9-79D6-4ACE-A3C8-27DCD51D21ED";
	private static final String PLAYREADY_SYSTEM_ID = "9A04F079-9840-4286-AB92-E65BE0885F95";
	private static final String FAIRPLAY_SYSTEM_ID = "94CE86FB-07FF-4F43-ADB8-93D2FA968CA2";
	private static final String NCG_SYSTEM_ID = "D9E4411A-E886-4909-A380-A77F28D52335";
	private static final String HLS_NCG_SYSTEM_ID = "48582A1D-1FF4-426E-8CD5-06424FCC578C";

	private String kmsUrl;
	private String encToken;

	public PallyConCpixClient(String kmsUrl, String encToken) {
		this.kmsUrl = kmsUrl;
		this.encToken = encToken;
	}
	@Override
	public ContentPackagingInfo GetContentKeyInfoFromPallyConKMS(String contentId,
		EnumSet<DrmType> drmTypes, EncryptionScheme encryptionScheme,
		EnumSet<TrackType> trackTypes) throws CpixClientException{

		Map<TrackType, String> keyMap = buildKeyMap(trackTypes);

		String requestXml = buildRequestXml(contentId, keyMap, drmTypes, encryptionScheme);
		String responseXml = makeHttpRequest(kmsUrl + encToken, requestXml);

		if (responseXml != null) {
			return parseResponse(responseXml);
		} else {
			throw new CpixClientException("Error occurred while getting content key info.");
		}
	}
	private Map<TrackType, String> buildKeyMap(EnumSet<TrackType> trackTypes) {
		Map<TrackType, String> keyMap = new HashMap<>();
		if (trackTypes.contains(TrackType.ALL_TRACKS)) {
			// Set the track type to "HD" for single-key
			keyMap.put(TrackType.HD, UUID.randomUUID().toString());
		} else {
			if (trackTypes.contains(TrackType.AUDIO)) {
				keyMap.put(TrackType.AUDIO, UUID.randomUUID().toString());
			}
			if (trackTypes.contains(TrackType.SD)) {
				keyMap.put(TrackType.SD, UUID.randomUUID().toString());
			}
			if (trackTypes.contains(TrackType.HD)) {
				keyMap.put(TrackType.HD, UUID.randomUUID().toString());
			}
			if (trackTypes.contains(TrackType.UHD1)) {
				keyMap.put(TrackType.UHD1, UUID.randomUUID().toString());
			}
			if (trackTypes.contains(TrackType.UHD2)) {
				keyMap.put(TrackType.UHD2, UUID.randomUUID().toString());
			}
		}
		return keyMap;
	}

	private String buildRequestXml(String contentId, Map<TrackType, String> keyMap,
		EnumSet<DrmType> drmTypes, EncryptionScheme encryptionScheme) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();

			Element reqRoot = doc.createElement("cpix:CPIX");
			reqRoot.setAttribute("id", contentId);
			reqRoot.setAttribute("xmlns:cpix", "urn:dashif:org:cpix");
			reqRoot.setAttribute("xmlns:pskc", "urn:ietf:params:xml:ns:keyprov:pskc");
			reqRoot.setAttribute("xmlns:speke", "urn:aws:amazon:com:speke");

			Element reqContentKeyList = doc.createElement("cpix:ContentKeyList");
			Element reqContentKeyUsageRuleList = doc.createElement("cpix:ContentKeyUsageRuleList");
			Element reqDrmSystemList = doc.createElement("cpix:DRMSystemList");

			for (TrackType track : keyMap.keySet()) {
				Element reqContentKey = doc.createElement("cpix:ContentKey");
				reqContentKey.setAttribute("kid", keyMap.get(track));
				reqContentKey.setAttribute("commonEncryptionScheme", encryptionScheme.name().toLowerCase());
				reqContentKeyList.appendChild(reqContentKey);

				Element reqContentKeyUsageRule = doc.createElement("cpix:ContentKeyUsageRule");
				reqContentKeyUsageRule.setAttribute("intendedTrackType", track.name());
				reqContentKeyUsageRule.setAttribute("kid", keyMap.get(track));
				reqContentKeyUsageRuleList.appendChild(reqContentKeyUsageRule);

				for (DrmType drmType : drmTypes) {
					String systemId = null;
					switch (drmType) {
						case WIDEVINE:
							systemId = WIDEVINE_SYSTEM_ID;
							break;
						case PLAYREADY:
							systemId = PLAYREADY_SYSTEM_ID;
							break;
						case FAIRPLAY:
							systemId = FAIRPLAY_SYSTEM_ID;
							break;
						case NCG:
							systemId = NCG_SYSTEM_ID;
							break;
						case HLS_NCG:
							systemId = HLS_NCG_SYSTEM_ID;
							break;
					}

					if (systemId != null) {
						Element reqDrmSystem = doc.createElement("cpix:DRMSystem");
						reqDrmSystem.setAttribute("kid", keyMap.get(track));
						reqDrmSystem.setAttribute("systemId", systemId);
						reqDrmSystemList.appendChild(reqDrmSystem);
					}
				}
			}

			reqRoot.appendChild(reqContentKeyList);
			reqRoot.appendChild(reqContentKeyUsageRuleList);
			reqRoot.appendChild(reqDrmSystemList);
			doc.appendChild(reqRoot);

			return convertDocumentToString(doc);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	private String convertDocumentToString(Document doc) {
		try {
			DOMSource domSource = new DOMSource(doc);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.transform(domSource, result);
			return writer.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	private String makeHttpRequest(String url, String requestData) throws CpixClientException{
		try {
			URL urlObj = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/xml");
			conn.setDoOutput(true);

			// Send the request data
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(requestData);
			wr.flush();

			// Read the response
			StringBuilder responseBuilder = new StringBuilder();
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					responseBuilder.append(inputLine);
				}
				in.close();
			} else {
				throw new CpixClientException("HTTP Request Error: " + conn.getResponseCode() + " - " + conn.getResponseMessage());
			}

			conn.disconnect();

			return responseBuilder.toString();
		} catch (Exception ex) {
			throw new CpixClientException("Error occurred during HTTP request.", ex);
		}
	}

	private ContentPackagingInfo parseResponse(String responseXml) throws CpixClientException{
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(responseXml));
			Document doc = docBuilder.parse(is);

			List<MultiDrmInfo> multiDrmInfos = new ArrayList<>();
			NodeList contentKeyUsageRuleList = doc.getElementsByTagName("cpix:ContentKeyUsageRule");
			NodeList contentKeyList = doc.getElementsByTagName("cpix:ContentKey");
			NodeList drmSystemList = doc.getElementsByTagName("cpix:DRMSystem");

			for (int i = 0; i < contentKeyUsageRuleList.getLength(); i++) {
				Element contentKeyUsageRule = (Element) contentKeyUsageRuleList.item(i);
				String trackType = contentKeyUsageRule.getAttribute("intendedTrackType");
				String keyId = contentKeyUsageRule.getAttribute("kid");
				MultiDrmInfo drmInfo = new MultiDrmInfo();
				drmInfo.setTrackType(trackType);
				drmInfo.setKeyId(keyId);
				multiDrmInfos.add(drmInfo);
			}

			for (int i = 0; i < contentKeyList.getLength(); i++) {
				Element contentKey = (Element) contentKeyList.item(i);
				String keyId = contentKey.getAttribute("kid");
				String explicitIV = contentKey.getAttribute("explicitIV");
				String keyValue = contentKey.getElementsByTagName("pskc:PlainValue").item(0).getTextContent();

				for (MultiDrmInfo drmInfo : multiDrmInfos) {
					if (drmInfo.getKeyId().equals(keyId)) {
						drmInfo.setIv(explicitIV);
						drmInfo.setKey(keyValue);
						break;
					}
				}
			}

			for (int i = 0; i < drmSystemList.getLength(); i++) {
				Element drmSystem = (Element) drmSystemList.item(i);
				String keyId = drmSystem.getAttribute("kid");
				String systemId = drmSystem.getAttribute("systemId");

				for (MultiDrmInfo drmInfo : multiDrmInfos) {
					if (drmInfo.getKeyId().equals(keyId)) {
						switch (systemId) {
							case WIDEVINE_SYSTEM_ID:
								String psshWidevine = drmSystem.getElementsByTagName("cpix:PSSH").item(0).getTextContent();
								String contentProtectionDataWidevine = drmSystem.getElementsByTagName("cpix:ContentProtectionData").item(0).getTextContent();
								drmInfo.setWidevinePssh(psshWidevine);
								drmInfo.setWidevinePsshPayload(contentProtectionDataWidevine);
								break;
							case PLAYREADY_SYSTEM_ID:
								String psshPlayReady = drmSystem.getElementsByTagName("cpix:PSSH").item(0).getTextContent();
								String contentProtectionDataPlayReady = drmSystem.getElementsByTagName("cpix:ContentProtectionData").item(0).getTextContent();
								drmInfo.setPlayreadyPssh(psshPlayReady);
								drmInfo.setPlayreadyPsshPayload(contentProtectionDataPlayReady);
								break;
							case FAIRPLAY_SYSTEM_ID:
								Element uriExtXKeyElement = (Element) drmSystem.getElementsByTagName("cpix:URIExtXKey").item(0);
								String fairplayHlsKeyUri = uriExtXKeyElement.getTextContent();
								drmInfo.setFairplayHlsKeyUri(StringUtil.decodeBase64(fairplayHlsKeyUri));

								Element hlsSignalingDataElement = (Element) drmSystem.getElementsByTagName("cpix:HLSSignalingData").item(0);
								String fairplayHlsSignalingData = hlsSignalingDataElement.getTextContent();
								drmInfo.setFairplayHlsSignalingData(StringUtil.decodeBase64(fairplayHlsSignalingData));
								break;
							case NCG_SYSTEM_ID:
								Element uriExtXKeyElementNCG = (Element) drmSystem.getElementsByTagName("cpix:URIExtXKey").item(0);
								String ncgCek = uriExtXKeyElementNCG.getTextContent();
								drmInfo.setNcgCek(ncgCek);
								break;
							case HLS_NCG_SYSTEM_ID:
								Element uriExtXKeyElementHLSNCG = (Element) drmSystem.getElementsByTagName("cpix:URIExtXKey").item(0);
								String ncgHlsKeyUri = uriExtXKeyElementHLSNCG.getTextContent();
								drmInfo.setNcgHlsKeyUri(StringUtil.decodeBase64(ncgHlsKeyUri));
								break;
						}
						break;
					}
				}
			}

			Element cpixElement = doc.getDocumentElement();
			String contentId = cpixElement.getAttribute("id");
			ContentPackagingInfo packInfo = new ContentPackagingInfo();
			packInfo.setContentId(contentId);
			packInfo.setMultiDrmInfos(multiDrmInfos);

			return packInfo;
		} catch (Exception ex) {
			throw new CpixClientException("Error occurred during parsing response XML.", ex);
		}
	}
}
