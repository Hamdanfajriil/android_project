package com.example.lumicotelecommunication.models;

public class ModelProject {

    String id, project, uid;
    long timestamp;

    public ModelProject() {

    }

    public ModelProject(String id, String project, String uid, long timestamp) {
        this.id = id;
        this.project = project;
        this.uid = uid;
        this.timestamp = timestamp;

    }

    /*-----Getter/Setter----*/

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
