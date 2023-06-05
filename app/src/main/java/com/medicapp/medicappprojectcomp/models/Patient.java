package com.medicapp.medicappprojectcomp.models;


import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Patient {
    private Date birthday;
    private Date dateDiagnostic;
    private String email;
    private Double height;
    private String imageUrl;
    private String lastName;
    private String name;
    private String numberDocument;
    private String typeDocument;
    private Long weight;
    private String id;

}
