package com.aks.group.breakout.room;

/**
 * Created by anujs on 5/26/2017.
 */

public class GroupsData {
    private String groupUid;
    private String groupName;
    private String ownerName;
    private String ownerUid;
    public GroupsData() {
    }

    public GroupsData(String groupUid, String groupName, String ownerName, String ownerUid) {
        this.groupUid = groupUid;
        this.groupName = groupName;
        this.ownerName = ownerName;
        this.ownerUid = ownerUid;
    }

    public String getGroupUid() {
        return groupUid;
    }

    public void setGroupUid(String groupUid) {
        this.groupUid = groupUid;
    }


    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerUid() { return ownerUid; }

    public void setOwnerUid(String ownerUid) {
        this.ownerUid = ownerUid;
    }
}
