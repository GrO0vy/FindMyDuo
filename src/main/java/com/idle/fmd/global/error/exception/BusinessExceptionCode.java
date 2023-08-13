package com.idle.fmd.global.error.exception;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

// 에러 코드를 작성하는 클래스
// 에러코드명( Http Status, "에러 발생 시 메세지" ) 형태로 작성
@RequiredArgsConstructor
@Getter
public enum BusinessExceptionCode {
    // 회원가입 관련 예외

    // 중복된 아이디로 회원가입을 시도할 때 발생하는 예외의 예외코드
    DUPLICATED_USER_ERROR(HttpStatus.BAD_REQUEST, "이미 존재하는 회원입니다."),

    // 회원가입 시 비밀번호와 비밀번호 확인 값이 다를 때 발생하는 예외의 예외코드
    PASSWORD_CHECK_ERROR(HttpStatus.BAD_REQUEST, "패스워드와 패스워드 확인이 일치 하지않습니다.");



    private final HttpStatus status;
    private final String message;
}
