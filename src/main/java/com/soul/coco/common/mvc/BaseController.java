package com.soul.coco.common.mvc;

import com.soul.coco.common.utils.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * 类的功能描述.
 * 公共的控件器，可在类中实现一些共同的方法和属性 持续更新中
 * @auther zxzxyangjie
 * @Date 2017/4/28
 */
public class BaseController {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    protected RedisUtil redisUtil;

    /**
	 * 获取客户端的真实ip
	 * @param request
	 * @return
	 */
	protected String getClientIP(HttpServletRequest request) {
		logger.info("获取IP地址");
		String ipAddress = request.getHeader("x-forwarded-for");  
        if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {  
            ipAddress = request.getHeader("Proxy-Client-IP");  
        }  
        if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {  
            ipAddress = request.getHeader("WL-Proxy-Client-IP");  
        }  
        if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {  
            ipAddress = request.getRemoteAddr();  
            if(ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")){  
                //根据网卡取本机配置的IP  
                InetAddress inet=null;  
                try {  
                    inet = InetAddress.getLocalHost();  
                } catch (UnknownHostException e) {  
                    e.printStackTrace();  
                }  
                ipAddress= inet.getHostAddress();  
            }  
        }  
        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割  
        if(ipAddress!=null && ipAddress.length()>15){ //"***.***.***.***".length() = 15  
            if(ipAddress.indexOf(",")>0){  
                ipAddress = ipAddress.substring(0,ipAddress.indexOf(","));  
            }  
        }  
        return ipAddress;
	}
	
	/**
	 * 获取本机mac地址
	 * @param ia
	 * @return
	 * @throws SocketException
	 */
	protected static String getLocalMac(InetAddress ia) throws SocketException {
		//获取网卡，获取地址
		byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
		StringBuffer sb = new StringBuffer("");
		for(int i=0; i<mac.length; i++) {
			if(i!=0) {
				sb.append("-");
			}
			//字节转换为整数
			int temp = mac[i]&0xff;
			String str = Integer.toHexString(temp);
			System.out.println("每8位:"+str);
			if(str.length()==1) {
				sb.append("0"+str);
			}else {
				sb.append(str);
			}
		}
		return sb.toString().toUpperCase();
	}

	/**
	 * 获取访问来源
	 * @param request
	 * @return
	 */
	protected static Integer getSource(HttpServletRequest request) {
		String userAgent = request.getHeader("user-agent").toLowerCase();;
		if(userAgent.indexOf("micromessenger")!= -1){
			//微信
			return 4;
		}else if(userAgent.indexOf("android") != -1){
			//安卓
			return 2;
		}else if(userAgent.indexOf("iphone") != -1 || userAgent.indexOf("ipad") != -1 || userAgent.indexOf("ipod") != -1){
			//苹果
			return 3;
		}else{
			//网页
			return 1;
		}
	}

}
