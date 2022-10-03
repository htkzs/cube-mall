package com.kkb.cubemall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kkb.cubemall.common.utils.PageUtils;
import com.kkb.cubemall.common.utils.Query;
import com.kkb.cubemall.product.vo.Category2Vo;
import com.kkb.cubemall.product.vo.Category3Vo;
import com.kkb.cubemall.product.vo.CategoryVo;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.kkb.cubemall.product.dao.CategoryDao;
import com.kkb.cubemall.product.entity.CategoryEntity;
import com.kkb.cubemall.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
    @Autowired
    private RedissonClient redissonClient;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 查询所有分类
     * @return
     */
    @Override
    public List<CategoryEntity> listWithTree() {
        //1.查询所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);
        //2.组装成父子的树形结构
        //2.1 找到所有的一级分类
        List<CategoryEntity> levelOneMenus = entities.stream().filter(
            //过滤过一级分类 parentId==0, 根据这个条件构建出所有一级分类的数据
            categoryEntity -> categoryEntity.getParentId() == 0
        ).map((menu)->{
            //出现递归操作,关联出子分类(2,3级分类)
            menu.setChildrens( getChildrens(menu, entities) );
            return menu;
        }).collect(Collectors.toList());

        return levelOneMenus;
    }

    /**
     * 逻辑删除菜单
     * @param asList
     */
    @Override
    public void removeMenuByIds(List<Integer> asList) {
        //TODO 检查当前要删除的菜单是否被别的地方引用
        //逻辑删除
        baseMapper.deleteBatchIds(asList);
    }

    /**
     * 收集三级菜单id
     * @param categoryId
     * @return [558, 559, 560]
     */
    @Override
    public Long[] findCategoryPath(Integer categoryId) {
        List<Long> paths = new ArrayList<>();
        //通过递归查询到 把当前分类id与 父分类id 添加到 paths集合中
        List<Long> parentPath = findParentPath(categoryId, paths);

        Collections.reverse(parentPath);

        return parentPath.toArray(new Long[parentPath.size()]);
    }
    //查询三级分类的菜单
