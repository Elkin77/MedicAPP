package com.medicapp.medicappprojectcomp.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Insulin {
    private Double quantity;
    private String date;
    private String hour;
    private String state;
    private String comment;


}
