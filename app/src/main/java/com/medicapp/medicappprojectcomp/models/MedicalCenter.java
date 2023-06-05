package com.medicapp.medicappprojectcomp.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class MedicalCenter {
    private String name;
    private String address;
    private String image;
    private long distance;
    private Double latitude;
    private Double longitude;
}
