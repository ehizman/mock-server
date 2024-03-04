package com.ehizman.mock_gloserver.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequeryApiResponse {
    private String status;
    private String transId;
    private String egmsTransId;
    private String resultCode;
    private String bucketId;
    private String bucketName;
    private String planId;
    private String volume;
    private String quantity;
    private String message;
    @JsonProperty(value = "transaction_status")
    private String transactionStatus;
    @JsonProperty(value = "transaction_details")
    private String transactionDetails;
    @JsonIgnore()
    private HttpStatus httpStatus;
}
