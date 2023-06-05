package com.medicapp.medicappprojectcomp.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Reminder {
    String title;
    String dateStart;
    String dateEnd;
    List<String> days;
    String hour;
}
