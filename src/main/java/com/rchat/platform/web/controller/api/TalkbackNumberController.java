package com.rchat.platform.web.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.rchat.platform.common.LogAPI;
import com.rchat.platform.domain.TalkbackNumber;
import com.rchat.platform.service.TalkbackNumberService;
import com.rchat.platform.web.exception.TalkbackUserNotFoundException;

@RestController
@RequestMapping("/talkback-numbers")
public class TalkbackNumberController {
	@Autowired
	private TalkbackNumberService talkbackUserNumberService;

	@PostMapping
	@LogAPI("创建对讲号码")
	public TalkbackNumber create(@RequestBody TalkbackNumber number) {
		return talkbackUserNumberService.create(number);
	}

	@DeleteMapping("/{id}")
	@LogAPI("删除对讲号码")
	@ResponseStatus(code = HttpStatus.OK, reason = "删除对讲号码")
	public void delete(@PathVariable String id) {
		assertNumberExists(id);

		talkbackUserNumberService.delete(id);
	}

	private void assertNumberExists(String id) {
		boolean exists = talkbackUserNumberService.exists(id);
		if (!exists)
			throw new TalkbackUserNotFoundException();
	}
}
