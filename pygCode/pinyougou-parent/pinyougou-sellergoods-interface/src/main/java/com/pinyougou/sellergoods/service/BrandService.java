package com.pinyougou.sellergoods.service;

import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbBrand;

import entity.PageResult;

/**
 * 品牌接口
 * @author Jolin
 *
 */
public interface BrandService {

    /**
     * 查询所有品牌记录条数
     * @return
     */
	public List<TbBrand> findAll();
	/**
	 * 品牌分页
	 * @param pageNum 当前页码
	 * @param pageSize  每页记录数
	 * @return
	 */
	public PageResult findPage(int pageNum,int pageSize);
	/**
	 * 增加品牌
	 * @param brand
	 */
	public void add(TbBrand brand);
	/**
	 * 根据ID获取实体的其他信息
	 * @param id
	 * @return
	 */
	public TbBrand findOne(Long id);
	/**
	 * 修改品牌字段
	 * @param brand
	 */
	public void update(TbBrand brand);
	/**
	 * 批量删除品牌
	 * @param ids
	 */
	public void delete(Long [] ids);
	
	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbBrand brand, int pageNum,int pageSize);
	/**
	 * 返回品牌下拉列表
	 * @return
	 */
	public List<Map> selectOptionList();

	
}
