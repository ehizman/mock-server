package com.ehizman.mock_gloserver.filters;

import com.ehizman.mock_gloserver.exceptions.MockRequeryServerException;
import com.ehizman.mock_gloserver.models.RequeryApiResponse;
import com.ehizman.mock_gloserver.utils.ResponseBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(0)
public class RequestHeaderFilter implements Filter {
    private Logger logger = LoggerFactory.getLogger(RequestHeaderFilter.class);

    @Autowired
    private Environment env;


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        logger.info("Request --> {}", httpRequest.toString());
        logger.info("Response --> {}", httpResponse.toString());

        if (httpRequest.getHeader("x-api-key").equals(env.getProperty("x-api-key"))){
            logger.info("Successfully recognized api key in header");
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            String transId = "";
            logger.info("Unauthorized api key. Generating response ");

            try{
                transId = FilterUtil.getRequestBody(httpRequest).orElseThrow(() -> new MockRequeryServerException("no request bod")).getTransId();
            } catch (MockRequeryServerException exception){
                logger.error("Exception line - 47");
                logger.error("Exception class --> {}", this.getClass().getCanonicalName());
                logger.error("No request body --> {}", exception.toString());
            }finally {
                RequeryApiResponse apiResponse = ResponseBuilder.buildUnauthorizedRequeryResponse(
                        env.getProperty("unauthorized.resultcode"),
                        env.getProperty("unauthorized.message"),
                        env.getProperty("unauthorized.status"),
                        transId
                );
                httpResponse.setStatus(apiResponse.getHttpStatus().value());
                ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);;
                httpResponse.setContentType("application/json");
                httpResponse.setCharacterEncoding("UTF-8");
                httpResponse.getWriter().write(objectMapper.writeValueAsString(apiResponse));
            }
        }
    }


}
