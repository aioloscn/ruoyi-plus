package com.ruoyi.spider.service.impl;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.hutool.core.util.ObjectUtil;
import com.google.common.collect.Maps;
import com.ruoyi.cms.mapper.BookDao;
import com.ruoyi.cms.service.BaseService;
import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.ICallBack;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.spider.backend.SpiderBackendService;
import com.ruoyi.spider.config.SpiderConstants;
import com.ruoyi.spider.mapper.SpiderConfigMapper;
import com.ruoyi.spider.mapper.SpiderFieldMapper;
import com.ruoyi.spider.util.IdGeneratorSnowflake;
import com.ruoyi.system.domain.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import com.ruoyi.spider.mapper.SpiderMissionMapper;
import com.ruoyi.spider.domain.SpiderMission;
import com.ruoyi.spider.service.ISpiderMissionService;
import com.ruoyi.common.core.text.Convert;

/**
 * 爬虫任务Service业务层处理
 * 
 * @author wujiyue
 * @date 2019-11-11
 */
@Service
public class SpiderMissionServiceImpl extends BaseService implements ISpiderMissionService
{
    @Autowired
    private SpiderMissionMapper spiderMissionMapper;
    @Autowired
    private SpiderConfigMapper spiderConfigMapper;

    @Autowired
    private SpiderFieldMapper spiderFieldMapper;

    @Autowired
    private BookDao bookDao;

    @Autowired
    private IdGeneratorSnowflake idWorker;

    @Autowired
    private StringRedisTemplate redis;

    /**
     * 查询爬虫任务
     * 
     * @param missionId 爬虫任务ID
     * @return 爬虫任务
     */
    @Override
    public SpiderMission selectSpiderMissionById(Long missionId)
    {
        return spiderMissionMapper.selectSpiderMissionById(missionId);
    }

    /**
     * 查询爬虫任务列表
     * 
     * @param spiderMission 爬虫任务
     * @return 爬虫任务
     */
    @Override
    @DataScope(deptAlias = "a",userAlias = "a")
    public List<SpiderMission> selectSpiderMissionList(SpiderMission spiderMission)
    {
        return spiderMissionMapper.selectSpiderMissionList(spiderMission);
    }

    /**
     * 新增爬虫任务
     * 
     * @param spiderMission 爬虫任务
     * @return 结果
     */
    @Override
    public int insertSpiderMission(SpiderMission spiderMission)
    {
        SysUser user= ShiroUtils.getSysUser();
        spiderMission.setUserId(user.getUserId().toString());
        spiderMission.setDeptId(user.getDeptId().toString());
        spiderMission.setStatus(SpiderConstants.SPIDER_MISSION_STATUS_WAIT);
        spiderMission.setCreateBy(user.getUserName());
        spiderMission.setCreateTime(DateUtils.getNowDate());
        return spiderMissionMapper.insertSpiderMission(spiderMission);
    }

    /**
     * 修改爬虫任务
     * 
     * @param spiderMission 爬虫任务
     * @return 结果
     */
    @Override
    public int updateSpiderMission(SpiderMission spiderMission)
    {
        return spiderMissionMapper.updateSpiderMission(spiderMission);
    }

    /**
     * 删除爬虫任务对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteSpiderMissionByIds(String ids)
    {
        return spiderMissionMapper.deleteSpiderMissionByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除爬虫任务信息
     * 
     * @param missionId 爬虫任务ID
     * @return 结果
     */
    @Override
    public int deleteSpiderMissionById(Long missionId)
    {
        return spiderMissionMapper.deleteSpiderMissionById(missionId);
    }

