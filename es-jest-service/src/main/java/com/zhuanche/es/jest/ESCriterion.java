package com.zhuanche.es.jest;

import org.elasticsearch.index.query.QueryBuilder;

import java.util.List;

/**
 * 查询条件
 */
public interface ESCriterion {
    public enum Operator {
        TERM, TERMS, RANGE, FUZZY, QUERY_STRING, MISSING, WILD, PREFIX
    }

    public enum MatchMode {
        START, END, ANYWHERE
    }

    public enum Projection {
        MAX, MIN, AVG, LENGTH, SUM, COUNT
    }

    public List<QueryBuilder> listBuilders();
}
