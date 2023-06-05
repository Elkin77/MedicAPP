package com.medicapp.medicappprojectcomp.models;

public enum Month {
    JANUARY("January", 1),
    FEBRUARY("February", 2),
    MARCH("March", 3),
    APRIL("April", 4),
    MAY("May", 5),
    JUNE("June", 6),
    JULY("July", 7),
    AUGUST("August", 8),
    SEPTEMBER("September", 9),
    OCTOBER("October", 10),
    NOVEMBER("November", 11),
    DECEMBER("December", 12);

    private final String label;
    private final int value;

    private Month(String label, int value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public int getValue() {
        return value;
    }
    public static Month fromValue(Integer value) {
        for (Month month : Month.values()) {
            if (month.value==value) {
                return month;
            }
        }
        throw new IllegalArgumentException("No se encontró un enum correspondiente al valor: " + value);
    }
}
