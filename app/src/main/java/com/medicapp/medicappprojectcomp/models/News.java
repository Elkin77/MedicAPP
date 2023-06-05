package com.medicapp.medicappprojectcomp.models;

public class News {
    String Name;
    String Description;
    public News(String name, String description){
        this.Name = name;
        this.Description = description;

    }

    public String getName() {
        return this.Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getDescription() {
        return this.Description;
    }

    public void setDescription(String description) {
        this.Description = description;
    }

}
