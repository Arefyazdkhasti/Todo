package com.example.todo.Model;

public class Task {
    private int id;
    private String description;
    private int priority;
    private String category;
    private int is_done;
    private int is_archive;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIs_done() {
        return is_done;
    }

    public void setIs_done(int is_done) {
        this.is_done = is_done;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getIs_archive() {
        return is_archive;
    }

    public void setIs_archive(int is_archive) {
        this.is_archive = is_archive;
    }
}
