package com.ehizman.mock_gloserver.filters;

import com.ehizman.mock_gloserver.models.RequeryApiRequest;
import com.ehizman.mock_gloserver.models.RequeryApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Order(1)
@Component
public class RequestCheckerFilter implements Filter {
    private Logger logger = LoggerFactory.getLogger(RequestCheckerFilter.class);
    @Autowired
    private Environment env;
    @SneakyThrows
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        logger.info("Request --> {}", httpRequest.toString());
        logger.info("Response --> {}", httpResponse.toString());
        CachedBodyHttpServletRequest cachedBodyHttpServletRequest =
                new CachedBodyHttpServletRequest(httpRequest);
        RequeryApiRequest requeryRequest = FilterUtil.getRequestBody(cachedBodyHttpServletRequest).orElse(null);

        if (requeryRequest == null){
            logger.error("Either SponsorId or TransId missing in requery object");
            RequeryApiResponse apiResponse = buildResponse();
            apiResponse.setMessage("Invalid Request. Either SponsorId or TransId is missing in request");
            writeServletResponse(httpResponse, apiResponse);
            return;
        }

        if (requeryRequest.getTransId() == null || requeryRequest.getTransId().trim().isEmpty()){
            logger.error("TransId missing in requery object");
            RequeryApiResponse apiResponse = buildResponse();
            apiResponse.setMessage("Invalid Request. TransId missing in requery object");
            writeServletResponse(httpResponse, apiResponse);
            return;
        }

        if (requeryRequest.getSponsorId() == null || requeryRequest.getSponsorId().trim().isEmpty()){
            logger.error("SponsorId missing in requery object");
            RequeryApiResponse apiResponse = buildResponse();
            apiResponse.setMessage("Invalid Request. TransId missing in requery object");
            writeServletResponse(httpResponse, apiResponse);
            return;
        }
        filterChain.doFilter(cachedBodyHttpServletRequest, servletResponse);
    }

    private void writeServletResponse(HttpServletResponse httpResponse, RequeryApiResponse requeryApiResponse) throws IOException {
        httpResponse.setStatus(requeryApiResponse.getHttpStatus().value());
        ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);;
        httpResponse.setContentType("application/json");
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.getWriter().write(objectMapper.writeValueAsString(requeryApiResponse));
    }

    private RequeryApiResponse buildResponse() {
        return RequeryApiResponse.builder()
                .status("error")
                .resultCode(env.getProperty("error.result_code"))
                .httpStatus(HttpStatus.BAD_REQUEST)
                .build();
    }
}
