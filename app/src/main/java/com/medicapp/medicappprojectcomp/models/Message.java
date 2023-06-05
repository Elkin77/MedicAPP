package com.medicapp.medicappprojectcomp.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Message {
    private String uuid;
    private String message;
    private boolean hasImage;
    private String imagePath;
    private String from;
    private long stamp;
    private boolean hasFile;

}