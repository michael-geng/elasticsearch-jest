package com.zhuanche.es.jest;

import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查询条件
 */
public class ESQueryBuilderConstructor {

    private int size = Integer.MAX_VALUE;

    private int from = 0;

//    private String asc;
//
//    private String desc;

    private Map<String, SortOrder> sorts;

    //查询条件容器
    private List<ESCriterion> mustCriterions = new ArrayList<>();
    private List<ESCriterion> shouldCriterions = new ArrayList<>();
    private List<ESCriterion> mustNotCriterions = new ArrayList<>();

    private String[] includeFields;
    private String[] excludeFields;

    //构造builder
    public QueryBuilder listBuilders() {
        int count = mustCriterions.size() + shouldCriterions.size() + mustNotCriterions.size();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        QueryBuilder queryBuilder = null;

        if (count >= 1) {
            //must容器
            if (!CollectionUtils.isEmpty(mustCriterions)) {
                for (ESCriterion criterion : mustCriterions) {
                    for (QueryBuilder builder : criterion.listBuilders()) {
                        queryBuilder = boolQueryBuilder.must(builder);
                    }
                }
            }
            //should容器
            if (!CollectionUtils.isEmpty(shouldCriterions)) {
                for (ESCriterion criterion : shouldCriterions) {
                    for (QueryBuilder builder : criterion.listBuilders()) {
                        queryBuilder = boolQueryBuilder.should(builder);
                    }

                }
            }
            //must not 容器
            if (!CollectionUtils.isEmpty(mustNotCriterions)) {
                for (ESCriterion criterion : mustNotCriterions) {
                    for (QueryBuilder builder : criterion.listBuilders()) {
                        queryBuilder = boolQueryBuilder.mustNot(builder);
                    }
                }
            }
            return queryBuilder;
        } else {
            return null;
        }
    }

    /**
     * 增加简单条件表达式
     */
    public ESQueryBuilderConstructor must(ESCriterion criterion) {
        if (criterion != null) {
            mustCriterions.add(criterion);
        }
        return this;
    }

    /**
     * 增加简单条件表达式
     */
    public ESQueryBuilderConstructor should(ESCriterion criterion) {
        if (criterion != null) {
            shouldCriterions.add(criterion);
        }
        return this;
    }

    /**
     * 增加简单条件表达式
     */
    public ESQueryBuilderConstructor mustNot(ESCriterion criterion) {
        if (criterion != null) {
            mustNotCriterions.add(criterion);
        }
        return this;
    }

    public ESQueryBuilderConstructor addSort(String field, SortOrder sort) {
        if (this.sorts == null) {
            sorts = new HashMap<>(4);
        }
        sorts.put(field, sort);
        return this;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    /*public String getAsc() {
        return asc;
    }

    public void setAsc(String asc) {
        this.asc = asc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }*/

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public Map<String, SortOrder> getSorts() {
        return sorts;
    }

    public String[] getIncludeFields() {
        return includeFields;
    }

    public void setIncludeFields(String[] includeFields) {
        this.includeFields = includeFields;
    }

    public String[] getExcludeFields() {
        return excludeFields;
    }

    public void setExcludeFields(String[] excludeFields) {
        this.excludeFields = excludeFields;
    }
}