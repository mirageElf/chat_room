package com.soul.coco.common.mvc;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *  方法名与mybatis 映射文件SQL ID对应
 */
public interface BaseMapper<T> {

	//添加
	Integer insert(T t) throws Exception;

	//修改
	Integer update(@Param("t") T t) throws Exception;

	//根据条件删除
	Integer delete(@Param("t") T t) throws Exception;

    //批量删除
	Integer deleteBatch(Object[] id) throws Exception;

	//根据id查询单个结果
	T queryObjectById(Long id) throws Exception;

	//查询所有
	List<T> queryAll() throws Exception;

	//根据条件（无条件）查询多个结果集
	List<T> queryManyByParams(@Param("t") T t) throws Exception;

	//根据条件（无条件）分页查询
	List<T> queryListByParams(@Param("currentRecord") Integer currentRecord, @Param("limit") Integer limit, @Param("t") T t) throws Exception;

	//根据条件（无条件）查询数据条数
	Integer queryTotalByParams(@Param("t") T t) throws Exception;
	
}
