package com.pallycon.cpix;

import com.pallycon.cpix.dto.ContentPackagingInfo;
import com.pallycon.cpix.dto.DrmType;
import com.pallycon.cpix.dto.EncryptionScheme;
import com.pallycon.cpix.dto.TrackType;
import com.pallycon.cpix.exception.CpixClientException;
import java.util.EnumSet;

public interface CpixClient {
	ContentPackagingInfo GetContentKeyInfoFromPallyConKMS(String contentId,
		EnumSet<DrmType> drmTypes, EncryptionScheme encryptionScheme,
		EnumSet<TrackType> trackTypes) throws CpixClientException;
}
