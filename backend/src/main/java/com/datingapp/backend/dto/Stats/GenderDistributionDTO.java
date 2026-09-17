package com.datingapp.backend.dto.Stats;

import com.datingapp.backend.enums.Gender;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GenderDistributionDTO {

    private Gender gender;
    private Long count;
}