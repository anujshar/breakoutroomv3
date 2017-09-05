package com.aks.group.breakout.room;

/**
 * Created by anujs on 5/26/2017.
 */

public class Member {

    private String uid;
    private String name;

    public Member() {
    }

    public Member(String text, String name, String photoUrl) {
        this.uid = text;
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String text) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
