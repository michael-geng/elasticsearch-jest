package com.zhuanche.es.jest;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.JestResultHandler;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.*;
import io.searchbox.indices.*;
import io.searchbox.indices.aliases.AddAliasMapping;
import io.searchbox.indices.aliases.GetAliases;
import io.searchbox.indices.aliases.ModifyAliases;
import io.searchbox.indices.mapping.PutMapping;
import io.searchbox.indices.settings.UpdateSettings;
import io.searchbox.params.SearchType;
import org.apache.commons.collections4.map.HashedMap;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ElasticSearchService {
    private final Logger logger = LoggerFactory.getLogger(ElasticSearchService.class);

    //查询返回的最大数量
    private final static int MAX_SIZE = 1000;

    private JestClient client;

    /**
     * es 的相关属性
     */
    private String esUrl;
    private Integer maxTotal;
    private Integer perTotal;

    public ElasticSearchService(String esUrl, Integer maxTotal, Integer perTotal) {
        this.esUrl = esUrl;
        this.maxTotal = maxTotal;
        this.perTotal = perTotal;

        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder(esUrl)
                .multiThreaded(true)
                .defaultMaxTotalConnectionPerRoute(Integer.valueOf(maxTotal))
                .maxTotalConnection(Integer.valueOf(perTotal))
                .build());
        this.client = factory.getObject();

    }

    public ElasticSearchService(JestClient client) {
        this.client = client;
    }

    /**
     * 功能描述：新建索引
     *
     * @param index 索引名
     */
    public void createIndex(String index) {
        try {
            client.execute(new CreateIndex.Builder(index).build());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建索引，指定type 的 mapping
     *
     * @param index
     * @param type
     * @param mapping
     */
    public void createIndex(String index, String type, String mapping) throws Exception{
        PutMapping.Builder builder = new PutMapping.Builder(index, type, mapping);
        JestResult jestResult = client.execute(builder.build());
        if (!jestResult.isSucceeded()) {
            throw new ESServiceException("create index type error: " + jestResult.getErrorMessage());
        }
    }

    /**
     * 功能描述：删除索引
     *
     * @param index 索引名
     */
    public void deleteIndex(String index) throws Exception{
        client.execute(new DeleteIndex.Builder(index).build());
    }

    /**
     * 功能描述：验证索引是否存在
     *
     * @param index 索引名
     */
    public boolean indexExist(String index) {
        try {
            JestResult result = client.execute(new IndicesExists.Builder(Arrays.asList(index)).build());
            return result.isSucceeded();
        } catch (IOException e) {
            throw new ESServiceException("check if exist index error: " + e.getMessage());
        }
    }

    /**
     * 获取对象
     * .setParameter(Parameters.ROUTING, "")
     */
    public <T> T getData(String index, String type, String _id, Class<T> clazz) {
        Get get = new Get.Builder(index, _id).type(type).build();
        try {
            JestResult result = client.execute(get);
            if (result.isSucceeded()) {
                return result.getSourceAsObject(clazz);
            }
        } catch (IOException e) {
            //e.printStackTrace();
            throw new ESServiceException("get data error: " + e.getMessage());
        } catch (Exception e) {
            throw new ESServiceException("get data error: " + e.getMessage());
        }

        return null;
    }

    /**
     * 获取json数据格式
     */
    public String getData(String index, String type, String _id) {
        Get get = new Get.Builder(index, _id).type(type).build();

        try {
            JestResult result = client.execute(get);
            return result.getJsonString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 功能描述：插入数据
     *
     * @param index 索引名
     * @param type  类型
     * @param json  数据
     */
    public void insertData(String index, String type, String json) {
        Index action = new Index.Builder(json).index(index).type(type).build();
        try {
            client.execute(action);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ESServiceException("insert error: " + e.getMessage());
        }
    }

    /**
     * 批量写入数据
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
     *
     * @param index 索引名
     * @param type  类型
     * @param _id   数据id
     * @param json  数据
     */
    public void insertData(String index, String type, String _id, String json) {
        Index action = new Index.Builder(json).index(index).type(type).id(_id).build();
        try {
            client.execute(action);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 功能描述：插入数据
     *
     * @param index 索引名
     * @param type  类型
     * @entity 里面最好有 id，要不然会自动生成一个
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
                logger.error("insert error:{}", e.getMessage());
            }
        });

    }

    /**
     * 功能描述：更新数据
     *
     * @param index 索引名
     * @param type  类型
     * @param _id   数据id
     */
    public <T> void updateData(String index, String type, String _id, T entity) {
        try {
            client.execute(new Update.Builder(entity).id(_id)
                    .index(index)
                    .type(type)
                    .build());
        } catch (IOException e) {
            throw new ESServiceException("index doc update exception:" + e.getMessage());
        }
    }

    /**
     * 功能描述：更新数据
     *
     * @param index 索引名
     * @param type  类型
     * @param _id   数据id
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
                logger.error("update error:{}", e.getMessage());
            }
        });

    }

    /**
     * 功能描述：更新数据
     *
     * @param index 索引名
     * @param type  类型
     * @param _id   数据id
     * @param json  数据
     */
    public void updateData(String index, String type, String _id, String json)  {
        try {
            client.execute(new Update.Builder(json).id(_id)
                    .index(index)
                    .type(type)
                    .build());
        } catch (IOException e) {
            e.printStackTrace();
            throw new ESServiceException("index doc update exception:" + e.getMessage());
        }
    }

    /**
     * 功能描述：删除数据
     *
     * @param index 索引名
     * @param type  类型
     * @param _id   数据id
     */
    public void deleteData(String index, String type, String _id) {
        try {
            client.execute(new Delete.Builder(_id)
                    .index(index)
                    .type(type)
                    .build());
        } catch (IOException e) {
            throw new ESServiceException("delete data exception:" + e.getMessage());
        }

    }

    /**
     * 功能描述：批量插入数据
     * @param index 索引名
     * @param type  类型
     * @param data  (_id 主键, json 数据)
     */
    public void bulkInsertData(String index, String type, Map<String, String> data) throws Exception{

        List<Index> indexes = new ArrayList<>();

        data.forEach((key, value) -> {
            indexes.add(new Index.Builder(value).id(key).build());

        });

        Bulk bulk = new Bulk.Builder()
                .defaultIndex(index)
                .defaultType(type)
                .addAction(indexes)
                .build();

        client.execute(bulk);
    }

    /**
     * 功能描述：批量插入数据
     *
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

    /**
     * 获取索引对应的别名
     *
     * @param index
     * @return
     */
    public boolean getIndexAliases(String index) {
        try {
            JestResult jestResult = client.execute(new GetAliases.Builder().addIndex(index).build());
            System.out.println(jestResult.getJsonString());
            if (jestResult != null) {
                return jestResult.isSucceeded();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 添加索引别名
     *
     * @param index
     * @param alias
     */
    public void addAlias(List<String> index, String alias) throws Exception{
        AddAliasMapping build = new AddAliasMapping.Builder(index, alias).build();
        JestResult jestResult = client.execute(new ModifyAliases.Builder(build).build());
        if (!jestResult.isSucceeded()) {
            throw new ESServiceException("add alias error" + jestResult.getErrorMessage());
        }
    }

    /**
     * 更改索引index设置setting
     *
     * @param index
     * @return
     */
    public boolean updateIndexSettings(String index) {
        String source;
        XContentBuilder mapBuilder = null;
        try {
            mapBuilder = XContentFactory.jsonBuilder();
            mapBuilder.startObject().startObject("index").field("max_result_window", "1000000").endObject().endObject();
            source = mapBuilder.string();
            JestResult jestResult = client.execute(new UpdateSettings.Builder(source).build());
            System.out.println(jestResult.getJsonString());
            if (jestResult != null) {
                return jestResult.isSucceeded();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 索引优化
     */
    public void optimizeIndex() {
        Optimize optimize = new Optimize.Builder().build();
        client.executeAsync(optimize, new JestResultHandler<JestResult>() {
            public void completed(JestResult jestResult) {
                System.out.println("optimizeIndex result:{}" + jestResult.isSucceeded());
            }

            public void failed(Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 清理缓存
     */
    public void clearCache() {
        try {
            ClearCache clearCache = new ClearCache.Builder().build();
            client.execute(clearCache);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 功能描述：查询
     *
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

        //属性
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


    /**
     * 功能描述：统计查询
     *
     * @param index       索引名
     * @param type        类型
     * @param constructor 查询构造
     * @param groupBy     统计分组字段
     */
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

        //增加多个值排序
        if (constructor.getSorts() != null) {
            constructor.getSorts().forEach((key, value) -> {
                sourceBuilder.sort(SortBuilders.fieldSort(key).order(value));
            });
        }
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

    /**
     * 功能描述：统计查询
     *
     * @param index       索引名
     * @param type        类型
     * @param constructor 查询构造
     * @param agg         自定义计算
     */
    public Map<Object, Object> statSearch(String index, String type, ESQueryBuilderConstructor constructor, AggregationBuilder agg) {

        if (agg == null) {
            throw new ESServiceException("aggregationBuilder 不能为空");
        }
        Map<Object, Object> map = new HashedMap();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        if (constructor != null) {
            sourceBuilder.query(constructor.listBuilders());
        } else {
            sourceBuilder.query(QueryBuilders.matchAllQuery());
        }

        sourceBuilder.from((constructor.getFrom()));
        sourceBuilder.size(constructor.getSize() > 0 ? constructor.getSize() : MAX_SIZE);

        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        //增加多个值排序
        if (constructor.getSorts() != null) {
            constructor.getSorts().forEach((key, value) -> {
                sourceBuilder.sort(SortBuilders.fieldSort(key).order(value));
            });
        }

        sourceBuilder.aggregation(agg);

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

        result.getAggregations().getTermsAggregation("agg").getBuckets().forEach(item -> {
            map.put(item.getKey(), item.getCount());
        });

        return map;
    }


}