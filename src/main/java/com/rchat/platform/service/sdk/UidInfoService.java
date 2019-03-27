package com.rchat.platform.service.sdk;

import java.util.List;

import com.rchat.platform.domain.sdk.UidInfo;

public interface UidInfoService {

	List<UidInfo> getbByUid(String uid);

}
