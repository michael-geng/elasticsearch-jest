package com.zhuanche.es.jest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Page<T> implements Serializable {

    private int pageSize;

    public static final int COUNT_NOT_COUNT = -1;
    public static final int PAGE_SIZE_NOT_PAGING = -1;
    private int last;

    private int first;
    private int next;
    private int prev;
    private int pageNo;
    private int centerNum;
    private static final long serialVersionUID = 1L;
    private String orderBy;
    public static final int COUNT_ONLY_COUNT = -2;
    private List<T> list;
    private long count;

    public List<T> getList() {
        return this.list;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public Page(int pageNo, int pageSize) {
        this(pageNo, pageSize, 0L);
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }


    public Page(int pageNo, int pageSize, long count, List<T> list) {
        this.pageNo = 1;
        this.pageSize = pageSize;
        this.list = list;
        this.centerNum = 5;

        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.count = count;
        if (list != null) {
            this.list = list;
        }
    }

    public int getFirstResult() {
        int a = (this.getPageNo() - 1) * this.getPageSize();
        if (this.getCount() != -1L && (long) a >= this.getCount()) {
            a = 0;
        }

        return a;
    }

    public int getMaxResults() {
        return this.getPageSize();
    }

    public long getCount() {
        return this.count;
    }

    public void setPageSize(int pageSize) {
        if (pageSize <= 0) {
            this.pageSize = 20;
        } else {
            this.pageSize = pageSize;
        }
    }

    public int getPageNo() {
        return this.pageNo;
    }

    public void initialize() {
        if (!this.isNotPaging() && !this.isNotCount() && !this.isOnlyCount()) {
            if (this.pageSize <= 0) {
                this.pageSize = 20;
            }

            this.first = 1;
            this.last = (int) (this.count / (long) this.pageSize) + this.first - 1;
            if (this.count % (long) this.pageSize != 0L || this.last == 0) {
                ++this.last;
            }

            if (this.last < this.first) {
                this.last = this.first;
            }

            if (this.pageNo <= this.first) {
                this.pageNo = this.first;
            }

            if (this.pageNo >= this.last) {
                this.pageNo = this.last;
            }

            Page var10000;
            if (this.pageNo > 1) {
                var10000 = this;
                this.prev = this.pageNo - 1;
            } else {
                var10000 = this;
                this.prev = this.first;
            }

            if (var10000.pageNo < this.last - 1) {
                this.next = this.pageNo + 1;
            } else {
                this.next = this.last;
            }
        }
    }

    public boolean isNotPaging() {
        return this.pageSize == -1;
    }

    public Page() {
        this.pageNo = 1;
        this.pageSize = 10;
        this.list = new ArrayList();
        this.centerNum = 5;
    }

    public void setCount(long count) {
        if ((this.count = count) != -1L && (long) this.pageSize >= count) {
            this.pageNo = 1;
        }

    }

    public boolean isOnlyCount() {
        return this.count == -2L;
    }

    public Page(int pageNo, int pageSize, long count) {
        this(pageNo, pageSize, count, (List) null);
    }

    public boolean isNotCount() {
        return this.count == -1L || this.isNotPaging();
    }

    public Page<T> setList(List<T> list) {
        if (list == null) {
            list = new ArrayList();
        }
        this.list = list;
        return this;
    }

    public void setCenterNum(int centerNum) {
        this.centerNum = centerNum;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

}