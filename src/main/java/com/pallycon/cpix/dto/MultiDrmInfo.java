package com.pallycon.cpix.dto;

public class MultiDrmInfo {
	private String trackType;
	private String key;
	private String keyId;
	private String iv;
	private String widevinePssh;
	private String widevinePsshPayload;
	private String playreadyPssh;
	private String playreadyPsshPayload;
	private String fairplayHlsSignalingData;
	private String fairplayHlsKeyUri;
	private String ncgCek;
	private String ncgHlsKeyUri;

	public String getTrackType() {
		return trackType;
	}

	public void setTrackType(String trackType) {
		this.trackType = trackType;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getKeyId() {
		return keyId;
	}

	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}

	public String getIv() {
		return iv;
	}

	public void setIv(String iv) {
		this.iv = iv;
	}

	public String getWidevinePssh() {
		return widevinePssh;
	}

	public void setWidevinePssh(String widevinePssh) {
		this.widevinePssh = widevinePssh;
	}

	public String getWidevinePsshPayload() {
		return widevinePsshPayload;
	}

	public void setWidevinePsshPayload(String widevinePsshPayload) {
		this.widevinePsshPayload = widevinePsshPayload;
	}

	public String getPlayreadyPssh() {
		return playreadyPssh;
	}

	public void setPlayreadyPssh(String playreadyPssh) {
		this.playreadyPssh = playreadyPssh;
	}

	public String getPlayreadyPsshPayload() {
		return playreadyPsshPayload;
	}

	public void setPlayreadyPsshPayload(String playreadyPsshPayload) {
		this.playreadyPsshPayload = playreadyPsshPayload;
	}

	public String getFairplayHlsSignalingData() {
		return fairplayHlsSignalingData;
	}

	public void setFairplayHlsSignalingData(String fairplayHlsSignalingData) {
		this.fairplayHlsSignalingData = fairplayHlsSignalingData;
	}

	public String getFairplayHlsKeyUri() {
		return fairplayHlsKeyUri;
	}

	public void setFairplayHlsKeyUri(String fairplayHlsKeyUri) {
		this.fairplayHlsKeyUri = fairplayHlsKeyUri;
	}

	public String getNcgCek() {
		return ncgCek;
	}

	public void setNcgCek(String ncgCek) {
		this.ncgCek = ncgCek;
	}

	public String getNcgHlsKeyUri() {
		return ncgHlsKeyUri;
	}

	public void setNcgHlsKeyUri(String ncgHlsKeyUri) {
		this.ncgHlsKeyUri = ncgHlsKeyUri;
	}
}
