package com.soul.coco.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

/*	public static void main(String[] agr) {
		try {
			String ag = "{\"showapi_res_error\": \"\",\"showapi_res_id\": \"672f35f525b24d92bcc3d236c2217baa\",\"showapi_res_code\": 0,\"showapi_res_body\": {\"tel\": \"95533\",\"bankName\": \"中国建设银行\",\n" +
					"\"cardType\": \"借记卡\",\"url\": \"www.ccb.com\",\"ret_code\": 0,\"area\": \"四川省 - 成都\",\"brand\": \"建设银行 - 龙卡储蓄卡(银联卡) - 借记卡\",\"cardNum\": \"6227003813920572233\",\n" +
					"\"simpleCode\": \"CCB\"}}";
			JSONObject jsStr = JSONObject.parseObject(ag);
			Object ad = jsStr.get("showapi_res_body");
			Map<String, Object> map = (Map<String, Object>) ad;

			BankBo stud = new BankBo();
			BeanUtils.populate(stud, map);
			System.out.println(stud);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

	/**
	 * 获取ip地址
	 *
	 * @return
	 */
	public static String getIpAddress(HttpServletRequest request) {
		String ipAddress = null;
		ipAddress = request.getHeader("x-forwarded-for");
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getRemoteAddr();
			if (ipAddress.equals("127.0.0.1")) {
				// 根据网卡取本机配置的IP
				InetAddress inet = null;
				try {
					inet = InetAddress.getLocalHost();
					ipAddress = inet.getHostAddress();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}
		}

		// 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
		if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length()
			// = 15
			if (ipAddress.indexOf(",") > 0) {
				ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
			}
		}
		return ipAddress;
	}

	/**
	 * 校验银行卡号
	 * @param bankCard
	 * @return
	 */
	public static boolean checkBankCard(String bankCard) {
		if(bankCard.length() < 15 || bankCard.length() > 19) {
			return false;
		}
		char bit = getBankCardCheckCode(bankCard.substring(0, bankCard.length() - 1));
		if(bit == 'N'){
			return false;
		}
		return bankCard.charAt(bankCard.length() - 1) == bit;
	}

	/**
	 * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
	 * @param nonCheckCodeBankCard
	 * @return
	 */
	public static char getBankCardCheckCode(String nonCheckCodeBankCard) {
		if (nonCheckCodeBankCard == null || nonCheckCodeBankCard.trim().length() == 0
				|| !nonCheckCodeBankCard.matches("\\d+")) {
			//如果传的不是数据返回N
			return 'N';
		}
		char[] chs = nonCheckCodeBankCard.trim().toCharArray();
		int luhmSum = 0;
		for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
			int k = chs[i] - '0';
			if (j % 2 == 0) {
				k *= 2;
				k = k / 10 + k % 10;
			}
			luhmSum += k;
		}
		return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
	}

	/**
	 * 判断身份证号码是否合法
	 * @param IDNumber
	 * @return
	 */
	public static boolean isIDNumber(String IDNumber) {
		if (IDNumber == null || "".equals(IDNumber)) {
			return false;
		}
		// 定义判别用户身份证号的正则表达式（15位或者18位，最后一位可以为字母）
		String regularExpression = "(^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|" +
				"(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}$)";

		boolean matches = IDNumber.matches(regularExpression);

		//判断第18位校验值
		if (matches) {

			if (IDNumber.length() == 18) {
				try {
					char[] charArray = IDNumber.toCharArray();
					//前十七位加权因子
					int[] idCardWi = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
					//这是除以11后，可能产生的11位余数对应的验证码
					String[] idCardY = {"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
					int sum = 0;
					for (int i = 0; i < idCardWi.length; i++) {
						int current = Integer.parseInt(String.valueOf(charArray[i]));
						int count = current * idCardWi[i];
						sum += count;
					}
					char idCardLast = charArray[17];
					int idCardMod = sum % 11;
					if (idCardY[idCardMod].toUpperCase().equals(String.valueOf(idCardLast).toUpperCase())) {
						return true;
					} else {
						System.out.println("身份证最后一位:" + String.valueOf(idCardLast).toUpperCase() +
								"错误,正确的应该是:" + idCardY[idCardMod].toUpperCase());
						return false;
					}

				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("异常:" + IDNumber);
					return false;
				}
			}

		}
		return matches;
	}

	/**
	 * 获取专利授权年度
	 * @param apply_date
	 * @param auth_date
	 * @return
	 */
	public static int getAuthYear(String apply_date, String auth_date) {
		if (!apply_date.matches("\\d{4}-\\d{2}-\\d{2}") || !auth_date.matches("\\d{4}-\\d{2}-\\d{2}")) {
			return 0;
		}
		int apply_year = Utils.parseInt(apply_date.substring(0,4));
		int apply_month = Utils.parseInt(apply_date.substring(5,7));
		int apply_day = Utils.parseInt(apply_date.substring(8,10));
		int auth_year = Utils.parseInt(auth_date.substring(0,4));
		int auth_month = Utils.parseInt(auth_date.substring(5,7));
		int auth_day = Utils.parseInt(auth_date.substring(8,10));
		
		if (apply_year == auth_year) {
			return 1;
		}
		if (auth_month > apply_month) {
			return auth_year - apply_year + 1;
		}
		if (auth_month == apply_month) {
			if (auth_day >= apply_day) {
				return auth_year - apply_year + 1;
			}
		}
		return auth_year - apply_year;
	}
	
	public static boolean isEmpty(String str) {
		if (str == null || str.equals("")) {
			return true;
		}
		return false;
	}

	/**
	 * 返回不为空的字符串
	 * 
	 * @param obj
	 * @return
	 */
	public static String getString(Object obj) {
		if (obj == null || obj.toString().equals("null")) {
			return "";
		}
		return obj.toString();
	}

	/**
	 * 返回不为空的字符串
	 * 
	 * @param obj
	 * @param def
	 *            默认值
	 * @return
	 */
	public static String getString(Object obj, String def) {
		if (obj == null || obj.toString().equals("null")) {
			return def;
		}
		return obj.toString().trim();
	}

	/**
	 * 返回给定格式的服务器时间
	 * 
	 * @param parrent
	 *            String 默认返回yyyy-MM-dd HH:mm:ss格式的时间
	 * @return
	 */
	public static String getServerTime(String parrent) {
		if (parrent == null || parrent.equals("")) {
			parrent = "yyyy-MM-dd HH:mm:ss";
		}
		try {
			Calendar c = Calendar.getInstance(Locale.CHINESE);
			SimpleDateFormat sformat = new SimpleDateFormat(parrent, Locale.CHINA);
			return sformat.format(c.getTime());
		} catch (Exception e) {
			e.printStackTrace();
			return (new Timestamp(System.currentTimeMillis()) + "").substring(0, 19);
		}
	}

	/**
	 * 格式化时间 date转string
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDate(Date date) {
		try {
			SimpleDateFormat sformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
			return sformat.format(date);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 返回给定格式的服务器时间
	 * 
	 * @param parrent
	 *            String 默认返回yyyy-MM-dd HH:mm:ss格式的时间
	 * @param field
	 *            the calendar field,eg:Calendar.WEEK_OF_YEAR
	 * @param offset
	 *            the amount of date or time to be added to the field, eg:-2
	 * @return
	 */
	public static String getServerTime(String parrent, int field, int offset) {
		if (parrent == null || parrent.equals("")) {
			parrent = "yyyy-MM-dd HH:mm:ss";
		}
		try {
			Calendar c = Calendar.getInstance(Locale.CHINESE);
			SimpleDateFormat sformat = new SimpleDateFormat(parrent);
			c.add(field, offset);
			return sformat.format(c.getTime());
		} catch (Exception e) {
			e.printStackTrace();
			return (new Timestamp(System.currentTimeMillis()) + "").substring(0, 19);
		}
	}

	/**
	 * 计算时间，返回yyyy-MM-dd HH:mm:ss
	 * 
	 * @param datetime
	 *            String 原始时间
	 * @param field
	 *            the calendar field,eg:Calendar.WEEK_OF_YEAR
	 * @param offset
	 *            the amount of date or time to be added to the field, eg:-2
	 * @return
	 */
	public static String getDatetime(String datetime, int field, int offset) {
		String parrent = "yyyy-MM-dd HH:mm:ss";
		try {
			SimpleDateFormat sformat = new SimpleDateFormat(parrent);
			Calendar c = Calendar.getInstance(Locale.CHINESE);
			c.setTime(sformat.parse(datetime));
			c.add(field, offset);
			return sformat.format(c.getTime());
		} catch (Exception e) {
			e.printStackTrace();
			return datetime;
		}
	}
	
	public static String getDatetime(String datetime,String parrent, int field, int offset) {
		if (parrent == null || parrent.equals("")) {
			parrent = "yyyy-MM-dd HH:mm:ss";
		}
		try {
			SimpleDateFormat sformat = new SimpleDateFormat(parrent);
			Calendar c = Calendar.getInstance(Locale.CHINESE);
			c.setTime(sformat.parse(datetime));
			c.add(field, offset);
			return sformat.format(c.getTime());
		} catch (Exception e) {
			e.printStackTrace();
			return datetime;
		}
	}

	/**
	 * 计算日期，返回yyyy-MM-dd
	 * 
	 * @param date
	 *            String 原始时间
	 * @param field
	 *            the calendar field,eg:Calendar.WEEK_OF_YEAR
	 * @param offset
	 *            the amount of date or time to be added to the field, eg:-2
	 * @return
	 */
	public static String getDate(String date, int field, int offset) {
		String parrent = "yyyy-MM-dd";
		try {
			date = date.substring(0, 10);
			SimpleDateFormat sformat = new SimpleDateFormat(parrent);
			Calendar c = Calendar.getInstance(Locale.CHINESE);
			c.setTime(sformat.parse(date));
			c.add(field, offset);
			return sformat.format(c.getTime());
		} catch (Exception e) {
			e.printStackTrace();
			return date;
		}
	}

	public static double parseDouble(Object str) {
		if (str == null) {
			return 0;
		}
		String s = str.toString().trim();
		if (!s.matches("-?\\d+(\\.\\d+)?")) {
			return 0;
		}
		return Double.parseDouble(s);
	}

	public static float parseFloat(Object str) {
		if (str == null) {
			return 0;
		}
		String s = str.toString().trim();
		if (!s.matches("-?\\d+(\\.\\d+)?")) {
			return 0;
		}
		return Float.parseFloat(s);
	}

	public static int parseInt(Object str) {
		return parseInt(str, 0);
	}

	public static int parseInt(Object str, int defaultValue) {
		if (str == null || str.equals("")) {
			return defaultValue;
		}
		String s = str.toString().trim();
		if (!s.matches("-?\\d+")) {
			return defaultValue;
		}
		return Integer.parseInt(s);
	}

	public static long parseLong(Object str) {
		if (str == null || str.equals("")) {
			return 0;
		}
		String s = str.toString().trim();
		if (!s.matches("-?\\d+")) {
			return 0;
		}
		return Long.parseLong(s);
	}

	/**
	 * 左补字符0
	 * 
	 * @param str
	 * @param length
	 * @return
	 */
	public static String fill0Left(String str, int length) {
		if (str == null) {
			str = "";
		}
		int len = length - str.length();
		if (len > 0) {
			for (int i = 0; i < len; i++) {
				str = "0" + str;
			}
		}
		return str;
	}

	/**
	 * Map 转成 String
	 * 
	 * @param map
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String toString(Map<?, ?> map) {
		if (map == null) {
			return "";
		}
		if (map.isEmpty()) {
			return "{}";
		}
		StringBuffer buf = new StringBuffer();
		buf.append("{");

		Iterator<?> i = map.entrySet().iterator();
		boolean hasNext = i.hasNext();
		while (hasNext) {
			Entry<?, ?> e = (Entry<?, ?>) (i.next());
			Object key = e.getKey();
			Object value = e.getValue();
			buf.append((key == map ? "(this Map)" : key));

			buf.append("=");
			if (value == map) {
				buf.append("(this Map)");
			} else {
				if (value instanceof Object[]) {
					Object[] objs = (Object[]) value;
					buf.append("[");
					for (int j = 0; j < objs.length; j++) {
						if (j > 0) {
							buf.append(", ");
						}
						buf.append(objs[j]);
					}
					buf.append("]");
				} else if (value instanceof Map) {
					buf.append(toString((Map<?, ?>) value));
				} else {
					buf.append(value);
				}
			}
			hasNext = i.hasNext();
			if (hasNext) {
				buf.append(", ");
			}
		}
		buf.append("}");
		return buf.toString();
	}

	/**
	 * Object[] 转成 String
	 * 
	 * @param objs
	 * @return
	 */
	public static String toString(Object[] objs) {
		if (objs == null) {
			return "";
		}
		StringBuffer buf = new StringBuffer();
		buf.append("[");
		for (int j = 0; j < objs.length; j++) {
			if (j > 0) {
				buf.append(", ");
			}
			buf.append(String.valueOf(objs[j]));
		}
		buf.append("]");
		return buf.toString();
	}

	/**
	 * List<Map<String, Object>> 转成 String
	 * 
	 * @param objs
	 * @return
	 */
	public static String toString(List<HashMap<String, Object>> objs) {
		if (objs == null) {
			return "";
		}
		StringBuffer buf = new StringBuffer();
		buf.append("[");
		int j = 0;
		for (Map<String, Object> map : objs) {
			if (j > 0) {
				buf.append(", ");
			}
			buf.append(toString(map));
			j++;
		}
		buf.append("]");
		return buf.toString();
	}

	/**
	 * 是否早于当天
	 * 
	 * @param date
	 *            yyyy-MM-dd
	 * 
	 * @return
	 */
	public static boolean beforeToday(String date) {
		SimpleDateFormat sformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date d = sformat.parse(date + " 00:00:00");
			Date now = sformat.parse(getServerTime("yyyy-MM-dd") + " 00:00:00");
			return d.before(now);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 是否晚于当天
	 * 
	 * @param date
	 *            yyyy-MM-dd
	 * @return
	 */
	public static boolean afterToday(String date) {
		SimpleDateFormat sformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date d = sformat.parse(date + " 00:00:00");
			Date now = sformat.parse(getServerTime("yyyy-MM-dd") + " 00:00:00");
			return d.after(now);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 是否是当天
	 * 
	 * @param date
	 *            yyyy-MM-dd
	 * @return
	 */
	public static boolean isToday(String date) {
		return date.equals(getServerTime("yyyy-MM-dd"));
	}

	/**
	 * 是否是当天(工作日)
	 * 
	 * @param weekday
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static boolean isWeekday(int weekday) {
		return (new Date().getDay() == weekday - 1) ? true : false;
	}

	/**
	 * 是否在两个日期段内
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static boolean inDate(String startDate, String endDate) {
		return !afterToday(startDate) && !beforeToday(endDate);
	}

	/**
	 * 是否在两个时间段内
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static boolean inTime(String startTime, String endTime) {
		startTime = startTime.replace(":", "");
		if (startTime.length() == 4) {
			startTime = startTime + "00";
		}
		endTime = endTime.replace(":", "");
		if (endTime.length() == 4) {
			endTime = endTime + "00";
		}
		int now = Integer.parseInt(getServerTime("HHmmss"));
		if (now >= Integer.parseInt(startTime) && now <= Integer.parseInt(endTime)) {
			return true;
		}
		return false;
	}

	/**
	 * 是否早于当前时间
	 * 
	 * @param time
	 *            HHmm
	 * @return
	 */
	public static boolean beforeNow(String time) {
		time = time.replace(":", "");
		if (time.length() == 4) {
			time = time + "00";
		}
		int now = Integer.parseInt(getServerTime("HHmmss"));
		if (Integer.parseInt(time) < now) {
			return true;
		}
		return false;
	}

	/**
	 * 是否晚于当前时间
	 * 
	 * @param time
	 *            HHmm
	 * @return
	 */
	public static boolean afterNow(String time) {
		time = time.replace(":", "");
		if (time.length() == 4) {
			time = time + "00";
		}
		int now = Integer.parseInt(getServerTime("HHmmss"));
		if (Integer.parseInt(time) > now) {
			return true;
		}
		return false;
	}

	public static String toString(double d) {
		BigDecimal bd = new BigDecimal(d);
		String s = bd.toPlainString();
		String[] ss = s.split("\\.");
		if (ss.length == 2) {
			if (ss[1].length() > 10) {
				return ss[0] + "." + ss[1].substring(0, 10);
			}
		}
		return s;
	}

	public static double toDouble(double d) {
		BigDecimal bd = new BigDecimal(d);
		String s = bd.toPlainString();
		String[] ss = s.split("\\.");
		if (ss.length == 2) {
			if (ss[1].length() > 10) {
				return Double.parseDouble(ss[0] + "." + ss[1].substring(0, 10));
			}
		}
		return Double.parseDouble(s);
	}

	public static String getWeekStr(int week) {
		switch (week) {
		case 1:
			return "周日";
		case 2:
			return "周一";
		case 3:
			return "周二";
		case 4:
			return "周三";
		case 5:
			return "周四";
		case 6:
			return "周五";
		case 7:
			return "周六";
		}
		return "未知[" + week + "]";
	}

	public static String encode(String str) {
		try {
			return URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;
	}

	public static String decode(String str) {
		try {
			return URLDecoder.decode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;
	}

	public static String simpleDatetime(String datetime) {
		if (datetime == null) {
			return "";
		}
		if (datetime.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
			return datetime.substring(5, 16);
		} else if (datetime.matches("\\d{4}-\\d{2}-\\d{2}")) {
			return datetime.substring(5, 10);
		} else if (datetime.matches("\\d{2}:\\d{2}:\\d{2}")) {
			return datetime.substring(0, 5);
		}
		return datetime;
	}

	public static String formatDate(String datetime) {
		if (datetime == null) {
			return "";
		}
		if (datetime.length() > 10) {
			return datetime.substring(0, 10);
		}
		return datetime;
	}

	/**
	 * 当date2 >= date1 时返回true
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean matchDate(String date1, String date2) {
		if (date1 == null || date1.equals("")) {
			return true;
		}
		if (date2 == null || date2.equals("")) {
			return false;
		}
		SimpleDateFormat sformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date d1 = sformat.parse(date1 + " 00:00:00");
			Date d2 = sformat.parse(date2 + " 00:00:00");
			return d1.equals(d2) || d1.before(d2);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static final String REG_MOBILE = "/^1\\d{10}$/";// 手机号正则表达试
	public static final String REG_TEL = "(0\\d{2,3}-?\\d{7,8}(-\\d{3,})?)|(400\\d{7})";// 验证座机
	public static final String REG_MAIL = "\\w+([\\-+\\.]\\w+)*@\\w+([\\-\\.]\\w+)*\\.\\w+([\\-\\.]\\w+)*";// 验证邮箱

	/**
	 * 是否是有效的手机号
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isMobile(String str) {
		if (str == null || str.equals("")  || str.length() != 11) {
			return false;
		}
		return true;
	}

	/**
	 * 是否是有效的座机
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isTelephone(String str) {
		if (str == null || str.equals("")) {
			return false;
		}
		return str.matches(REG_TEL);
	}

	/**
	 * 是否是有效的邮箱号
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isMail(String str) {
		if (str == null || str.equals("")) {
			return false;
		}
		return str.matches(REG_MAIL);
	}

	public static String date(int y, int m, int d) {
		String date = y + "-";
		if (m < 10) {
			date += "0" + m + "-";
		} else {
			date += m + "-";
		}
		if (d < 10) {
			date += "0" + d;
		} else {
			date += d;
		}
		return date;
	}

	/**
	 * 将字符串时间转换成日期格式
	 * 
	 * @param datetime
	 *            String 默认返回yyyy-MM-dd HH:mm:ss格式的时间
	 * 
	 * @return
	 */
	public static Date toDate(String datetime) {
		if (datetime == null || datetime.length() < 0) {
			return null;
		}
		if (datetime.matches("\\d{14,}")) {
			datetime = datetime.substring(0, 4) + "-" + datetime.substring(4, 6) + "-" + datetime.substring(6, 8) + " " + datetime.substring(8, 10) + ":" + datetime.substring(10, 12) + ":" + datetime.substring(12, 14);
		} else if (datetime.matches("\\d{4}-\\d{2}-\\d{2}")) {
			datetime = datetime + " 00:00:00";
		} else if (datetime.matches("\\d{2}:\\d{2}:\\d{2}(.\\d+)?")) {
			datetime = getServerTime("yyyy-MM-dd") + " " + datetime;
		} else if (!datetime.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}(.\\d+)?")) {
			try {
				return new Date(datetime);
			} catch (Exception e) {
				System.err.println("Unparseable date: \"" + datetime + "\"");
				return null;
			}
		}

		try {
			SimpleDateFormat sformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return sformat.parse(datetime);
		} catch (Exception e) {
			System.err.println("Unparseable date: \"" + datetime + "\"");
			return null;
		}
	}

	/**
	 * 计算时间差 (时间单位,开始时间,结束时间) 调用方法
	 * 
	 * @unit ：0-毫秒；1-秒；2-分；3-小时；4-天
	 */
	public static long timeDiff(String time1, String time2, int unit) {
		return timeDiff(time1, time2, unit, true);
	}

	public static final int MILLISECOND = 0;
	public static final int SECOND = 1;
	public static final int MINUTE = 2;
	public static final int HOUR = 3;
	public static final int DAY = 4;

	/**
	 * 计算时间差 (时间单位,开始时间,结束时间) 调用方法
	 * 
	 * @unit ：0-毫秒；1-秒；2-分；3-小时；4-天
	 * @flag : true-返回绝对值,false-区分正分
	 */
	public static long timeDiff(String time1, String time2, int unit, boolean flag) {
		// 单位(如：不足1天(24小时) 则返回0)，开始时间，结束时间
		Date date1 = toDate(time1);
		Date date2 = toDate(time2);
		long ltime = date2.getTime() - date1.getTime();
		if (flag) {
			ltime = Math.abs(ltime);
		}
		if (unit == SECOND) {
			return ltime / 1000;// 返回秒
		} else if (unit == MINUTE) {
			return ltime / 60000;// 返回分钟
		} else if (unit == HOUR) {
			return ltime / 3600000;// 返回小时
		} else if (unit == DAY) {
			return ltime / 86400000;// 返回天数
		} else {
			return ltime;// 毫秒
		}
	}

	public static String genRandomNum(int len) {
		// 35是因为数组是从0开始的，26个字母+10个数字
		final int maxNum = 10;
		int i; // 生成的随机数
		int count = 0; // 生成的长度
		char[] str = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
		StringBuffer num = new StringBuffer("");
		Random r = new Random();
		while (count < len) {
			// 生成随机数，取绝对值，防止生成负数，
			i = Math.abs(r.nextInt(maxNum)); // 生成的数最大为36-1
			if (i >= 0 && i < str.length) {
				num.append(str[i]);
				count++;
			}
		}
		return num.toString();
	}

	/**
	 * 获取SN号码
	 * 
	 * @param currentMaxSN
	 *            当前最大值SN码
	 * @param firstPart
	 *            需要SN码第一部分
	 * @param len
	 *            生产号码的长度(第二部分)
	 * @return
	 */
	public static String getMaxSN(String currentMaxSN, String firstPart, int len) {
		String initSN = "";
		if ("".equals(currentMaxSN) || null == currentMaxSN) {
			String initTwoPart = "";
			for (int i = 0; i < len; i++) {
				initTwoPart += "0";
			}
			initSN = firstPart + initTwoPart;
		} else {
			initSN = currentMaxSN;
		}
		int num = Integer.parseInt(initSN.replace(firstPart, ""));
		String numStr = ++num + "";
		int length = numStr.length();
		for (int i = length; i < len; i++) {
			numStr = "0" + numStr;
		}
		return firstPart + numStr;
	}

	/**
	 * 获取SN号码
	 * 
	 * @param firstPart
	 *            前缀
	 * @param len
	 *            生产随机数
	 * @return
	 */
	public static String getSN(String firstPart, int len) {
		return firstPart + getServerTime("yyyyMMdd") + genRandomNum(len);
	}

	/**
	 * 获取SN号码
	 * 
	 * @param firstPart
	 * @param format
	 * @param len
	 * @return
	 */
	public static String getSN(String firstPart, String format, int len) {
		return firstPart + getServerTime(format) + genRandomNum(len);
	}

	public static String getFileStr(String[] files) {
		String fileStr = "";
		if (files.length > 0) {
			for (int i = 0; i < files.length; i++) {
				if (i == 0) {
					fileStr += files[i];
				} else {
					fileStr += "," + files[i];
				}
			}
		}
		return fileStr;
	}

	/**
	 * 获取文件JSON字符串
	 * 
	 * @param fileList
	 * @return
	 */
	public static String getFileListStr(String[] fileList) {
		if (null != fileList) {
			JSONArray arr = new JSONArray();
			for (int i = 0; i < fileList.length; i++) {
				arr.add(JSONObject.parse(fileList[i]));
			}
			return arr.toString();
		}
		return null;
	}

	/**
	 * 人命币 分转换成元
	 * 
	 * @param cent
	 *            分
	 */
	public static String cent2Yuan(String cent) {
		if (null == cent || "".equals(cent)) {
			return "0";
		}
		String result = (float) (Math.round(Double.valueOf(cent) / 100 * 100)) / 100 + "";
		return result.substring(0, result.indexOf("."));
	}

	/**
	 * 人命币 元转换成分
	 * 
	 * @param yuan
	 * @return
	 */
	public static String yuan2Cent(String yuan) {
		if (null == yuan) {
			return "0";
		}
		return Math.round(Double.valueOf(yuan) * 100) + "";
	}

	public static String formatDateTime(String datetime) {
		if (datetime == null) {
			return "";
		}
		if (datetime.length() > 19) {
			return datetime.substring(0, 19);
		}
		return datetime;
	}

	public static String getDefaultPassword(int length) {
		String password = "";
		for (int i = 0; i < length; i++) {
			char[] codeSeq = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '2', '3', '4', '5', '6', '7', '8', '9' };
			password += String.valueOf(codeSeq[new Random().nextInt(codeSeq.length)]);// random.nextInt(10));
		}
		return password;
	}

	/**
	 * 验证邮箱
	 * 
	 * @param email
	 * @return true 是邮箱 false 不是邮箱
	 */
	public static boolean isEmail(String email) {
		boolean flag = false;
		try {
			// String check =
			// "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			Pattern regex = Pattern.compile(REG_MAIL);
			Matcher matcher = regex.matcher(email);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 得到几天前的时间
	 * 
	 * @param date
	 * @param day
	 * @return
	 */
	public static String[][] getDateBefore(String date, int day) {
		String[][] res = new String[2][];
		String[] real_date = new String[day];// 真实的日期
		String[] show_date = new String[day];// 用来显示的日期
		try {
			Calendar now = Calendar.getInstance();
			SimpleDateFormat sformat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
			SimpleDateFormat show_date_format = new SimpleDateFormat("d/M", Locale.CHINA);
			now.setTime(sformat.parse(date));
			now.set(Calendar.DATE, now.get(Calendar.DATE) - day - 1);
			int index = 0;
			for (int i = 1; i <= day; i++) {
				now.set(Calendar.DATE, now.get(Calendar.DATE) + 1);
				real_date[index] = sformat.format(now.getTime());
				show_date[index] = show_date_format.format(now.getTime());
				index++;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		res[0] = real_date;
		res[1] = show_date;
		return res;
	}

	/**
	 * 几天前
	 * 
	 * @param day
	 * @return
	 */
	public static String getDateBefore(int day) {
		try {
			Calendar now = Calendar.getInstance();
			SimpleDateFormat sformat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
			// SimpleDateFormat show_date_format = new SimpleDateFormat("d/M",
			// Locale.CHINA);
			now.setTime(sformat.parse(getServerTime("yyyy-MM-dd")));
			now.set(Calendar.DATE, now.get(Calendar.DATE) - day - 1);
			return sformat.format(now.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return getServerTime("yyyy-MM-dd");
	}

	/**
	 * 封装JDK自带的UUID, 通过Random数字生成, 中间无-分割.
	 */
	public static String uuid() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	/**
	 * 格式化金额
	 * 
	 * @param amount
	 * @return
	 */
	public static String getDecimalFormat(String amount) {
		double formatAmount = Utils.parseDouble(amount);
		DecimalFormat decimalFormat = new DecimalFormat();
		decimalFormat.applyPattern("##,###");
		return decimalFormat.format(formatAmount);
	}

	/**
	 * 生成code字符串
     * @param prefix 编码前缀
	 * @return
	 */
	public static String getCode(String prefix){
        String code = "" ;
		if(!StringUtils.isEmpty(prefix)){
            code = getServerTime("yyyyMMddHHmmss");
		}
		return code;
	}

	/**对字符串进行MD5编码*/
//	public static String encodeByMD5(String originString){
//		if (originString!=null) {
//			try {
//				//创建具有指定算法名称的信息摘要
//				MessageDigest md5 = MessageDigest.getInstance("MD5");
//				//使用指定的字节数组对摘要进行最后更新，然后完成摘要计算
//				byte[] results = md5.digest(originString.getBytes());
//				//将得到的字节数组变成字符串返回
//				String result = byteArrayToHexString(results);
//				return result;
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		return null;
//	}

	//产生6位随机验证码
	public static String getRandNum(int charCount) {
		String charValue = "";
		for (int i = 0; i < charCount; i++) {
			char c = (char) (randomInt(0, 10) + '0');
			charValue += String.valueOf(c);
		}
		return charValue;
	}
	public static  int randomInt(int from, int to) {
		Random r = new Random();
		return from + r.nextInt(to - from);
	}


	/**
	 * JSONArray转List
	 * [ {  "relation" : "父母" ,"name":"你好" ,"phone":"13640910285" ,"isEmergent":"true"} , {  "relation" : "配偶","name":"你好d" ,"phone":"13640910283" ,"isEmergent":"true"} ]
	 * JSONArray 转List集合
	 * @param jsonString 一个字符串JSONArray
	 * @param cls   对象.Class
	 * @param <T>
	 * @return
	 */
	public static <T> List<T> getPersons(String jsonString, Class cls) {
		List<T> list = new ArrayList<T>();
		try {
			list = JSON.parseArray(jsonString, cls);
		} catch (Exception e) {
		}
		return list;
	}

	/**
	 * 16位编号
	 * @return
	 */
	public static String getRecordNumByUUId() {
		int first = new Random(10).nextInt(8) + 1;
		System.out.println(first);
		int hashCodeV = UUID.randomUUID().toString().hashCode();
		if (hashCodeV < 0) {//有可能是负数
			hashCodeV = -hashCodeV;
		}
		// 0 代表前面补充0
		// 4 代表长度为4
		// d 代表参数为正数型
		return first + String.format("%015d", hashCodeV);
	}

}
