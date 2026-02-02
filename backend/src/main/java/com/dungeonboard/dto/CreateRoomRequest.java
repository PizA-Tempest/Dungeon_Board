package com.dungeonboard.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateRoomRequest {
    @NotBlank
    @Size(min = 3, max = 30)
    private String name;

    @Min(2)
    @Max(4)
    private int maxPlayers = 4;

    private boolean isPrivate = false;

    private int botCount = 0;
}
