package com.dungeonboard.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CharacterSelectionRequest {
    @NotNull
    private int classId;

    @NotNull
    private int raceId;
}
