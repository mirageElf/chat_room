package com.soul.coco.common.utils;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisUtil {

    public final static String LOANUSER = "REDISUTIL+LOANUSER+USER";
    public final static Integer EXPIRE = 3600 * 24 * 7; // 过期时间 S， 7天

    @Autowired
	private StringRedisTemplate redisTemplate;
    /**
     * 保存对象到Redis 对象不过期
     *
     * @param key    待缓存的key
     * @param object 待缓存的对象
     * @return 返回是否缓存成功
     */
    public boolean setObject(String key, Object object) throws Exception {
        return setObject(key, object, -1);
    }

    /**
     * 保存对象到Redis 并设置超时时间
     *
     * @param key     缓存key
     * @param object  缓存对象
     * @param timeout 超时时间
     * @return 返回是否缓存成功
     * @throws Exception 异常上抛
     */
    public boolean setObject(String key, Object object, int timeout) throws Exception {
        String value = SerializeUtil.serialize(object);
        boolean result = false;
        try {
            //为-1时不设置超时时间
            if (timeout != -1) {
                setString(key,value,timeout);
            } else {
                setString(key,value);
            }
            result = false;
        } catch (Exception e) {
            throw e;
        }
        return  result;
    }

    /**
     * 缓存用户信息(7天过期)
     * 缓存借款用户信息
     * @param token 用户登录唯一密钥
     * @param object 待缓存的对象
     * @return 返回是否缓存成功
     */
    public boolean setLoanUser(String token,Object object) throws Exception {
        return setObject(LOANUSER+token, object, EXPIRE);
    }
    
    /**
     * 从Redis中获取对象
     *
     * @param key 待获取数据的key
     * @return 返回key对应的对象
     */
    public Object getObject(String key) throws Exception {
        Object object = null;
        try {
            String serializeObj = getString(key);
            if (null == serializeObj || serializeObj.length() == 0) {
                object = null;
            } else {
            	object = SerializeUtil.deserialize(serializeObj);
            }
        }  catch (Exception e) {
            throw e;
        }
        return object;
    }

    /**
     * 缓存String类型的数据,数据不过期
     *
     * @param key   待缓存数据的key
     * @param value 需要缓存的额数据
     * @return 返回是否缓存成功
     */
    public boolean setString(String key, String value) throws Exception {
    	redisTemplate.opsForValue().set(key, value);
    	
    	return true;
    }
    
    /**
     * 缓存String类型的数据并设置超时时间
     *
     * @param key     key
     * @param value   value
     * @param timeout 超时时间
     * @return 返回是否缓存成功
     */
    public boolean setString(String key, String value, int timeout) throws Exception {
    	redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    	
    	return true;
    }

    /**
     * 获取String类型的数据
     *
     * @param key 需要获取数据的key
     * @return 返回key对应的数据
     */
    public  String getString(String key) throws Exception {
    	return redisTemplate.opsForValue().get(key);
    }

    public boolean del(String key) throws Exception {
        return redisTemplate.delete(key);
    }

    /**
     * 更新value值，不改变过期时间
     * @param key 需修改值的key
     * @param object 需修改的值
     * @param offset 偏移量，从偏移量开始用value参数覆写(overwrite)键key储存的字符串值
     * @return
     * @throws Exception
     */
    public Boolean setRange(String key, Object object, Long offset) throws Exception {
        String value = SerializeUtil.serialize(object);
        redisTemplate.boundValueOps(key).set(value, offset);
        return true;
    }

    public boolean hasKey(String key) throws Exception {
        return redisTemplate.hasKey(key);
    }

}
