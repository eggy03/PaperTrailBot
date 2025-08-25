package org.papertrail.sdk.http;

import com.google.gson.Gson;
import kong.unirest.core.HttpMethod;
import kong.unirest.core.HttpRequestWithBody;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import org.jetbrains.annotations.NotNull;

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

        Gson gson = new Gson();
        HttpResponse<String> response;
        if (requestBody != null) {
            String jsonBody = gson.toJson(requestBody);
            response = request.body(jsonBody).asString();
        } else {
            response = request.asString();
        }

        if(response.isSuccess()) {
            if (successResponseClass == Void.class || response.getBody() == null || response.getBody().isBlank()) {
                return new HttpServiceResponse<>(null, null, true); // success, but nothing to map
            }
            S success = gson.fromJson(response.getBody(), successResponseClass);
            return new HttpServiceResponse<>(success, null, true);

        } else {
            E error = gson.fromJson(response.getBody(), errorResponseClass);
            return new HttpServiceResponse<>(null, error, false);
        }
    }
}
