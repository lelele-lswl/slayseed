package org.lelele.slayseed.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SeedRequest {
    @NotBlank(message = "种子码不能为空")
    @Size(max = 200)
    private String seedCode;

    @NotBlank(message = "请选择塔")
    private String tower;

    @NotBlank(message = "请选择角色")
    private String towerCharacter;

    private String playerCount;
    private String seedType;

    @NotBlank(message = "描述不能为空")
    @Size(max = 2000)
    private String description;

    @Size(max = 500)
    private String tags;
}