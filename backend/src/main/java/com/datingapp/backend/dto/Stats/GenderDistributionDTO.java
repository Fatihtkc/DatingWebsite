package com.datingapp.backend.dto.Stats;

import com.datingapp.backend.enums.Gender;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenderDistributionDTO {

    private Gender gender;
    private Long count;
}