package com.kalah.backend.helpers;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {

    private Flag flag;
    private String Message;
    private String ErrorMessage;
    private Integer Code;
    private Object Result;

    public enum Flag
    {
        Pass,
        Fail
    }

    public ApiResponse ReturnErrorResponse(ApiResponse response, int httpCode, String stackTrace, String errorMessage)
    {
        log.error("Error: " + stackTrace);
        response.setResult(null);
        response.setCode(httpCode);
        response.setErrorMessage(errorMessage);
        response.setFlag(ApiResponse.Flag.Fail);
        return response;
    }
}
