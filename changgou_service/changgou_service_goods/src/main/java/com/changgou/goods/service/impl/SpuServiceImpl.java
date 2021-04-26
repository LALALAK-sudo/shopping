package com.changgou.goods.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.dao.*;
import com.changgou.goods.pojo.*;
import com.changgou.goods.service.SpuService;
import com.changgou.util.IdWorker;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private CategoryBrandMapper categoryBrandMapper;
    /**
     * 查询全部列表
     * @return
     */
    @Override
    public List<Spu> findAll() {
        return spuMapper.selectAll();
    }

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    @Override
    public Spu findById(String id){
        return  spuMapper.selectByPrimaryKey(id);
    }

    @Autowired
    private IdWorker idWorker;

    /**
     * 增加
     * @param goods
     */
    @Transactional
    @Override
    public void add(Goods goods){
        // 1 添加spu
        Spu spu = goods.getSpu();
        // 设置分布式id
        long spuId = idWorker.nextId();
        spu.setId(String.valueOf(spuId));
        // 设置删除状态
        spu.setIsDelete("0");
        // 上架状态
        spu.setIsMarketable("0");
        // 审核状态
        spu.setStatus("0");
        spuMapper.insert(spu);
        // 2 添加sku集合
        this.saveSkuList(goods);

    }

    private void saveSkuList(Goods goods) {
        Spu spu = goods.getSpu();
        // 分类对象
        Category category = categoryMapper.selectByPrimaryKey(spu.getCategory3Id());
        // 品牌对象
        Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());
        // 查询关联表
        CategoryBrand categoryBrand = new CategoryBrand();
        categoryBrand.setBrandId(spu.getBrandId());
        categoryBrand.setCategoryId(spu.getCategory3Id());
        int count = categoryBrandMapper.selectCount(categoryBrand);
        if(count == 0 ) {
            // 品牌与分类还没有关联关系 要添加
            categoryBrandMapper.insert(categoryBrand);
        }
        // 获取sku集合
        List<Sku> skuList = goods.getSkuList();
        if(skuList != null) {
            for(Sku sku:skuList) {
                sku.setId(String.valueOf(idWorker.nextId()));
                if(StringUtils.isEmpty(sku.getSpec())) {
                    sku.setSpec("0");
                }
                String name = spu.getName();
                Map<String ,String> specMap = JSON.parseObject(sku.getSpec(), Map.class);

                if(specMap != null && specMap.size() > 0) {
                    for (String value : specMap.values()) {
                        name += " " + value;
                    }
                }
                sku.setName(name);
                sku.setSpuId(spu.getId());
                // 创建时间和修改时间
                sku.setCreateTime(new Date());
                sku.setUpdateTime(new Date());
                // 分类id
                sku.setCategoryId(category.getId());
                sku.setCategoryName(category.getName());
                // 品牌名称
                sku.setBrandName(brand.getName());
                // 将sku添加到数据库
                skuMapper.insertSelective(sku);
            }
        }
    }


    /**
     * 修改
     * @param goods
     */
    @Override
    public void update(Goods goods){
        // 修改spu
        Spu spu = goods.getSpu();
        spuMapper.updateByPrimaryKey(spu);
        // 修改sku(删除+重新添加)
        Example example = new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("spuId",spu.getId());
        skuMapper.deleteByExample(example);
        this.saveSkuList(goods);
        // spuMapper.updateByPrimaryKey(spu);
    }

    /**
     * 删除
     * @param id
     */
    @Override
    public void delete(String id){
        // 逻辑删除
        // 查询spu
        Spu spu = spuMapper.selectByPrimaryKey(id);
        // 判断当前商品是否处于下架状态
        if(!spu.getIsMarketable().equals("0")) {
            throw new RuntimeException("当前商品必须处于下架状态才能删除");
        }
        // 如果是下假状态，修改相对应的标记未逻辑删除
        spu.setIsMarketable("1");
        spu.setStatus("0");
        spuMapper.updateByPrimaryKeySelective(spu);
        //spuMapper.deleteByPrimaryKey(id);
    }


    /**
     * 条件查询
     * @param searchMap
     * @return
     */
    @Override
    public List<Spu> findList(Map<String, Object> searchMap){
        Example example = createExample(searchMap);
        return spuMapper.selectByExample(example);
    }

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    @Override
    public Page<Spu> findPage(int page, int size){
        PageHelper.startPage(page,size);
        return (Page<Spu>)spuMapper.selectAll();
    }

    /**
     * 条件+分页查询
     * @param searchMap 查询条件
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @Override
    public Page<Spu> findPage(Map<String,Object> searchMap, int page, int size){
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        return (Page<Spu>)spuMapper.selectByExample(example);
    }

    /**
     * 根据id查询spu与sku的列表信息
     * @param id
     * @return
     */
    @Override
    public Goods findGoodsById(String id) {
        Goods goods = new Goods();
        // 查询spu封装在goods
        Spu spu = spuMapper.selectByPrimaryKey(id);
        goods.setSpu(spu);
        // 查询sku集合封装在goods
        Example example = new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("spuId",id);
        List<Sku> skus = skuMapper.selectByExample(example);
        goods.setSkuList(skus);
        return goods;
    }

    @Override
    @Transactional
    public void audit(String id) {
        // 查询spu对象
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if(spu == null) {
            throw new RuntimeException("当前商品不存在");
        }
        // 判断当前spu是否处于删除状态
        if("1".equals(spu.getIsDelete())) {
            throw new RuntimeException("当前商品处于删除状态");
        }
        // 不处于删除状态 修改审核状态位1 上下架状态位1
        spu.setStatus("1");
        spu.setIsMarketable("1");
        // 执行修改操作
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    @Override
    public void pull(String id) {
        // 查询spu
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if(spu == null) {
            throw new RuntimeException("当商品不存在");
        }
        // 判断当前商品是否处于删除状态
        if("1".equals(spu.getIsDelete())) {
            throw new RuntimeException("当前商品处于删除状态");
        }
        // 商品出与未删除状态的化，则修改上下架状态未已下架
        spu.setIsMarketable("0");
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    @Override
    @Transactional
    public void put(String id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if(spu == null) {
            throw new RuntimeException("当前商品不存在'");
        }
        // 商品状态必须是1 已经审核
        if(!"1".equals(spu.getStatus())) {
            throw new RuntimeException("当前商品未审核");
        }
        spu.setIsMarketable("1");
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    @Override
    @Transactional
    public void restore(String id) {
        // 查询spu
        Spu spu = spuMapper.selectByPrimaryKey(id);
        // 判断当前商品必须处于已删除
        if("1".equals(spu.getIsDelete())) {
            throw new RuntimeException("此商品未删除");
        }
        // 修改相关的属性字段进行保存操作
        spu.setIsDelete("0");
        spu.setStatus("0");
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    @Override
    @Transactional
    public void realDel(String id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        // 当前商品是否未已删除状态
        if("1".equals(spu.getIsDelete())) {
            throw new RuntimeException("商品处于未删除");
        }
        // 执行删除操作
        spuMapper.deleteByPrimaryKey(id);
    }

    /**
     * 构建查询对象
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // 主键
            if(searchMap.get("id")!=null && !"".equals(searchMap.get("id"))){
                criteria.andEqualTo("id",searchMap.get("id"));
           	}
            // 货号
            if(searchMap.get("sn")!=null && !"".equals(searchMap.get("sn"))){
                criteria.andEqualTo("sn",searchMap.get("sn"));
           	}
            // SPU名
            if(searchMap.get("name")!=null && !"".equals(searchMap.get("name"))){
                criteria.andLike("name","%"+searchMap.get("name")+"%");
           	}
            // 副标题
            if(searchMap.get("caption")!=null && !"".equals(searchMap.get("caption"))){
                criteria.andLike("caption","%"+searchMap.get("caption")+"%");
           	}
            // 图片
            if(searchMap.get("image")!=null && !"".equals(searchMap.get("image"))){
                criteria.andLike("image","%"+searchMap.get("image")+"%");
           	}
            // 图片列表
            if(searchMap.get("images")!=null && !"".equals(searchMap.get("images"))){
                criteria.andLike("images","%"+searchMap.get("images")+"%");
           	}
            // 售后服务
            if(searchMap.get("saleService")!=null && !"".equals(searchMap.get("saleService"))){
                criteria.andLike("saleService","%"+searchMap.get("saleService")+"%");
           	}
            // 介绍
            if(searchMap.get("introduction")!=null && !"".equals(searchMap.get("introduction"))){
                criteria.andLike("introduction","%"+searchMap.get("introduction")+"%");
           	}
            // 规格列表
            if(searchMap.get("specItems")!=null && !"".equals(searchMap.get("specItems"))){
                criteria.andLike("specItems","%"+searchMap.get("specItems")+"%");
           	}
            // 参数列表
            if(searchMap.get("paraItems")!=null && !"".equals(searchMap.get("paraItems"))){
                criteria.andLike("paraItems","%"+searchMap.get("paraItems")+"%");
           	}
            // 是否上架
            if(searchMap.get("isMarketable")!=null && !"".equals(searchMap.get("isMarketable"))){
                criteria.andEqualTo("isMarketable",searchMap.get("isMarketable"));
           	}
            // 是否启用规格
            if(searchMap.get("isEnableSpec")!=null && !"".equals(searchMap.get("isEnableSpec"))){
                criteria.andEqualTo("isEnableSpec", searchMap.get("isEnableSpec"));
           	}
            // 是否删除
            if(searchMap.get("isDelete")!=null && !"".equals(searchMap.get("isDelete"))){
                criteria.andEqualTo("isDelete",searchMap.get("isDelete"));
           	}
            // 审核状态
            if(searchMap.get("status")!=null && !"".equals(searchMap.get("status"))){
                criteria.andEqualTo("status",searchMap.get("status"));
           	}

            // 品牌ID
            if(searchMap.get("brandId")!=null ){
                criteria.andEqualTo("brandId",searchMap.get("brandId"));
            }
            // 一级分类
            if(searchMap.get("category1Id")!=null ){
                criteria.andEqualTo("category1Id",searchMap.get("category1Id"));
            }
            // 二级分类
            if(searchMap.get("category2Id")!=null ){
                criteria.andEqualTo("category2Id",searchMap.get("category2Id"));
            }
            // 三级分类
            if(searchMap.get("category3Id")!=null ){
                criteria.andEqualTo("category3Id",searchMap.get("category3Id"));
            }
            // 模板ID
            if(searchMap.get("templateId")!=null ){
                criteria.andEqualTo("templateId",searchMap.get("templateId"));
            }
            // 运费模板id
            if(searchMap.get("freightId")!=null ){
                criteria.andEqualTo("freightId",searchMap.get("freightId"));
            }
            // 销量
            if(searchMap.get("saleNum")!=null ){
                criteria.andEqualTo("saleNum",searchMap.get("saleNum"));
            }
            // 评论数
            if(searchMap.get("commentNum")!=null ){
                criteria.andEqualTo("commentNum",searchMap.get("commentNum"));
            }

        }
        return example;
    }

}
