package com.medicapp.medicappprojectcomp.models;

import com.google.android.gms.maps.model.LatLng;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PositionMap {
    private String title;
    private String content;
    private String imageBase64;
    private LatLng position;
    private String address;
    private String phone;
}
