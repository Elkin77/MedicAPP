package com.medicapp.medicappprojectcomp.adapters;

import com.airbnb.lottie.L;

public class Sport {

    private String Name;
    private String Level;

    private String Routine;

    private String Days;
    private String Sport;
    private String SportPng;

    public Sport(String name, String level, String routine, String days, String sport, String sportPng) {
        this.Name = name;
        this.Level = level;
        this.Routine = routine;
        this.Days = days;
        this.Sport = sport;
        this.SportPng = sportPng;
    }

    public String getRoutine() {
        return Routine;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setNativeName(String nativeName) {
        Level = nativeName;
    }


    public void setSport(String sport) {
        Sport = sport;
    }

    public void setSportPng(String sportPng) {
        SportPng = sportPng;
    }

    public String getName() {
        return Name;
    }

    public String getLevel() {
        return Level;
    }

    public void setLevel(String level) {
        Level = level;
    }

    public void setRoutine(String routine) {
        Routine = routine;
    }

    public void setDays(String days) {
        Days = days;
    }

    public String getDays() {
        return Days;
    }

    public String getSport() {
        return Sport;
    }

    public String getSportPng() {
        return SportPng;
    }

    @Override
    public String toString() {
        return "Sport{" +
                "Name='" + Name + '\'' +
                ", NativeName='" + Level + '\'' +
                ", Sport='" + Sport + '\'' +
                ", SportPng='" + SportPng + '\'' +
                '}';
    }
}
