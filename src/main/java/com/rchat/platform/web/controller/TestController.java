package com.rchat.platform.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rchat.platform.service.TalkbackUserService;
import com.rchat.platform.service.redis.RedisService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value="测试模块")
@RestController
@RequestMapping(value = "/swagger")
public class TestController {
	@Autowired
    private TalkbackUserService talkbackUserService;
	@Autowired
    private RedisService redisService;

	@ApiOperation(value = "测试")
    @RequestMapping(value = "tel", method = RequestMethod.GET)
    public String add( @RequestParam("str") String str ) {
        return "添加成功";
    }
	@ApiOperation(value = "测试缓存")
    @RequestMapping(value = "ceshi", method = RequestMethod.GET)
    public String ceshi() {
		redisService.set("13324", "ceshi");
		Object obj=redisService.get("13324");
		System.out.println(obj);
        return "添加成功";
    }
	/**
	 * 修改该账号的默认群组
	 * @param talkBackGroupsId
	 * @param talkBackUserIds
	 */
	@ApiOperation(value = "修改该账号的默认群组")
    @RequestMapping(value = "/changeDefaultGruops", method = RequestMethod.GET)
	public void changeDefaultGruops( @RequestParam("talkBackGroupsId")String talkBackGroupsId, @RequestParam("talkBackUserIds")String talkBackUserIds) {
		boolean flag=talkbackUserService.changeDefaultGruops(talkBackGroupsId,talkBackUserIds);
		System.out.println(flag);
	}

}