//    @Override
//    public List<CategoryVo> getLevel1Categorys() {
//        //1.获取一级分类菜单 只需要封装相应的id和name信息
//        List<CategoryEntity> categoryEntities = this.baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_id", 0));
//        List<CategoryVo> categoryVoList = categoryEntities.stream().map(categoryEntity -> {
//            CategoryVo categoryVo = new CategoryVo();
//            BeanUtils.copyProperties(categoryEntity, categoryVo);
//            //2.查询一级分类下关联的二级分类
//            List<CategoryEntity> level2Categorys = this.baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_id", categoryEntity.getId()));
//            if(level2Categorys !=null){
//                List<Category2Vo> category2VoList = level2Categorys.stream().map(level2Category -> {
//                    Category2Vo category2Vo = new Category2Vo();
//                    category2Vo.setId(level2Category.getId().toString());
//                    category2Vo.setName(level2Category.getName());
//                    //3.查询二级分类下的三级分类
//                    List<CategoryEntity> level3Categorys = this.baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_id", level2Category.getId()));
//                    if(level3Categorys !=null){
//                        List<Category3Vo> category3VoList = level3Categorys.stream().map(level3Category -> {
//                            Category3Vo category3Vo = new Category3Vo();
//                            category3Vo.setId(level3Category.getId().toString());
//                            category3Vo.setName(level3Category.getName());
//                            return category3Vo;
//                        }).collect(Collectors.toList());
//                        //给二级分类下设置关联的三级分类
//                        category2Vo.setCategory3VoList(category3VoList);
//                    }
//                    return category2Vo;
//                }).collect(Collectors.toList());
//                //在一级分类上关联二级分类
//                categoryVo.setCategory2VoList(category2VoList);
//            }
//            return categoryVo;
//        }).collect(Collectors.toList());
//        return categoryVoList;
//    }

    //原始三级分类的代码优化 优化为查询所有后 通过集合筛选过滤
    //value = "category" 相当于组名称  key = "'getLevel1Categorys'"相当于存放的键值 注意必须使用单引号 否则会解析为表达式

    /**
     * 使用SpringCache改造
     * @return
     */
    @Override
    @Cacheable(value = "category",key = "#root.method.name")
    public List<CategoryVo> getLevel1Categorys() {
            List<CategoryVo> categoryJsonFromDb = getCategoryJsonWithRedisLock();
            return categoryJsonFromDb;
    }

    /*
    @Override
    @Cacheable(value = "category",key = "#root.method.name")
    public List<CategoryVo> getLevel1Categorys() {
        //1.获取一级分类菜单 只需要封装相应的id和name信息
        //1. 从缓存中查询数据
        String categoryJSON = stringRedisTemplate.opsForValue().get("categoryJSON");
        if (StringUtils.isEmpty(categoryJSON)){
            System.out.println("缓存不命中, 查询数据库....");
            //2. 缓存中没有数据, 查询数据库, 从数据库查询分类数据
            List<CategoryVo> categoryJsonFromDb = getCategoryJsonWithRedisLock();
            //3.将查询到的数据存入缓存中
            String jsonString = JSON.toJSONString(categoryJsonFromDb);
            stringRedisTemplate.opsForValue().set("categoryJSON", jsonString);
            return categoryJsonFromDb;
        }
        //4.如果缓存中有json字符串 ，将JSON转换为java对象
        List<CategoryVo> categoryVos = JSON.parseObject(categoryJSON, new TypeReference<List<CategoryVo>>() {
        });
        return categoryVos;
    }
    */
    /**
     * 三级菜单更新 （缓存一致性 使用失效模式）
     * @param category
     */
    @Override
    @CacheEvict(value = "category",key = "'getLevel1Categorys'")
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
    }

    private List<CategoryVo> getCategoryJsonFromDb() {
        //由于缓存没有命中 其中一个线程拿到锁之后将访问数据库
        synchronized (this){
            String categoryJSON = stringRedisTemplate.opsForValue().get("categoryJSON");
            if (!StringUtils.isEmpty(categoryJSON)) {
                List<CategoryVo> categoryVos = JSON.parseObject(categoryJSON, new TypeReference<List<CategoryVo>>() {
                });
                return categoryVos;
            }
            List<CategoryEntity> categoryEntities = this.baseMapper.selectList(null);
            //查询一级分类
            List<CategoryEntity> category1VoList = getParent_id(categoryEntities,0);
            List<CategoryVo> categoryVoList = category1VoList.stream().map(categoryEntity -> {
                CategoryVo categoryVo = new CategoryVo();
                BeanUtils.copyProperties(categoryEntity, categoryVo);
                //2.查询一级分类下关联的二级分类
                List<CategoryEntity> level2Categorys = getParent_id(categoryEntities,categoryEntity.getId());
                if(level2Categorys !=null){
                    List<Category2Vo> category2VoList = level2Categorys.stream().map(level2Category -> {
                        Category2Vo category2Vo = new Category2Vo();
                        category2Vo.setId(level2Category.getId().toString());
                        category2Vo.setName(level2Category.getName());
                        //3.查询二级分类下的三级分类
                        List<CategoryEntity> level3Categorys = getParent_id(categoryEntities,level2Category.getId());
                        if(level3Categorys !=null){
                            List<Category3Vo> category3VoList = level3Categorys.stream().map(level3Category -> {
                                Category3Vo category3Vo = new Category3Vo();
                                category3Vo.setId(level3Category.getId().toString());
                                category3Vo.setName(level3Category.getName());
                                return category3Vo;
                            }).collect(Collectors.toList());
                            //给二级分类下设置关联的三级分类
                            category2Vo.setCategory3VoList(category3VoList);
                        }
                        return category2Vo;
                    }).collect(Collectors.toList());
                    //在一级分类上关联二级分类
                    categoryVo.setCategory2VoList(category2VoList);
                }
                return categoryVo;
            }).collect(Collectors.toList());
            String jsonString = JSON.toJSONString(categoryVoList);
            //过期时间避免缓存穿透
            stringRedisTemplate.opsForValue().set("categoryJSON", jsonString,1, TimeUnit.DAYS);
            return categoryVoList;
        }

    }

    //使用redisson分布式锁
    public List<CategoryVo> getCategoryJsonWithRedissonLock(){
        //获取redisson的分布式锁
        RLock rLock = redissonClient.getLock("category-lock");
        // redisson默认的看门狗机制解决了因宕机造成的死锁问题
        rLock.lock();
        List<CategoryVo> categoryJsonFromDb = null;
        //如果发生运行时异常
        try{
            categoryJsonFromDb = getCategoryJsonFromDb();
        }finally{
            rLock.unlock();
        }
        return categoryJsonFromDb;
    }

    //使用redis分布式锁
    public List<CategoryVo> getCategoryJsonWithRedisLock() {

        String uuid = UUID.randomUUID().toString();
        //防止执行数据库业务时宕机 增加锁失效时间，防止死锁
        //增加过期时间后又导致的另外一个问题是 线程1 可能删除线程2的锁 解决办法设置每个线程唯一一把锁
        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid,30,TimeUnit.SECONDS);
        if(lock){
            //成功获取到锁后查数据库
            //如果执行业务时发生异常 将导致锁无法释放
            List<CategoryVo> categoryJsonFromDb = null;
            try{
                categoryJsonFromDb = getCategoryJsonFromDb();
            }catch (Exception e){
                e.printStackTrace();
            }finally{
                //stringRedisTemplate.delete("lock");
                String lockValue= stringRedisTemplate.opsForValue().get("local");
                if(uuid.equals(lockValue)){
                    //删除锁之前发生key过期：下一个线程的锁可能被删除
                    //再极端的条件下就是判断锁时发生宕机 导致死锁 使用LUA解决
                    stringRedisTemplate.delete("lock");
                }
            }
            return categoryJsonFromDb;
        }else{
            //调用自己 尝试获取锁 自旋
            return getCategoryJsonWithRedisLock();
        }
    }

    private List<CategoryEntity> getParent_id(List<CategoryEntity> categoryEntity,Integer parentId) {
        List<CategoryEntity> collect = categoryEntity.stream().filter(item -> {
            return item.getParentId().equals(parentId);
        }).collect(Collectors.toList());
        return collect;
    }


    /**
     * 递归收集菜单id
     * @param categoryId
     * @param paths
     * @return  [560, 559, 558]
     */
    private List<Long> findParentPath(Integer categoryId, List<Long> paths) {
        //收集当前分类id到集合中
        paths.add(categoryId.longValue());

        CategoryEntity categoryEntity = this.getById(categoryId);
        if (categoryEntity.getParentId() != 0){
            findParentPath( categoryEntity.getParentId(), paths);
        }
        return paths;
    }


    /**
     * 递归查找指定分类的所有子分类( 所有菜单的子菜单)
     * @param currentMenu
     * @param entities
     * @return
     */
    private List<CategoryEntity> getChildrens(CategoryEntity currentMenu, List<CategoryEntity> entities) {
        List<CategoryEntity> childrens = entities.stream().filter(
            //过滤出 当前菜单的所有匹配的子菜单 currentMenu.id == categoryEntity.parentId
            categoryEntity -> currentMenu.getId().equals(categoryEntity.getParentId())
        ).map((menu)->{
            //找到子分类
            menu.setChildrens( getChildrens(menu, entities) );
            return menu;
        }).collect(Collectors.toList());

        return childrens;
    }

}