package com.rchat.platform.service.impl.sdk;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rchat.platform.domain.sdk.UidInfo;
import com.rchat.platform.domain.sdk.UidInfoRepository;
import com.rchat.platform.service.sdk.UidInfoService;
@Service
public class UidInfoServiceImpl implements UidInfoService {
	@Autowired
	private UidInfoRepository uidInfoRepository;

	@Override
	public List<UidInfo> getbByUid(String uid) {
		return uidInfoRepository.getbByUid(uid);
	}

}
