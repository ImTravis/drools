package com.drools.rules.entity;

import java.util.List;

/**
 * 接受rule返回的参数
 */
public class ResultFact {
    private List<Object> list;
    private String result;

    public List<Object> getList() {
        return list;
    }

    public void setList(List<Object> list) {
        this.list = list;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "ResultFact{" +
                "list=" + list +
                ", result='" + result + '\'' +
                '}';
    }
}


