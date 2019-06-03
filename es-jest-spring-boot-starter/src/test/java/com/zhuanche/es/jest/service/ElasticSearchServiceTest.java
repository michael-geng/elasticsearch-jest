package com.zhuanche.es.jest.service;

import com.alibaba.fastjson.JSON;
import com.zhuanche.es.jest.*;
import com.zhuanche.es.jest.bean.WorkSheet;
import io.searchbox.core.SearchResult;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCountAggregationBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= ApplicationTest.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ElasticSearchServiceTest {

    @Autowired
    private ElasticSearchService searchService;

//    @Autowired
//    private WorkSheetService workSheetService;

    private String index = "index-workorder";
    private String type = "worksheet";

//        @Test
    public void deleteIndex() throws Exception{
        //删除
        this.searchService.deleteIndex(index);
        //this.createIndex();
        //this.testInsertData();
    }
    @Test
    public void createIndex() throws Exception{
        //XContentBuilder builder= XContentFactory.jsonBuilder();
        String mapping="{\"worksheet\": {\"properties\": {\"call_record_id\": {\"type\": \"long\"}, \"city_id\": {\"type\": \"long\"}, \"commit_user_id\": {\"type\": \"keyword\"}, \"commit_user_name\": {\"type\": \"keyword\"}, \"confirm_sheet_type_four\": {\"type\": \"long\"}, \"confirm_sheet_type_one\": {\"type\": \"long\"}, \"confirm_sheet_type_three\": {\"type\": \"long\"}, \"confirm_sheet_type_two\": {\"type\": \"long\"}, \"contact\": {\"type\": \"keyword\"}, \"create_date\": {\"type\": \"date\"}, \"current_deal_user_id\": {\"type\": \"keyword\"}, \"current_deal_user_name\": {\"type\": \"keyword\"}, \"current_status\": {\"type\": \"long\"}, \"dept_id\": {\"type\": \"long\"}, \"driver_id\": {\"type\": \"long\"}, \"driver_name\": {\"type\": \"keyword\"}, \"driver_phone\": {\"type\": \"keyword\"}, \"duty_dept\": {\"type\": \"long\"}, \"handle_time\": {\"type\": \"long\"}, \"id\": {\"type\": \"long\"}, \"license_plates\": {\"type\": \"keyword\"}, \"memo\": {\"type\": \"keyword\"}, \"order_no\": {\"type\": \"keyword\"}, \"order_type\": {\"type\": \"long\"}, \"reopen_times\": {\"type\": \"long\"}, \"rider_name\": {\"type\": \"keyword\"}, \"rider_phone\": {\"type\": \"keyword\"}, \"service_type_id\": {\"type\": \"long\"}, \"sheet_classify\": {\"type\": \"long\"}, \"sheet_priority\": {\"type\": \"long\"}, \"sheet_source\": {\"type\": \"long\"}, \"sheet_tag\": {\"type\": \"long\"}, \"sheet_tag_sort\": {\"type\": \"long\"}, \"sheet_type_four\": {\"type\": \"long\"}, \"sheet_type_one\": {\"type\": \"long\"}, \"sheet_type_three\": {\"type\": \"long\"}, \"sheet_type_two\": {\"type\": \"long\"}, \"update_date\": {\"type\": \"date\"}, \"urge_times\": {\"type\": \"long\"}, \"weight\": {\"type\": \"long\"}, \"work_sheet_no\": {\"type\": \"keyword\"} } } } ";
        this.searchService.createIndex(index);
        this.searchService.createIndex(index, type, mapping);
    }

    @Test
    public void testInsertData() throws Exception{

        List<Map<String, Object>> list = null;
        System.out.println("从 db 查询出来的数据:" + JSON.toJSONString(list));
        //按照mapping 创建 index
        List<WorkSheet> dataList = new ArrayList<>();

        list.forEach(item ->{
            WorkSheet entity = new WorkSheet();
            entity.setId(Long.valueOf(item.get("id").toString()));
            entity.setWorkSheetNo(item.get("work_sheet_no").toString());
            entity.setSheetTypeTwo(item.get("sheet_type_two")!=null? Integer.valueOf(item.get("sheet_type_two").toString()) : 0);
            entity.setAttentionUserIds(item.get("attention_user_ids")!=null ? item.get("attention_user_ids").toString(): "");
            entity.setCreateDate(new Date(System.currentTimeMillis()));
            dataList.add(entity);
        });

        this.searchService.bulkInsertData(index, type, dataList);
        //this.searchService.insertData(index, type, list);

    }

    @Test
    public void testUpdate() throws Exception{
        this.searchService.deleteData(index, type, "101328");

        WorkSheet workSheet =  this.searchService.getData(index, type, "101328", WorkSheet.class);
        workSheet.setUpdateDate(new Date(System.currentTimeMillis()));
        workSheet.setUrgeTimes(2);
        workSheet.setReopenTimes(3);
        System.out.println(JSON.toJSONString(workSheet));
        this.searchService.updateData(index, type, "101328", workSheet);
    }

    @Test
    public void testIndexExist(){
        System.out.println("索引是否存在：" + this.searchService.indexExist( index));
    }

    @Test
    public void testAddAliases() throws Exception{
        List<String> list = Arrays.asList(index);
        this.searchService.addAlias(list , "index-workorder_alias");
        this.testGetIndexAliases();
    }

    @Test
    public void testGetIndexAliases(){
        this.searchService.getIndexAliases(index);
    }

    @Test
    public void testGetObject(){
        String id = "AWq5jSn0MCnKwL3pKsoK";
        WorkSheet entity = this.searchService.getData(index, type, id, WorkSheet.class);
        System.out.println("testGetObject 返回值：" + entity.toString());
    }

    @Test
    public void testSearch(){
        Page<WorkSheet> page = new Page<>();

        ESQueryBuilderConstructor constructor = new ESQueryBuilderConstructor();
        //constructor.must(new ESQueryBuilders().term("sheetSource", "3"));//单个值匹配   OK
        constructor.must(new ESQueryBuilders().terms("deptId", Arrays.asList(94,93)));//多值匹配  OK

        //constructor.must(new ESQueryBuilders().term("licensePlates", "京BJM00测"));// ok

        //constructor.should(new ESQueryBuilders().term("commitUserName", "张威")
        //.term("contact", "15801098325"));//OK 精确匹配
        //constructor.must(new ESQueryBuilders().range("sheet_source", 0, 6 ));
//        constructor.must(new ESQueryBuilders().prefixString("licensePlates1","京BJM00"));
        //constructor.must(new ESQueryBuilders().terms("deptId", Arrays.asList(14, 9)));
        //date
        DateTime start = new DateTime().plusDays(-35);

        System.out.println(start.toString("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
        System.out.println(new DateTime().toString("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
        constructor.must(new ESQueryBuilders().range("createDate", start.toDate().getTime(), new DateTime().toDate().getTime()));

        constructor.setFrom((page.getPageNo()-1) * page.getPageSize());
        constructor.setSize(100);
        constructor.addSort("id", SortOrder.DESC);

        page = this.searchService.search(index, type, WorkSheet.class, constructor);
        page.getList().forEach(item ->{
            System.out.println(item.getId());
        });
        System.out.println("查询返回结果：" + JSON.toJSONString(page));
    }


    @Test
    public void testSearchAndIncludeFields(){
        Page<WorkSheet> page = new Page<>();

        ESQueryBuilderConstructor constructor = new ESQueryBuilderConstructor();

        constructor.setFrom((page.getPageNo()-1) * page.getPageSize());
        constructor.setSize(page.getPageSize());

        constructor.setIncludeFields(new String[]{"id"});

        page = this.searchService.search(index, type, WorkSheet.class, constructor);
        System.out.println("查询返回结果：" + JSON.toJSONString(page));
    }

    @Test
    public void testStat() throws Exception{
        ESQueryBuilderConstructor constructor = new ESQueryBuilderConstructor();
        constructor.setFrom(0);
        constructor.setSize(0);
        Map<String, Object> data = this.searchService.statSearch(index, type, constructor, "sheetTypeOne");

        System.out.println("返回结果:" + JSON.toJSONString(data));
    }

    @Test
    public void testStat1() throws Exception{
        //统计待办个数
        ESQueryBuilderConstructor constructor = new ESQueryBuilderConstructor();
        constructor.setFrom(0);
        constructor.setSize(0);
        constructor.must(new ESQueryBuilders().term("currentStatus", 1).term("currentDealUserId", "1500"));
        ValueCountAggregationBuilder aggregationBuilder = AggregationBuilders.count("count").field("id");
        //.offset("+8h");

        SearchResult data = this.searchService.stat(index, type, constructor, aggregationBuilder);
        System.out.println("返回结果:" + data.getJsonString());
        if (data.isSucceeded()){
            data.getAggregations().getValueCountAggregation("count").getValueCount();
            System.out.println("获取数量" + data.getAggregations().getValueCountAggregation("count").getValueCount());
        }


    }

    @Test
    public void testStat2() throws Exception{
        //按照 createDate 分组 按天分组
        ESQueryBuilderConstructor constructor = new ESQueryBuilderConstructor();
        constructor.setFrom(0);
        constructor.setSize(0);
        //加上时间过滤
        DateTime start = new DateTime().plusDays(-10);
        constructor.must(new ESQueryBuilders().range("createDate", start.toDate().getTime(), new DateTime().toDate().getTime()));

        DateHistogramAggregationBuilder aggregationBuilder = AggregationBuilders.dateHistogram("dateagg")
                .field("createDate")
                .dateHistogramInterval(DateHistogramInterval.DAY)
                .timeZone(DateTimeZone.forID("Asia/Shanghai"));
                //.offset("+8h");

        Map<String, Object> data = this.searchService.statSearch(index, type, constructor, aggregationBuilder);
        System.out.println("返回结果:" + JSON.toJSONString(data));

        data.entrySet().stream().sorted(Comparator.comparing(entry -> entry.getKey() ) )
                .forEach(entry -> {
                    System.out.println(new DateTime(new Date(Long.valueOf(entry.getKey().toString()))).toString("yyyy-MM-dd") + "---" + entry.getValue());
        });

    }

    @Test
    public void testStat3() throws Exception{
        //多级别统计,这种情况，只能返回 result 自己处理了
        ESQueryBuilderConstructor constructor = new ESQueryBuilderConstructor();
        constructor.setFrom(0);
        constructor.setSize(0);
        //加上时间过滤
        DateTime start = new DateTime().plusDays(-10);
        constructor.must(new ESQueryBuilders().range("createDate", start.toDate().getTime(), new DateTime().toDate().getTime()));

        TermsAggregationBuilder builder = AggregationBuilders.terms("agg").field("sheetTypeOne").subAggregation(AggregationBuilders.terms("agg").field("sheetTypeTwo"));
        Map<String, Object> data = this.searchService.statSearch(index, type, constructor, builder);
        System.out.println("返回结果:" + JSON.toJSONString(data));

        data.entrySet()
                .forEach(entry -> {
                    System.out.println("两级分组统计返回：" + entry.getKey() + "---" + JSON.toJSONString(entry.getValue()));
                });

    }

}
