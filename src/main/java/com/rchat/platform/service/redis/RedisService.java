package com.rchat.platform.service.redis;

public interface RedisService {

    /**
    * Description:指定缓存失效时间 
    * @param    key 键 
    * @param    time 时间(秒) 
    * @return   boolean
    */
    public boolean expire(String key,long time);

    /**
    * Description:根据key 获取过期时间 
    * @param    key 键 不能为null 
    * @return   时间(秒) 返回0代表为永久有效 
    */
    public long getExpire(String key);

    /**
    * Description:判断key是否存在 
    * @param    key 键 
    * @return   true 存在 false不存在 
    */
    public boolean hasKey(String key);


    /**
    * Description:普通缓存放入 
    * @param    key 键 
    * @param    value 值 
    * @return   true成功 false失败 
    */
    public void set(String key, Object value);

    /**
    * Description:普通缓存获取 
    * @param    key 键
    * @return   值 
    */
    public Object get(String key); 

    /**
    * Description:删除缓存 
    * @param    key 可以传一个值 或多个 
    * @return
    */
    public void del(String ... key);
    /**
     * 缓存时间到自动删除缓存
     * @param key
     * @param value
     * @param time
     * @return
     */
    public boolean set(String key, Object value, long time);
}
