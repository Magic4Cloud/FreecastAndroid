package com.cloud4magic.freecast.bean;

import java.util.List;

/**
 * Date    2017/7/10
 * Author  xiaomao
 */

public class MediaBean {

    private String name;
    private List<String> list;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "MediaBean{" +
                "name='" + name + '\'' +
                ", list=" + list +
                '}';
    }
}
