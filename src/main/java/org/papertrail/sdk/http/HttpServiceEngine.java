package org.papertrail.sdk.http;

import io.vavr.control.Either;
import org.jetbrains.annotations.NotNull;
import org.papertrail.sdk.model.ErrorObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.tinylog.Logger;

import java.net.URI;
import java.time.Instant;

public class HttpServiceEngine {

    private final RestClient client;
    private static final HttpServiceEngine INSTANCE = new HttpServiceEngine();

    private HttpServiceEngine() {
        client = RestClient.builder().build();
    }

    public static HttpServiceEngine getInstance() {
        return INSTANCE;
    }

    public  <S> Either<ErrorObject, S> makeRequest (
            @NotNull HttpMethod httpMethod,
            @NotNull String url,
            @NotNull HttpHeaders headers,
            @NotNull Class<S> successResponseClass) {

        try {
            S body = client.method(httpMethod)
                    .uri(URI.create(url))
                    .headers(h-> h.addAll(headers))
                    .retrieve()
                    .toEntity(successResponseClass)
                    .getBody();

            return Either.right(body);
        } catch (HttpClientErrorException e) {
            Logger.trace("Client error when calling {} {}: {}", httpMethod, url, e.getMessage(), e);
            ErrorObject error = e.getResponseBodyAs(ErrorObject.class);
            return Either.left(error);
        } catch (HttpServerErrorException e) {
            Logger.error("Server error when calling {} {}: {}", httpMethod, url, e.getMessage(), e);
            ErrorObject error = e.getResponseBodyAs(ErrorObject.class);
            return Either.left(error);
        } catch (ResourceAccessException e) {
            Logger.error("Resource access error when calling {} {}: {}", httpMethod, url, e.getMessage(), e);
            ErrorObject error =  new ErrorObject(503, "API Unreachable", e.getMessage(), Instant.now().toString(), url);
            return Either.left(error);
        }
    }

    public  <S> Either<ErrorObject, S> makeRequestWithBody (
            @NotNull HttpMethod httpMethod,
            @NotNull String url,
            @NotNull HttpHeaders headers,
            @NotNull Object requestBody,
            @NotNull Class<S> successResponseClass) {

        try {
            S body = client.method(httpMethod)
                    .uri(URI.create(url))
                    .headers(h-> h.addAll(headers))
                    .body(requestBody)
                    .retrieve()
                    .toEntity(successResponseClass)
                    .getBody();

            return Either.right(body);

        } catch (HttpClientErrorException e) {
            Logger.trace("Client error when calling {} {}: {}", httpMethod, url, e.getMessage(), e);
            ErrorObject error = e.getResponseBodyAs(ErrorObject.class);
            return Either.left(error);
        } catch (HttpServerErrorException e) {
            Logger.error("Server error when calling {} {}: {}", httpMethod, url, e.getMessage(), e);
            ErrorObject error = e.getResponseBodyAs(ErrorObject.class);
            return Either.left(error);
        } catch (ResourceAccessException e) {
            Logger.error("Resource access error when calling {} {}: {}", httpMethod, url, e.getMessage(), e);
            ErrorObject error =  new ErrorObject(503, "API Unreachable", e.getMessage(), Instant.now().toString(), url);
            return Either.left(error);
        }
    }
}
