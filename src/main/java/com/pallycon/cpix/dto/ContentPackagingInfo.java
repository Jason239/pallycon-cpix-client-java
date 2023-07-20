package com.pallycon.cpix.dto;

import java.util.List;

public class ContentPackagingInfo {
	private String contentId;
	private List<MultiDrmInfo> multiDrmInfos;

	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public List<MultiDrmInfo> getMultiDrmInfos() {
		return multiDrmInfos;
	}

	public void setMultiDrmInfos(List<MultiDrmInfo> multiDrmInfos) {
		this.multiDrmInfos = multiDrmInfos;
	}
}
