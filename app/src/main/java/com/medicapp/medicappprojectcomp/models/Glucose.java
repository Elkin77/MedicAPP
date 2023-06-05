package com.medicapp.medicappprojectcomp.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Glucose {
    private Double quantity;
    private String date;
    private String hour;

}
