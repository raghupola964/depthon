package com.depthon.dto;

import lombok.Data;

@Data
public class JudgeVerdict {

    private Long post_id;
    private String decision;       // APPROVED | REJECTED | NEEDS_REVISION
    private String feedback;
    private Double insight_score;
}