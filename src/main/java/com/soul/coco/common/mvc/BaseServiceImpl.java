package com.soul.coco.common.mvc;

import javax.annotation.Resource;
import java.util.List;


public class BaseServiceImpl<T> implements BaseService<T>{

	
	@Resource
	protected BaseMapper<T> baseMapper;
	
    //添加
	@Override
	public boolean save(T t) throws Exception {
		return baseMapper.insert(t)>0;
	}

	//修改
	@Override
	public boolean upd(T t) throws Exception {
		return baseMapper.update(t)>0;
	}

	//根据条件单个删除
	@Override
	public boolean remove(T t) throws Exception {
		return baseMapper.delete(t)>0;
	}

	//批量删除
	@Override
	public boolean removeBatch(Integer[] ids) throws Exception {
		return baseMapper.deleteBatch(ids)>0;
	}

	
	//根据条件（无条件）查询多个结果
	@Override
	public List<T> findManyByParams(T t) throws Exception {
		
		return baseMapper.queryManyByParams(t);
	}

	//获取表的数据记录条数
	@Override
	public Integer getTotalByParams(T t) throws Exception {
		
		return baseMapper.queryTotalByParams(t);
	}

	//根据条件分页查询，一般分页
	@Override
	public List<T> findPageByParams(Integer page, Integer limit, T t) throws Exception {
		page=page<1?1:page;
		return baseMapper.queryListByParams((page-1)*limit, limit, t);
	}

	//获取数据页数
	@Override
	public Integer getTotalPageByParams(Integer pageSize, T t) throws Exception {
		Integer totalRecord = baseMapper.queryTotalByParams(t);
		if(totalRecord%pageSize==0){
			return (int) (totalRecord/pageSize);
		}else{
			return (int) (totalRecord/pageSize + 1);
		}
		
	}

	//根据条件查询单个结果
	@Override
	public T findObjectByParams(T t) throws Exception {
        List<T> ts = baseMapper.queryManyByParams(t);
        return ts!=null&&ts.size()>0?ts.get(0):null;
	}

	//查询所有
	@Override
	public List<T> findAll() throws Exception {
		return baseMapper.queryAll();
	}

	@Override
	public T queryObjectById(Long id) throws Exception {
		return baseMapper.queryObjectById(id);
	}
}
