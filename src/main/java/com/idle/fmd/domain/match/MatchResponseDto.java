package com.idle.fmd.domain.match;

import lombok.Data;

@Data
public class MatchResponseDto {
    private String nickname;
    private String tier;
    private String mode;
    private String myLine;
    private String opponent;
}
