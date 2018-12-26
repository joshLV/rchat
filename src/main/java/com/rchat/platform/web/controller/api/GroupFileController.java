package com.rchat.platform.web.controller.api;

import com.rchat.platform.common.LogAPI;
import com.rchat.platform.common.RchatEnv;
import com.rchat.platform.common.RchatUtils;
import com.rchat.platform.domain.*;
import com.rchat.platform.exception.NoRightAccessException;
import com.rchat.platform.jms.TopicNameConstants;
import com.rchat.platform.service.*;
import com.rchat.platform.web.exception.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

@RestController
@RequestMapping("/api/groupFiles")
public class GroupFileController extends MessageListenerAdapter{
    @Autowired
    private GroupService groupService;
    @Autowired
    private GroupFileService groupFileService;
    @Autowired
    private RchatEnv env;
    @Autowired
    private JmsTemplate jms;

    @GetMapping
    public Page<GroupFile> list(@PageableDefault Pageable pageable) {
        Page<GroupFile> groups;
        if (RchatUtils.isGroupAdmin()) {
            throw new NoRightAccessException();
        } else if (RchatUtils.isAgentAdmin()) {
        	throw new NoRightAccessException();
        } else {
            groups = groupFileService.findAll(pageable);
        }
        return groups;
    }

    @PutMapping("/{id}")
    @LogAPI("更新文件")
    public GroupFile update(@PathVariable String id, @RequestBody GroupFile groupFile) {
        Optional<GroupFile> o = groupFileService.findOne(id);
        if(o.isPresent()){       	
        	groupFile.setId(id);
        	groupFile = groupFileService.update(groupFile);
        	return groupFile;
        } else {
        	return null;
        }
    }

    @DeleteMapping("/{id}")
    @LogAPI("删除文件")
    @ResponseStatus(code = HttpStatus.OK, reason = "删除文件成功")
    public void delete(@PathVariable String id) {
        Optional<GroupFile> o = groupFileService.findOne(id);

        GroupFile group = o.orElseThrow(GroupNotFoundException::new);
        groupFileService.delete(group);

        jms.convertAndSend(TopicNameConstants.GROUP_FILE_DELETE, group);
    }

    @GetMapping("/{id}")
    public GroupFile one(@PathVariable String id) {
        Optional<GroupFile> o = groupFileService.findOne(id);
        return o.orElseThrow(GroupNotFoundException::new);
    }
    
    @GetMapping("/group/{id}")
    public HashMap<String, Object> detailOne(@PathVariable String id) {
        Optional<Group> o = groupService.findOne(id);
        Group group = o.orElseThrow(GroupNotFoundException::new);
        HashMap<String, Object> map = new HashMap<>();
        List<GroupFile> fileList = groupFileService.findByGroupAndType(group, FileType.File);
        int totalFile = getTotalSize(fileList);
        List<GroupFile> voiceList = groupFileService.findByGroupAndType(group, FileType.Voice);
        int totalVoice = getTotalSize(voiceList);
        List<GroupFile> intercomList = groupFileService.findByGroupAndType(group, FileType.Intercom);
        int totalIntercom = getTotalSize(intercomList);
        List<GroupFile> vedioList = groupFileService.findByGroupAndType(group, FileType.Vedio);
        int totalVedio = getTotalSize(vedioList);
        map.put("fileList", fileList);
        map.put("voiceList", voiceList);
        map.put("intercomList", intercomList);
        map.put("vedioList", vedioList);
        map.put("totalFile", totalFile);
        map.put("totalVoice", totalVoice);
        map.put("totalIntercom", totalIntercom);
        map.put("totalVedio", totalVedio);
        return map;
    }

	private int getTotalSize(List<GroupFile> fileList) {
		int totalSize = 0;
        if(null != fileList && fileList.size() > 0){
        	for (int i = 0, size = fileList.size(); i < size; i++) {
        		totalSize += fileList.get(i).getSize();
			}
        }
        return totalSize;
	}

    
    //监听注解
    @JmsListener(destination = "rchat.topic.group-file.changeOne")
	public void onMessageheheOne(Message message, Session session) throws JMSException, JSONException {
		try {
			updateGroupFile(message);
		} catch (MessageConversionException | JMSException e) {
			e.printStackTrace();
		}
	}
    
    //监听注解
    @JmsListener(destination = "rchat.topic.group-file.changeTwo")
	public void onMessageheheTwo(Message message, Session session) throws JMSException, JSONException {
		try {
			updateGroupFile(message);
		} catch (MessageConversionException | JMSException e) {
			e.printStackTrace();
		}
	}
    
    //监听注解
    @JmsListener(destination = "rchat.topic.group-file.changeThree")
	public void onMessageheheThree(Message message, Session session) throws JMSException, JSONException {
		try {
			updateGroupFile(message);
		} catch (MessageConversionException | JMSException e) {
			e.printStackTrace();
		}
	}
    
    //监听注解
    @JmsListener(destination = "rchat.topic.group-file.changeFour")
    public void onMessageheheFour(Message message, Session session) throws JMSException, JSONException {
    	try {
    		updateGroupFile(message);
    	} catch (MessageConversionException | JMSException e) {
    		e.printStackTrace();
    	}
    }
    
    //监听注解
    @JmsListener(destination = "rchat.topic.group-file.changeFive")
    public void onMessageheheFive(Message message, Session session) throws JMSException, JSONException {
    	try {
    		updateGroupFile(message);
    	} catch (MessageConversionException | JMSException e) {
    		e.printStackTrace();
    	}
    }

	private void updateGroupFile(Message message) throws JMSException, JSONException {
		String str = (String)getMessageConverter().fromMessage(message);
		JSONObject json=new JSONObject(str);
		Map<String,Object> map=new HashMap<String, Object>();
		Iterator it = json.keys();
		while (it.hasNext()) {  
		   String key = (String) it.next();  
		   Object value = json.get(key);  
		   map.put(key, value);
		}
		String fileId = map.get("fileId")!=null?map.get("fileId").toString() : "";
		String name = map.get("name")!=null?map.get("name").toString() : "";
		String url = map.get("url")!=null?map.get("url").toString() : "";
		String groupId = map.get("groupId")!=null?map.get("groupId").toString() : "";
		String type = map.get("type")!=null?map.get("type").toString() : "0";
		String size = map.get("size")!=null?map.get("size").toString() : "0";
		Optional<Group> group = groupService.findOne(groupId);
		if(group.isPresent()){
			List<GroupFile> groupFile = groupFileService.findByFileId(fileId);
			if(null != groupFile && groupFile.size() > 0){
				GroupFile gf = groupFile.get(0);
				gf.setName(name);
				gf.setSize(Integer.parseInt(size));
				gf.setUrl(url);
				groupFileService.update(gf);
			} else {
				GroupFile gf = new GroupFile();
				gf.setFileId(fileId);
				gf.setGroup(group.get());
				gf.setName(name);
				gf.setSize(Integer.parseInt(size));
				gf.setType(FileType.File);
				gf.setUrl(url);
				groupFileService.create(gf);
			}
		}
	}
}
