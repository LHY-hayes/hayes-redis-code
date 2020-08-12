package com.hayes.bash.hayesredis.common;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class Pager implements Serializable {

    private static final long serialVersionUID = 4542617637761955078L;

    /**
     * currentPage 当前页
     */
    private long currentPage = 1;
    /**
     * pageSize 每页大小
     */
    private long pageSize = 10;
    /**
     * pageTotal 总页数
     */
    private long pageTotal;
    /**
     * recordTotal 总条数
     */
    private long recordTotal = 0;
    /**
     * previousPage 前一页
     */
    private long previousPage;
    /**
     * nextPage 下一页
     */
    private long nextPage;
    /**
     * firstPage 第一页
     */
    private long firstPage = 1;
    /**
     * lastPage 最后一页
     */
    private long lastPage;

    private long sRow;
    private long eRow;

    public Pager(long currentPage, long pageSize, long recordTotal) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.recordTotal = recordTotal;
        otherAttr();
    }

    public Pager() {
        super();
    }

    /**
     * 设置其他参数
     */
    public void otherAttr() {
        // 总页数
        this.pageTotal = this.recordTotal % this.pageSize > 0 ? this.recordTotal / this.pageSize + 1 : this.recordTotal / this.pageSize;
        // 第一页
        this.firstPage = 1;
        // 最后一页
        this.lastPage = this.pageTotal;
        if (currentPage < 1) {
            currentPage = 1;
        }
        if (currentPage > pageTotal) {
            currentPage = pageTotal;
        }
        // 前一页
        if (this.currentPage > 1) {
            this.previousPage = this.currentPage - 1;
        } else {
            this.previousPage = this.firstPage;
        }
        // 下一页
        if (this.currentPage < this.lastPage) {
            this.nextPage = this.currentPage + 1;
        } else {
            this.nextPage = this.lastPage;
        }
        sRow = (currentPage - 1) * pageSize;
        eRow = currentPage * pageSize;
    }

}