    @Override
    public AjaxResult runSpiderMission(String missionId) {
        SpiderMission mission=this.selectSpiderMissionById(Long.valueOf(missionId));

        if(mission!=null){
            if(SpiderConstants.SPIDER_MISSION_STATUS_RUNNING.equals(mission.getStatus())){
                return AjaxResult.error("该任务正在运行中!");
            }
           /* Long configId=mission.getSpiderConfigId();
            SpiderConfig config = spiderConfigMapper.selectSpiderConfigById(configId);
            SpiderField queryForm=new SpiderField();
            queryForm.setConfigId(config.getId());
            List<SpiderField> fields = spiderFieldMapper.selectSpiderFieldList(queryForm);
            config.setFieldsList(fields);
            String entryUrls=mission.getEntryUrls();
            List<String> urls= Lists.newArrayList();
            if(StringUtils.isNotEmpty(entryUrls)){
                String[] arr=entryUrls.split(",");
                for(String s:arr){
                    if(StringUtils.isNotEmpty(s)){
                        urls.add(s);
                    }
                }
            }
            config.setEntryUrlsList(urls);
            config.setExitWay(mission.getExitWay());
            Long c= mission.getExitWayCount();
            if(c==null){
                c=0L;
            }
            config.setCount(Integer.valueOf(c.toString()));
            AbstractProcessor processor=new DefalutProcessor(config,missionId.toString());

            mission.setStatus(SpiderConstants.SPIDER_MISSION_STATUS_RUNNING);
            mission.setStartTime(new Date());
            spiderMissionMapper.updateSpiderMission(mission);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    CopyOnWriteArrayList<LinkedHashMap<String, String>> datas = processor.execute();
                    mission.setEndTime(new Date());
                    mission.setStatus(SpiderConstants.SPIDER_MISSION_STATUS_DONE);
                    mission.setSuccessNum(Long.valueOf(datas.size()));
                    Long count=(mission.getEndTime().getTime()-mission.getStartTime().getTime())/1000;
                    mission.setTimeCost(count.toString());
                    spiderMissionMapper.updateSpiderMission(mission);
                }
            }).start();*/
            SpiderCallBack spiderCallBack= new SpiderCallBack();
            SpiderBackendService spiderBackendService=new SpiderBackendService(missionId,spiderCallBack);
            spiderBackendService.start();
        }
        return AjaxResult.success();
    }

