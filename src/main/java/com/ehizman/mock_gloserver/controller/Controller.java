package com.ehizman.mock_gloserver.controller;

import com.ehizman.mock_gloserver.models.RequeryApiRequest;
import com.ehizman.mock_gloserver.models.RequeryApiResponse;
import com.ehizman.mock_gloserver.utils.Utils;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;


@RestController
public class Controller {
    private final Logger logger = LoggerFactory.getLogger(Controller.class);
    @Autowired
    private Environment env;


    @GetMapping(
            value = "/status/",
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<?> checkTransactionStatus(
            @RequestBody RequeryApiRequest requeryRequestObject,
            @RequestParam("response_flag") @NotBlank String responseFlag
    ){
        logger.info("Request Body --> {}", requeryRequestObject);
        logger.info("Response Flag --> {}", responseFlag);
        if (responseFlag.trim().isEmpty()){
            new ResponseEntity<>("invalid request. Response flag is missing", HttpStatus.NOT_FOUND);
        }
        // The response flag chooses between generating a 500 or a transaction not found or a successful response
        RequeryApiResponse apiResponse = switch (responseFlag){
            case "sc" -> generateSuccessResponse(requeryRequestObject);
            case "nf" -> generateNotFoundResponse(requeryRequestObject);
            case "er" -> generate500Response(requeryRequestObject);
            default -> throw new IllegalStateException("Unexpected value: " + responseFlag);
        };
        assert apiResponse != null & Objects.requireNonNull(apiResponse).getHttpStatus() != null;
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

//    @ExceptionHandler({Exception.class})
//    public String serverError(ModelAndView mav, HttpServletRequest request) {
//        // Nothing to do.  Returns the logical view name of an error page, passed
//        // to the view-resolver(s) in usual way.
//        // Note that the exception is NOT available to this view (it is not added
//        // to the model) but see "Extending ExceptionHandlerExceptionResolver"
//        // below.
//        mav.addObject("url", request.getRequestURL());
//        mav.addObject("timestamp", LocalDateTime.now());
//        mav.addObject("error", "Server error");
//        mav.addObject("status", "error");
//        return "error";
//    }

    private RequeryApiResponse generate500Response(RequeryApiRequest requeryRequestObject) {
        return RequeryApiResponse.builder()
                .status("error")
                .transId(requeryRequestObject.getTransId())
                .resultCode(env.getProperty("server_failure.result_code"))
                .message("Something went wrong. Please try again later")
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
    }

    private RequeryApiResponse generateNotFoundResponse(RequeryApiRequest requeryRequestObject) {
        return RequeryApiResponse.builder()
                .status("error")
                .transId(requeryRequestObject.getTransId())
                .resultCode(env.getProperty("error_result_code"))
                .message("Transaction not found")
                .httpStatus(HttpStatus.NOT_FOUND)
                .build();

    }

    private RequeryApiResponse generateSuccessResponse(RequeryApiRequest requeryRequestObject) {
        return RequeryApiResponse.builder()
                .status("ok")
                .transId(requeryRequestObject.getTransId())
                .egmsTransId(Utils.generateAlphanumericString(32))
                .resultCode(env.getProperty("success.result_code"))
                .bucketId("12")
                .planId("21")
                .volume("500")
                .quantity("1")
                .transactionStatus("Successful")
                .transactionDetails("Transaction is successful")
                .httpStatus(HttpStatus.OK)
                .build();

    }
}
