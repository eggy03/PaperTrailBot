package org.papertrail.persistencesdk.auditlog;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import org.papertrail.persistencesdk.ApiResponse;
import org.papertrail.persistencesdk.ErrorResponse;
import org.papertrail.utilities.EnvConfig;
import org.tinylog.Logger;

import java.time.LocalDateTime;

public class AuditLogRegistration {

    private AuditLogRegistration(){
        throw  new IllegalStateException("Utility Class");
    }

    private static final String BASE_URL = EnvConfig.get("API_URL");

    public static ApiResponse<AuditLogSuccessResponse, ErrorResponse> registerGuild(String guildId, String channelId){

       HttpResponse<String> response = Unirest.post(BASE_URL +"api/v1/log/audit")
                .header("Content-Type", "application/json")
                .body(new AuditLogSuccessResponse(guildId, channelId))
               .asString();

        ObjectMapper mapper = new ObjectMapper();

        if(response.isSuccess()) {
            try {
                AuditLogSuccessResponse successResponse = mapper.readValue(response.getBody(), AuditLogSuccessResponse.class);
                return new ApiResponse<>(successResponse, null);
            } catch (JsonProcessingException e) {
                Logger.error(e);
                return new ApiResponse<>(null, new ErrorResponse(-1, e.getMessage(), LocalDateTime.now().toString()));
            }
        } else {
            try {
                ErrorResponse errorResponse = mapper.readValue(response.getBody(), ErrorResponse.class);
                return new ApiResponse<>(null, errorResponse);
            } catch (JsonProcessingException e) {
                Logger.error(e);
                return new ApiResponse<>(null, new ErrorResponse(-1, e.getMessage(), LocalDateTime.now().toString()));
            }
        }

    }

    public static ApiResponse<AuditLogSuccessResponse, ErrorResponse> getRegisteredGuild(String guildId){

        HttpResponse<String> response = Unirest.get(BASE_URL +"api/v1/log/audit/"+guildId)
               .asString();

        ObjectMapper mapper = new ObjectMapper();

        if(response.isSuccess()) {
            try {
                AuditLogSuccessResponse successResponse = mapper.readValue(response.getBody(), AuditLogSuccessResponse.class);
                return new ApiResponse<>(successResponse, null);
            } catch (JsonProcessingException e) {
                Logger.error(e);
                return new ApiResponse<>(null, new ErrorResponse(-1, e.getMessage(), LocalDateTime.now().toString()));
            }
        } else {
            try {
                ErrorResponse errorResponse = mapper.readValue(response.getBody(), ErrorResponse.class);
                return new ApiResponse<>(null, errorResponse);
            } catch (JsonProcessingException e) {
                Logger.error(e);
                return new ApiResponse<>(null, new ErrorResponse(-1, e.getMessage(), LocalDateTime.now().toString()));
            }
        }
    }

    public static ApiResponse<AuditLogSuccessResponse, ErrorResponse> updateRegisteredGuild(String guildId, String channelId){

        HttpResponse<String> response = Unirest.put(BASE_URL +"api/v1/log/audit")
                .header("Content-Type", "application/json")
                .body(new AuditLogSuccessResponse(guildId, channelId))
                .asString();

        ObjectMapper mapper = new ObjectMapper();

        if(response.isSuccess()) {
            try {
                AuditLogSuccessResponse successResponse = mapper.readValue(response.getBody(), AuditLogSuccessResponse.class);
                return new ApiResponse<>(successResponse, null);
            } catch (JsonProcessingException e) {
                Logger.error(e);
                return new ApiResponse<>(null, new ErrorResponse(-1, e.getMessage(), LocalDateTime.now().toString()));
            }
        } else {
            try {
                ErrorResponse errorResponse = mapper.readValue(response.getBody(), ErrorResponse.class);
                return new ApiResponse<>(null, errorResponse);
            } catch (JsonProcessingException e) {
                Logger.error(e);
                return new ApiResponse<>(null, new ErrorResponse(-1, e.getMessage(), LocalDateTime.now().toString()));
            }
        }
    }

    public static ApiResponse<Void, ErrorResponse> deleteRegisteredGuild(String guildId){

        HttpResponse<String> response = Unirest.delete(BASE_URL +"api/v1/log/audit/"+guildId)
                .asString();

        ObjectMapper mapper = new ObjectMapper();

        if(response.isSuccess()) {
            return new ApiResponse<>(null, null);
        } else {
            try {
                ErrorResponse errorResponse = mapper.readValue(response.getBody(), ErrorResponse.class);
                return new ApiResponse<>(null, errorResponse);
            } catch (JsonProcessingException e) {
                Logger.error(e);
                return new ApiResponse<>(null, new ErrorResponse(-1, e.getMessage(), LocalDateTime.now().toString()));
            }
        }

    }
}
