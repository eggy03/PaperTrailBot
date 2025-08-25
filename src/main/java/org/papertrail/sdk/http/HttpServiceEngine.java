package org.papertrail.sdk.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.core.HttpMethod;
import kong.unirest.core.HttpRequestWithBody;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import org.jetbrains.annotations.NotNull;
import org.tinylog.Logger;

import java.util.Map;

public class HttpServiceEngine {

    private HttpServiceEngine() {
        throw new IllegalStateException("Utility Class");
    }

    public static <S, E> HttpServiceResponse<S, E> makeRequest (
            @NotNull HttpMethod httpMethod,
            @NotNull String url, Map<String, String> headerMap,
            Object requestBody,
            @NotNull Class<S> successResponseClass,
            @NotNull Class<E> errorResponseClass) {

        HttpRequestWithBody request = Unirest
                .request(httpMethod.toString(), url)
                .headers(headerMap);

        if(requestBody!=null)
            request.body(requestBody);

        HttpResponse<String> response = request.asString();

        ObjectMapper mapper = new ObjectMapper();
        if(response.isSuccess()) {
            try {
                if (successResponseClass == Void.class || response.getBody() == null || response.getBody().isBlank()) {
                    return new HttpServiceResponse<>(null, null, true); // success, but nothing to map
                }
                S success = mapper.readValue(response.getBody(), successResponseClass);
                return new HttpServiceResponse<>(success, null, true);
            } catch (JsonProcessingException e) {
                Logger.error(e);
                return new HttpServiceResponse<>(null, null, false);
            }
        } else {
            try {
                E error = mapper.readValue(response.getBody(), errorResponseClass);
                return new HttpServiceResponse<>(null, error, false);
            } catch (JsonProcessingException e) {
                Logger.error(e);
                return new HttpServiceResponse<>(null, null, false);
            }
        }
    }
}
