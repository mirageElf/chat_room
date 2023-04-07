package com.soul.coco.common.mvc;


import java.util.List;

public interface BaseService<T> {
	
	//添加
	boolean save(T t) throws Exception;
	
	//修改
	boolean upd(T t) throws Exception;
	
	//删除
	boolean remove(T t) throws Exception;
	
	//批量删除
	boolean removeBatch(Integer[] ids) throws Exception;

	//根据条件查询单个结果
	T findObjectByParams(T t) throws Exception;

    //查询所有
  	List<T> findAll() throws Exception;

	//根据id查询单个结果
	T queryObjectById(Long id) throws Exception;
    
    //根据条件（无条件）查询多个结果
  	List<T> findManyByParams(T t) throws Exception;
	
	//获取表的数据记录条数
	Integer getTotalByParams(T t) throws Exception;
	
	//根据条件分页查询，一般分页
	List<T> findPageByParams(Integer page, Integer limit, T t) throws Exception;
	
	//获取数据页数
	Integer getTotalPageByParams(Integer pageSize, T t) throws Exception;

}
