package org.papertrail.sdk.client;

import kong.unirest.core.HttpMethod;
import org.papertrail.sdk.http.HttpServiceEngine;
import org.papertrail.sdk.http.HttpServiceResponse;
import org.papertrail.sdk.model.ErrorResponse;
import org.papertrail.sdk.model.AuditLogResponse;
import org.papertrail.utilities.EnvConfig;
import java.util.Map;

public class AuditLogClient {

    private AuditLogClient(){
        throw  new IllegalStateException("Utility Class");
    }

    private static final String BASE_URL = EnvConfig.get("API_URL");
    private static final Map<String, String > CONTENT_HEADER = Map.of("Content-Type", "application/json");

    public static HttpServiceResponse<AuditLogResponse, ErrorResponse> registerGuild(String guildId, String channelId){

        return HttpServiceEngine.makeRequest(
                HttpMethod.POST,
                BASE_URL +"api/v1/log/audit",
                CONTENT_HEADER,
                new AuditLogResponse(guildId, channelId),
                AuditLogResponse.class,
                ErrorResponse.class
        );
    }

    public static HttpServiceResponse<AuditLogResponse, ErrorResponse> getRegisteredGuild(String guildId){

        return HttpServiceEngine.makeRequest(
                HttpMethod.GET,
                BASE_URL +"api/v1/log/audit/"+guildId,
                CONTENT_HEADER,
                null,
                AuditLogResponse.class,
                ErrorResponse.class
        );
    }

    public static HttpServiceResponse<AuditLogResponse, ErrorResponse> updateRegisteredGuild(String guildId, String channelId){

        return HttpServiceEngine.makeRequest(
                HttpMethod.PUT,
                BASE_URL +"api/v1/log/audit",
                CONTENT_HEADER,
                new AuditLogResponse(guildId, channelId),
                AuditLogResponse.class,
                ErrorResponse.class
        );
    }

    public static HttpServiceResponse<Void, ErrorResponse> deleteRegisteredGuild(String guildId){

        return HttpServiceEngine.makeRequest(
                HttpMethod.DELETE,
                BASE_URL +"api/v1/log/audit/"+guildId,
                CONTENT_HEADER,
                null,
                Void.class,
                ErrorResponse.class
        );
    }
}
