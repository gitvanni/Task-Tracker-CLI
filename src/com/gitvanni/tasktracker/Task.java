package com.gitvanni.tasktracker;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Formatter;

/**
 * This class represents a task to be done.
 *
 */

public class Task implements Serializable {

    private final int id;
    private String description;
    private TaskStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;



    public Task(int id, String description){
        this.id = id;
        this.description = description;
        this.status = TaskStatus.TODO;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = createdAt;
    }

    public Task(int id, String description, TaskStatus status, LocalDateTime createdAt, LocalDateTime updatedAt){
        this.id = id;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        Formatter formatter = new Formatter(string);
        formatter.format("{".indent(4));
        formatter.format("\"id\": \"%s\",".indent(8), id);
        formatter.format("\"description\": \"%s\",".indent(8), description);
        formatter.format("\"status\": \"%s\",".indent(8), status);
        formatter.format("\"createdAt\": \"%s\",".indent(8), createdAt);
        formatter.format("\"updatedAt\": \"%s\"".indent(8), updatedAt);
        formatter.format("}".indent(4));
        formatter.flush();
        return string.toString();
    }
}
