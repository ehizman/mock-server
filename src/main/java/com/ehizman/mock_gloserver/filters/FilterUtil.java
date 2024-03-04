package com.ehizman.mock_gloserver.filters;

import com.ehizman.mock_gloserver.models.RequeryApiRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;

public class FilterUtil {
    private static Logger logger = LoggerFactory.getLogger(FilterUtil.class);
    public static Optional<RequeryApiRequest> getRequestBody(HttpServletRequest request){
        // https://www.appsdeveloperblog.com/read-body-from-httpservletrequest-in-spring-filter/

        try {
//            byte[] byteArray = StreamUtils.copyToByteArray(request.getInputStream());
//            if (byteArray.length == 0){
//                return Optional.empty();
//            }
//            Map<String, String> jsonObject = objectMapper.readValue(byteArray, Map.class);
//            logger.info("Json Object --> {}", jsonObject);
//            return Optional.of(objectMapper.readValue(jsonObject, RequeryApiRequest.class));

            InputStream inputStream = request.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder requestBody = new StringBuilder();
            String line;

            ObjectMapper objectMapper = new ObjectMapper();

            while ((line = reader.readLine()) != null){
                requestBody.append(line);
            }
            // convert the JSON request body to POJO
            return Optional.of(objectMapper.readValue(requestBody.toString(), RequeryApiRequest.class));
        } catch (IOException e) {
            logger.error("Exception in Line 27");
            logger.error("Exception class --> FilterUtil.class");
            logger.error("IOException --> {}", e.getLocalizedMessage());
            throw new RuntimeException(e);
        }

    }
}
