package com.example.todo.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Note implements Parcelable {

    private int id;
    private String title;
    private String content;
    private String color;
    private int is_archived;
    private String created_at;

    public Note(){

    }
    protected Note(Parcel in) {
        id = in.readInt();
        title = in.readString();
        content = in.readString();
        color = in.readString();
        is_archived = in.readInt();
        created_at = in.readString();
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getIs_archived() {
        return is_archived;
    }

    public void setIs_archived(int is_archived) {
        this.is_archived = is_archived;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(color);
        dest.writeInt(is_archived);
        dest.writeString(created_at);
    }
}
