package com.idle.fmd.domain.matching;

import lombok.Data;

@Data
public class MatchingResponseDto {
    private String nickname;
    private String lolNickname;
    private String mode;
    private String myLine;
    private String tier;
    private String rank;
    private String mostOne;
    private String mostTwo;
    private String mostThree;
}
