package com.soul.coco.common.utils;

import com.soul.coco.common.common.Constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 类Result的功能描述:
 * 返回数据实体类
 * @auther zxzxyangjie
 * @date 2019年1月5日14:59:11
 */
public class Result extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;
	
	public Result() {
		put("code", "0");
	}

	public Result(String code, String msg) {
		put("code", code);
		put("msg", msg);
	}
	
	public static Result error() {
		return new Result(Constant.RESULT.CODE_NO.getValue(),Constant.RESULT.MSG_NO.getValue());
	}
	
	public static Result error(String msg) {
		return error(Constant.RESULT.CODE_NO.getValue(), msg);
	}
	
	public static Result error(String code, String msg) {
		Result r = new Result();
		r.put("code", code);
		r.put("msg", msg);
		return r;
	}

	public static Result ok(String msg) {
		Result r = new Result();
		r.put("msg", msg);
		return r;
	}
	
	public static Result ok(Map<String, Object> map) {
		Result r = new Result();
		r.putAll(map);
		return r;
	}
	
	public static Result ok() {
		return new Result(Constant.RESULT.CODE_YES.getValue(),Constant.RESULT.MSG_YES.getValue());
	}
	
	public static Result auto(int row) {
		if(row > 0) {
			return Result.ok();
		} else {
			return Result.error();
		}
	}
	
	/**
	 * row == count表示都成功
	 * @param row
	 * @param count
	 * @return
	 */
	public static Result auto(int row, int count) {
		if(row == count) {
			return Result.ok();
		} else {
			throw new RuntimeException("数据库处理总数错误");
		}
	}

	public Result put(String key, Object value) {
		super.put(key, value);
		return this;
	}
	
	public Object get(String key) {
		return super.get(key);
	}
}
