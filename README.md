---
基于 jest 封装的 elasticsearch 的 HTTP client <br>
jest 地址：https://github.com/searchbox-io/Jest
---

### spring boot 包引用
```$xslt
<dependency>
    <groupId>com.zhuanche.es.jest</groupId>
    <artifactId>sq-es-spring-boot-starter</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```
* 配置
```$xslt
es.url: http://host:9200
es.max-total: 100
es.per-total: 100
```
* 代码
```$xslt
    //注入 service
    @Autowired
    private ElasticSearchService searchService;
    
    //创建索引
    String mapping="{\"worksheet\": {\"properties\": {\"call_record_id\": {\"type\": \"long\"}, \"city_id\": {\"type\": \"long\"}, \"commit_user_id\": {\"type\": \"keyword\"}, \"commit_user_name\": {\"type\": \"keyword\"}, \"confirm_sheet_type_four\": {\"type\": \"long\"}, \"confirm_sheet_type_one\": {\"type\": \"long\"}, \"confirm_sheet_type_three\": {\"type\": \"long\"}, \"confirm_sheet_type_two\": {\"type\": \"long\"}, \"contact\": {\"type\": \"keyword\"}, \"create_date\": {\"type\": \"date\"}, \"current_deal_user_id\": {\"type\": \"keyword\"}, \"current_deal_user_name\": {\"type\": \"keyword\"}, \"current_status\": {\"type\": \"long\"}, \"dept_id\": {\"type\": \"long\"}, \"driver_id\": {\"type\": \"long\"}, \"driver_name\": {\"type\": \"keyword\"}, \"driver_phone\": {\"type\": \"keyword\"}, \"duty_dept\": {\"type\": \"long\"}, \"handle_time\": {\"type\": \"long\"}, \"id\": {\"type\": \"long\"}, \"license_plates\": {\"type\": \"keyword\"}, \"memo\": {\"type\": \"keyword\"}, \"order_no\": {\"type\": \"keyword\"}, \"order_type\": {\"type\": \"long\"}, \"reopen_times\": {\"type\": \"long\"}, \"rider_name\": {\"type\": \"keyword\"}, \"rider_phone\": {\"type\": \"keyword\"}, \"service_type_id\": {\"type\": \"long\"}, \"sheet_classify\": {\"type\": \"long\"}, \"sheet_priority\": {\"type\": \"long\"}, \"sheet_source\": {\"type\": \"long\"}, \"sheet_tag\": {\"type\": \"long\"}, \"sheet_tag_sort\": {\"type\": \"long\"}, \"sheet_type_four\": {\"type\": \"long\"}, \"sheet_type_one\": {\"type\": \"long\"}, \"sheet_type_three\": {\"type\": \"long\"}, \"sheet_type_two\": {\"type\": \"long\"}, \"update_date\": {\"type\": \"date\"}, \"urge_times\": {\"type\": \"long\"}, \"weight\": {\"type\": \"long\"}, \"work_sheet_no\": {\"type\": \"keyword\"} } } } ";
    this.searchService.createIndex(index);
    this.searchService.createIndex(index, type, mapping);
    
    //创建别名
    List<String> list = Arrays.asList(index);
    this.searchService.addAlias(list , "index-alias");
    
    //写入数据
    List<WorkSheet> dataList = new ArrayList<>();
    
    list.forEach(item ->{
        WorkSheet entity = new WorkSheet();
        entity.setId(Long.valueOf(item.get("id").toString()));
        dataList.add(entity);
    });

    searchService.bulkInsertData(index, type, dataList);
    
    //异步写入数据
    searchService.bulkInsertDataAsync(index, type, dataList);
    
    
```

* 搜索代码
```$xslt
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
    
```

### 使用基础 jar 包
```$xslt
    <dependency>
        <groupId>com.zhuanche.es.jest</groupId>
        <artifactId>es-jest-service</artifactId>
        <version>0.0.2-SNAPSHOT</version>
        <exclusions>
            <exclusion>
                <groupId>io.searchbox</groupId>
                <artifactId>jest</artifactId>
            </exclusion>

            <exclusion>
                <groupId>org.elasticsearch</groupId>
                <artifactId>elasticsearch</artifactId>
            </exclusion>

        </exclusions>
    </dependency>
```
* 使用 spring配置
```$xslt
<bean id="elasticSearchService" class="com.zhuanche.es.jest.ElasticSearchService">
        <constructor-arg index="0" value="http://host:9200" />
        <constructor-arg index="1" value="100"/>
        <constructor-arg index="2" value="100"/>
    </bean>
```

