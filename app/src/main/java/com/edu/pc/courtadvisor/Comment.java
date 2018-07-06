package com.edu.pc.courtadvisor;

import com.firebase.ui.auth.data.model.User;

/**
 * Created by pierre on 12/04/2018.
 */

public class Comment {
    private String IdF;
    private String userId;
    private String commentText;
    private String courtId;
    private String timeCreated;
    private long numLikes;

    public String getIdF() {
        return IdF;
    }

    public void setIdF(String idF) {
        IdF = idF;
    }

    public Comment() {

    }

    public Comment(String userId, String commentText, String courtId, String timeCreated, long numLikes) {
        this.userId = userId;
        this.commentText = commentText;
        this.courtId = courtId;
        this.timeCreated = timeCreated;
        this.numLikes = numLikes;
    }

    public String getUser() {
        return userId;
    }

    public String getCommentText() {
        return commentText;
    }

    public String getCourtId() {
        return courtId;
    }

    public String getTimeCreated() {
        return timeCreated;
    }

    public long getNumLikes() {
        return numLikes;
    }

    public void setUser(String userId) {
        this.userId = userId;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public void setCourtId(String courtId) {
        this.courtId = courtId;
    }

    public void setTimeCreated(String timeCreated) {
        this.timeCreated = timeCreated;
    }

    public void setNumLikes(long numLikes) {
        this.numLikes = numLikes;
    }

}
