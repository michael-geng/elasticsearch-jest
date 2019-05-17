package com.zhuanche.es.jest.service;

import com.alibaba.fastjson.JSON;
import com.zhuanche.es.jest.*;
import com.zhuanche.es.jest.bean.WorkSheet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

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

//    @Test
    public void deleteIndex() throws Exception{
        //删除
        this.searchService.deleteIndex(index);
        this.createIndex();
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
    public void testIndexExist(){
        System.out.println("索引是否存在：" + this.searchService.indexExist( index));
    }

    @Test
    public void testAddAliases() throws Exception{
        List<String> list = Arrays.asList(index);
        this.searchService.addAlias(list , "index-alias");
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
        //constructor.must(new ESQueryBuilders().terms("sheetSource", Arrays.asList(3,6,4)));//多值匹配  OK

        //constructor.must(new ESQueryBuilders().queryString("高德"));// ok
        //constructor.must(new ESQueryBuilders().fuzzy("commitUserName", "刘佳*")); // OK
        //constructor.must(new ESQueryBuilders().term("commitUserName", "刘佳星"));//OK 精确匹配
        //constructor.must(new ESQueryBuilders().range("sheet_source", 0, 6 ));

        //constructor.must(new ESQueryBuilders().terms("deptId", Arrays.asList(14, 9)));

        constructor.setFrom((page.getPageNo()-1) * page.getPageSize());
        constructor.setSize(page.getPageSize());

        page = this.searchService.search(index, type, WorkSheet.class, constructor);
        System.out.println("查询返回结果：" + JSON.toJSONString(page));
    }

    @Test
    public void testStat(){
        ESQueryBuilderConstructor constructor = new ESQueryBuilderConstructor();
        constructor.setFrom(0);
        constructor.setSize(100);
        Map<Object, Object> data = this.searchService.statSearch(index, type, constructor, "work_sheet_no");

        System.out.println("返回结果:" + JSON.toJSONString(data));
    }
}
