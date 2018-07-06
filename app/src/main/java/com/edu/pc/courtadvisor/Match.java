package com.edu.pc.courtadvisor;

import android.os.Parcel;
import android.os.Parcelable;

public class Match implements Parcelable {

    private int id;
    private String teamOneName;
    private String teamTwoName;
    private int teamOneScore;
    private int teamTwoScore;
//    private String created_at;

    public Match() {

    }

    public Match(int id, String teamOneName, String teamTwoName, int teamOneScore, int teamTwoScore) {
        this.id = id;
        this.teamOneName = teamOneName;
        this.teamTwoName = teamTwoName;
        this.teamOneScore = teamOneScore;
        this.teamTwoScore = teamTwoScore;
    }

    protected Match(Parcel in) {
        this.id = in.readInt();
        this.teamOneName = in.readString();
        this.teamTwoName = in.readString();
        this.teamOneScore = in.readInt();
        this.teamTwoScore = in.readInt();
    }

    public static final Creator<Match> CREATOR = new Creator<Match>() {
        @Override
        public Match createFromParcel(Parcel in) {
            return new Match(in);
        }

        @Override
        public Match[] newArray(int size) {
            return new Match[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTeamOneName() {
        return teamOneName;
    }

    public void setTeamOneName(String teamOneName) {
        this.teamOneName = teamOneName;
    }

    public String getTeamTwoName() {
        return teamTwoName;
    }

    public void setTeamTwoName(String teamTwoName) {
        this.teamTwoName = teamTwoName;
    }

    public int getTeamOneScore() {
        return teamOneScore;
    }

    public void setTeamOneScore(int teamOneScore) {
        this.teamOneScore = teamOneScore;
    }

    public int getTeamTwoScore() {
        return teamTwoScore;
    }

    public void setTeamTwoScore(int teamTwoScore) {
        this.teamTwoScore = teamTwoScore;
    }

//    public void setCreatedAt(String created_at) {
//        this.created_at = created_at;
//    }

    @Override
    public String toString() {
        return teamOneName + " " + teamOneScore + " - " + teamTwoScore + " " + teamTwoName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(teamOneName);
        parcel.writeString(teamTwoName);
        parcel.writeInt(teamOneScore);
        parcel.writeInt(teamTwoScore);

    }
}
