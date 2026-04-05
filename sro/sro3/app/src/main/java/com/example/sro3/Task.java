package com.example.sro3;

public class Task {
    public long id;
    public String name, category;
    public long deadline;
    
    public Task(long id, String name, long deadline, String category) {
        this.id = id;
        this.name = name;
        this.deadline = deadline;
        this.category = category;
    }
}
