引入 jar 包：
```
    <dependency>
        <groupId>io.searchbox</groupId>
        <artifactId>jest</artifactId>
        <version>5.3.3</version>
    </dependency>
```
### 初始化
```
JestClient client;

JestClientFactory factory = new JestClientFactory();
    factory.setHttpClientConfig(new HttpClientConfig
            .Builder(Arrays.asList(urls))
            .multiThreaded(true)
            .defaultMaxTotalConnectionPerRoute(Integer.valueOf(maxTotal))
            .maxTotalConnection(Integer.valueOf(perTotal))
            .build());
    this.client = factory.getObject();
```
### 基本操作
```$xslt
//创建索引
client.execute(new CreateIndex.Builder(index).build());

//创建 type
PutMapping.Builder builder = new PutMapping.Builder(index, type, mapping);
JestResult jestResult = client.execute(builder.build());
if (!jestResult.isSucceeded()) {
    //失败
}

//删除索引
client.execute(new DeleteIndex.Builder(index).build());

```
### 操作
```$xslt
    /**
     * 获取对象
     * 可以加路由：setParameter(Parameters.ROUTING, "")
     */
    public <T> T getData(String index, String type, String _id, Class<T> clazz) {
        Get get = new Get.Builder(index, _id).type(type).build();
        
        JestResult result = client.execute(get);
        if (result.isSucceeded()) {
            return result.getSourceAsObject(clazz);
        }
    }
    
    /**
     * 写入数据
     * 对象里要有 id
     */
    public <T> void insertData(String index, String type, List<T> list) {
        Index action = new Index.Builder(list).index(index).type(type).build();
        try {
            client.execute(action);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 功能描述：插入数据
     * @entity 里面最好有@JestId id，要不然会自动生成一个
     */
    public <T> void insertData(String index, String type, T entity) {
        Index action = new Index.Builder(entity).index(index).type(type).build();
        try {
            client.execute(action);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 功能描述：批量插入数据
     * @param index    索引名
     * @param type     类型
     * @param dataList 批量数据
     */
    public <T> void bulkInsertData(String index, String type, List<T> dataList) throws Exception{

        List<Index> actions = new ArrayList<>();
        assert dataList != null;
        dataList.forEach(item -> {
            actions.add(new Index.Builder(item).build());
        });

        Bulk bulk = new Bulk.Builder()
                .defaultIndex(index)
                .defaultType(type)
                .addAction(actions)
                .build();

        client.execute(bulk);
    }
```
### 异步操作
```$xslt
    //异步写入
    /**
     * 功能描述：插入数据
     *
     * @param index 索引名
     * @param type  类型
     * @entity 里面最好有 id，要不然会自动生成一个
     */
    public <T> void insertDataAsync(String index, String type, T entity) {
        Index action = new Index.Builder(entity).index(index).type(type).build();

        client.executeAsync(action, new JestResultHandler<JestResult>() {
            @Override
            public void completed(JestResult result) {
                logger.debug("insert success");
            }

            @Override
            public void failed(Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    /**
     * 功能描述：异步更新数据
     */
    public <T> void updateDataAsync(String index, String type, String _id, T entity) {

        client.executeAsync(new Update.Builder(entity).id(_id)
                .index(index)
                .type(type)
                .build(), new JestResultHandler<JestResult>() {
            @Override
            public void completed(JestResult result) {
                logger.debug("insert success");
            }

            @Override
            public void failed(Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    /**
     * 功能描述：异步批量插入数据
     */
    public <T> void bulkInsertData(String index, String type, List<T> dataList) throws Exception{

        List<Index> actions = new ArrayList<>();
        assert dataList != null;
        dataList.forEach(item -> {
            actions.add(new Index.Builder(item).build());
        });

        Bulk bulk = new Bulk.Builder()
                .defaultIndex(index)
                .defaultType(type)
                .addAction(actions)
                .build();

        client.execute(bulk);
    }
    
    
```
### 搜索
如果使用 sourcebuilder，需要引入 es 的包
```$xslt
<dependency>
    <groupId>org.elasticsearch</groupId>
    <artifactId>elasticsearch</artifactId>
    <version>5.5.3</version>
    <scope>compile</scope>
</dependency>
```
搜索代码：
```$xslt
    /**
     * 功能描述：搜索
     * @param index       索引名
     * @param type        类型
     * @param constructor 查询构造
     */
    public <T> Page<T> search(String index, String type, Class<T> clazz, ESQueryBuilderConstructor constructor) {
        Page<T> page = new Page<>();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //sourceBuilder.query(QueryBuilders.matchAllQuery());
        sourceBuilder.query(constructor.listBuilders());

        sourceBuilder.from((constructor.getFrom()));
        sourceBuilder.size(constructor.getSize() > MAX_SIZE ? MAX_SIZE : constructor.getSize());

        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        //增加多个值排序
        if (constructor.getSorts() != null) {
            constructor.getSorts().forEach((key, value) -> {
                sourceBuilder.sort(SortBuilders.fieldSort(key).order(value));
            });
        }

        //设置需要返回的属性
        if (constructor.getIncludeFields() != null || constructor.getExcludeFields() != null) {
            sourceBuilder.fetchSource(constructor.getIncludeFields(), constructor.getExcludeFields());
        }

        logger.debug("查询条件:{}", sourceBuilder.toString());
        //System.out.println("查询条件：" + sourceBuilder.toString());

        Search search = new Search.Builder(sourceBuilder.toString())
                .addIndex(index)
                .addType(type).setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .build();

        SearchResult result = null;
        try {
            result = client.execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.debug("查询结果:{}", result.getJsonString());
        List<T> list = new ArrayList<>();

        result.getHits(clazz).forEach(item -> {
            list.add(item.source);
        });

        page.setList(list).setCount(result.getTotal());

        return page;
    }
```
### 统计
```$xslt
public <T> Map<Object, Object> statSearch(String index, String type, ESQueryBuilderConstructor constructor, String groupBy) {
    Map<Object, Object> map = new HashedMap();
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    if (constructor != null) {
        sourceBuilder.query(constructor.listBuilders());
    } else {
        sourceBuilder.query(QueryBuilders.matchAllQuery());
    }

    sourceBuilder.from((constructor.getFrom()));
    sourceBuilder.size(constructor.getSize() > MAX_SIZE ? MAX_SIZE : constructor.getSize());

    sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

    sourceBuilder.aggregation(AggregationBuilders.terms("agg").field(groupBy));

    //不需要 source
    sourceBuilder.fetchSource(false);

    Search search = new Search.Builder(sourceBuilder.toString())
            .addIndex(index)
            .addType(type).setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
            .build();
    SearchResult result = null;
    try {
        result = client.execute(search);
    } catch (IOException e) {
        e.printStackTrace();
    }
    System.out.println("返回的聚合：" + result.getJsonString());

    result.getAggregations().getTermsAggregation("agg").getBuckets().forEach(item -> {
        map.put(item.getKey(), item.getCount());
    });

    return map;
}

```
> 总结：使用 jest 的 client 还是比较方便的，并且使用@JestId 注解，直接将对象的 id 作为 doc 的 id。性能上也不错，经本人测试 1 秒 5k-6k 数据写入没问题