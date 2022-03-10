package com.k2archer.server.tomato.bean.dto;

public class User {
    private long Id;
    private String name;


    public void setId(long id) {
        Id = id;
    }

    public long getId() {
        return Id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
