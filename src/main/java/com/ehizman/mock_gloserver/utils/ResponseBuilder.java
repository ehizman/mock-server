package com.ehizman.mock_gloserver.utils;


import com.ehizman.mock_gloserver.models.RequeryApiResponse;
import org.springframework.http.HttpStatus;

public class ResponseBuilder {
    public static RequeryApiResponse buildUnauthorizedRequeryResponse(String resultCode, String message, String status, String transId){
        return RequeryApiResponse.builder()
                        .status(status)
                        .resultCode(resultCode)
                        .transId(transId)
                        .message(message)
                        .httpStatus(HttpStatus.UNAUTHORIZED)
                        .build();
    }
}
