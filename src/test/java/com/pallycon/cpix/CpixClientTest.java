package com.pallycon.cpix;

import com.pallycon.cpix.dto.ContentPackagingInfo;
import com.pallycon.cpix.dto.DrmType;
import com.pallycon.cpix.dto.EncryptionScheme;
import com.pallycon.cpix.dto.MultiDrmInfo;
import com.pallycon.cpix.dto.TrackType;
import com.pallycon.cpix.exception.CpixClientException;
import com.pallycon.cpix.util.StringUtil;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;


class CpixClientTest {

	private CpixClient cpixClient;

	@BeforeEach
	public void setUp() {
		// Initialize CpixClient with KMS URL and KMS token
		String kmsUrl = "https://kms.pallycon.com/v2/cpix/pallycon/getKey/";
		String encToken = "eyJhY2Nlc3Nfa2V5IjoiZHNJb2xjN2gxRzhUVW1JMTdiWXd4aFV1TkZvRmNlNzJjeDllTU9rNjJ3YjhWTjJQZGdwV1lISXhTRVg5ZjBIaSIsInNpdGVfaWQiOiJERU1PIn0=";
		cpixClient = new PallyConCpixClient(kmsUrl, encToken);
	}

	@Test
	@DisplayName("CPIX Response Output Test")
	public void testGetContentKeyInfoFromPallyconKMS()
		throws CpixClientException, JsonProcessingException {
		String contentId = "cpix-client-test-cid";
		EnumSet<DrmType> drmTypes = EnumSet.of(DrmType.WIDEVINE, DrmType.PLAYREADY,
			DrmType.FAIRPLAY);
		EncryptionScheme encryptionScheme = EncryptionScheme.CENC;
		EnumSet<TrackType> trackTypes = EnumSet.of(TrackType.HD, TrackType.SD, TrackType.AUDIO);

		ContentPackagingInfo packagingInfo = cpixClient.GetContentKeyInfoFromPallyConKMS(contentId,
			drmTypes, encryptionScheme, trackTypes);

		Assertions.assertNotNull(packagingInfo);
		Assertions.assertEquals(contentId, packagingInfo.getContentId());
		Assertions.assertFalse(packagingInfo.getMultiDrmInfos().isEmpty());

		for (MultiDrmInfo drmInfo : packagingInfo.getMultiDrmInfos()) {
			Assertions.assertNotNull(drmInfo.getKeyId());
			Assertions.assertNotNull(drmInfo.getKey());
		}

		// Create and output JSON-formatted data for better readability.
		String jsonPackInfo = makeSampleJsonFromData(packagingInfo);
		System.out.println(jsonPackInfo);

		// Create to file
		String outputFilePath = "./" + contentId + ".json";
		try (FileWriter fileWriter = new FileWriter(outputFilePath)) {
			fileWriter.write(jsonPackInfo);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String makeSampleJsonFromData(ContentPackagingInfo packInfo)
		throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, false);
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

		// Using a LinkedHashMap for guaranteed ordering
		Map<String, Object> jsonPackInfo = new LinkedHashMap<>();
		jsonPackInfo.put("content_id", packInfo.getContentId());

		List<Map<String, Object>> contentKeyList = new ArrayList<>();
		for (MultiDrmInfo multiDrmInfo : packInfo.getMultiDrmInfos()) {
			Map<String, Object> strMultidrmInfo = new LinkedHashMap<>();

			if (packInfo.getMultiDrmInfos().size() > 1) {
				strMultidrmInfo.put("track_type", multiDrmInfo.getTrackType());
			}
			strMultidrmInfo.put("key_id_hex", multiDrmInfo.getKeyId().replace("-", ""));
			strMultidrmInfo.put("key_id_b64", Base64.getEncoder().encodeToString(
				StringUtil.hexStringToByteArray(multiDrmInfo.getKeyId().replace("-", ""))));
			strMultidrmInfo.put("key_hex",
				StringUtil.byteArrayToHexString(Base64.getDecoder().decode(multiDrmInfo.getKey())));
			strMultidrmInfo.put("key_b64", multiDrmInfo.getKey());
			strMultidrmInfo.put("iv_hex",
				StringUtil.byteArrayToHexString(Base64.getDecoder().decode(multiDrmInfo.getIv())));
			strMultidrmInfo.put("iv_b64", multiDrmInfo.getIv());

			Map<String, Object> widevineObj = new LinkedHashMap<>();
			widevineObj.put("pssh", multiDrmInfo.getWidevinePssh());
			widevineObj.put("pssh_payload_only", multiDrmInfo.getWidevinePsshPayload());
			strMultidrmInfo.put("widevine", widevineObj);

			Map<String, Object> playreadyObj = new LinkedHashMap<>();
			playreadyObj.put("pssh", multiDrmInfo.getPlayreadyPssh());
			playreadyObj.put("pssh_payload_only", multiDrmInfo.getPlayreadyPsshPayload());
			strMultidrmInfo.put("playready", playreadyObj);

			Map<String, Object> fairplayObj = new LinkedHashMap<>();
			fairplayObj.put("key_uri", multiDrmInfo.getFairplayHlsKeyUri());
			strMultidrmInfo.put("fairplay", fairplayObj);

			contentKeyList.add(strMultidrmInfo);
		}

		jsonPackInfo.put("content_key_list", contentKeyList);

		return objectMapper.writeValueAsString(jsonPackInfo);
	}
}