    public class SpiderCallBack implements ICallBack{
        Map params= Maps.newConcurrentMap();
        @Override
        public void onSuccess() {
            System.out.println(">>>>>>>>>>>>>done>>>>>>>>>>>>>>");
            CopyOnWriteArrayList<LinkedHashMap<String, String>> datas=(CopyOnWriteArrayList<LinkedHashMap<String, String>>)params.get("datas");
            System.out.println(">>>>>>>>>>>>>"+datas.size()+">>>>>>>>>>>>>>");

            AtomicInteger i = new AtomicInteger();
            datas.forEach(data -> {
                String name = data.get("name");
                String img = data.get("img").trim();
                String price = data.get("price").trim();
                String original_price = data.get("original-price").trim();
                String discount = data.get("discount").trim();
                String brief = data.get("brief");
                String preface = data.get("preface");
                String excerpt = data.get("excerpt");
                String kind = data.get("kind");

                int classificationIndex = 0;
                int categoryIndex = 0;

                // 把书籍的大小分类提取出来，映射到数字上，并保存到redis
                String regex = "[\u4e00-\u9fa5]+";
                Matcher matcher = Pattern.compile(regex).matcher(kind);
                if (matcher.find()) {
                    if (matcher.group().trim().equals("所属分类")) {}
                    if (matcher.find()) {
                        // 保存一级分类
                        // 查看redis中CATEGORY_ZSET这个key是否已存在
                        String classification = matcher.group();
                        Boolean hasClassificationZSet = redis.opsForZSet().getOperations().hasKey(CATEGORY_ZSET);
                        if (!hasClassificationZSet) {
                            redis.opsForZSet().add(CATEGORY_ZSET, classification, 0);
                        } else {
                            // 查看redis中CATEGORY_ZSET是否有classification这个成员
                            Long classificationExist = redis.opsForZSet().reverseRank(CATEGORY_ZSET, classification);
                            if (ObjectUtil.isNull(classificationExist)) {
                                // 获取值最大的score
                                Set<ZSetOperations.TypedTuple<String>> newestCategoryScore = redis.opsForZSet().reverseRangeByScoreWithScores(CATEGORY_ZSET, 0, MAXIMUM_RANGE_OF_CATEGORY_ZSET);
                                Iterator<ZSetOperations.TypedTuple<String>> iterator = newestCategoryScore.iterator();
                                if (iterator.hasNext()) {
                                    ZSetOperations.TypedTuple<String> next = iterator.next();
                                    Double score = next.getScore();
                                    classificationIndex = score.intValue() + 1;
                                    redis.opsForZSet().add(CATEGORY_ZSET, classification, classificationIndex);
                                }
                            } else {
                                // 此时classificationIndex任然是0，从redis中找到key对应的score
                                Double score = redis.opsForZSet().score(CATEGORY_ZSET, classification);
                                classificationIndex = score.intValue();
                            }
                        }

                        if (matcher.find()) {
                            // 保存二级分类
                            // 查看redis中CATEGORY_ZSET + ":" + classificationIndex是否已存在
                            String category = matcher.group();
                            Boolean hasCategoryZSet = redis.opsForZSet().getOperations().hasKey(CATEGORY_ZSET + ":" + classificationIndex);
                            if (!hasCategoryZSet) {
                                redis.opsForZSet().add(CATEGORY_ZSET + ":" + classificationIndex, category, 0);
                            } else {
                                // 查看redis中CATEGORY_ZSET + ":" + classificationIndex是否有category这个成员
                                Long categoryExist = redis.opsForZSet().reverseRank(CATEGORY_ZSET + ":" + classificationIndex, category);
                                if (ObjectUtil.isNull(categoryExist)) {
                                    // 获取值最大的score
                                    Set<ZSetOperations.TypedTuple<String>> newestCategoryScore = redis.opsForZSet().reverseRangeByScoreWithScores(CATEGORY_ZSET + ":" + classificationIndex, 0, MAXIMUM_RANGE_OF_CATEGORY_ZSET);
                                    Iterator<ZSetOperations.TypedTuple<String>> iterator = newestCategoryScore.iterator();
                                    if (iterator.hasNext()) {
                                        ZSetOperations.TypedTuple<String> next = iterator.next();
                                        Double score = next.getScore();
                                        categoryIndex = score.intValue() + 1;
                                        redis.opsForZSet().add(CATEGORY_ZSET + ":" + classificationIndex, category, categoryIndex);
                                    }
                                }
                            }
                        }
                    }
                }

                String catalog = data.get("catalog");
                String author = data.get("author");
                String publisher = data.get("publisher");
                String other_infor = data.get("other-infor");

                Map<String, Object> map = new HashMap<>();
                map.put("id", idWorker.nextId());
                map.put("name", name);
                map.put("pic_url", img);
                map.put("purchase_price", original_price);
                map.put("selling_price", price);
                map.put("discount", new BigDecimal(discount).multiply(new BigDecimal(100)).intValue());
                map.put("original_price", original_price);
                map.put("brief", brief);
                map.put("preface", preface);
                map.put("excerpt", excerpt);
                map.put("catalog", catalog);
                map.put("author", author);
                map.put("publisher", publisher);
                map.put("other_infor", other_infor);
                map.put("classification", classificationIndex);
                map.put("category", categoryIndex);
                map.put("status", 1);
                map.put("sort", i.incrementAndGet());
                map.put("gmt_create", new Date());
                map.put("gmt_modified", new Date());
                bookDao.insertBook(map);
            });
        }

        @Override
        public void onFail() {

        }

        @Override
        public Map setParams(Map map) {
            params.clear();
            params.putAll(map);
            return params;
        }
    }
}
