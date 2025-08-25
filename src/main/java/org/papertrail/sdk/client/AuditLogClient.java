package org.papertrail.sdk.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import org.papertrail.sdk.model.result.ApiResult;
import org.papertrail.sdk.model.ErrorResponse;
import org.papertrail.sdk.model.AuditLogResponse;
import org.papertrail.utilities.EnvConfig;
import org.tinylog.Logger;

import java.time.LocalDateTime;
import java.util.Arrays;

public class AuditLogClient {

    private AuditLogClient(){
        throw  new IllegalStateException("Utility Class");
    }

    private static final String BASE_URL = EnvConfig.get("API_URL");
    private static final String API_KEY_NAME = "apikey";
    private static final String API_KEY_VALUE = EnvConfig.get("API_KEY");

    public static ApiResult<AuditLogResponse, ErrorResponse> registerGuild(String guildId, String channelId){

       HttpResponse<String> response = Unirest.post(BASE_URL +"api/v1/log/audit")
               .basicAuth(API_KEY_NAME, API_KEY_VALUE)
                .header("Content-Type", "application/json")
                .body(new AuditLogResponse(guildId, channelId))
               .asString();

        ObjectMapper mapper = new ObjectMapper();

        if(response.isSuccess()) {
            try {
                AuditLogResponse successResponse = mapper.readValue(response.getBody(), AuditLogResponse.class);
                return new ApiResult<>(successResponse, null);
            } catch (JsonProcessingException e) {
                Logger.error(e);
                return new ApiResult<>(null, new ErrorResponse(-1, e.getClass().getSimpleName(), e.getMessage(), LocalDateTime.now().toString(), Arrays.toString(e.getStackTrace())));
            }
        } else {
            try {
                ErrorResponse errorResponse = mapper.readValue(response.getBody(), ErrorResponse.class);
                return new ApiResult<>(null, errorResponse);
            } catch (JsonProcessingException e) {
                Logger.error(e);
                return new ApiResult<>(null, new ErrorResponse(-1, e.getClass().getSimpleName(), e.getMessage(), LocalDateTime.now().toString(), Arrays.toString(e.getStackTrace())));
            }
        }

    }

    public static ApiResult<AuditLogResponse, ErrorResponse> getRegisteredGuild(String guildId){

        HttpResponse<String> response = Unirest.get(BASE_URL +"api/v1/log/audit/"+guildId)
                .basicAuth(API_KEY_NAME, API_KEY_VALUE)
               .asString();

        ObjectMapper mapper = new ObjectMapper();

        if(response.isSuccess()) {
            try {
                AuditLogResponse successResponse = mapper.readValue(response.getBody(), AuditLogResponse.class);
                return new ApiResult<>(successResponse, null);
            } catch (JsonProcessingException e) {
                Logger.error(e);
                return new ApiResult<>(null, new ErrorResponse(-1, e.getClass().getSimpleName(), e.getMessage(), LocalDateTime.now().toString(), Arrays.toString(e.getStackTrace())));
            }
        } else {
            try {
                ErrorResponse errorResponse = mapper.readValue(response.getBody(), ErrorResponse.class);
                return new ApiResult<>(null, errorResponse);
            } catch (JsonProcessingException e) {
                Logger.error(e);
                return new ApiResult<>(null, new ErrorResponse(-1, e.getClass().getSimpleName(), e.getMessage(), LocalDateTime.now().toString(), Arrays.toString(e.getStackTrace())));
            }
        }
    }

    public static ApiResult<AuditLogResponse, ErrorResponse> updateRegisteredGuild(String guildId, String channelId){

        HttpResponse<String> response = Unirest.put(BASE_URL +"api/v1/log/audit")
                .basicAuth(API_KEY_NAME, API_KEY_VALUE)
                .header("Content-Type", "application/json")
                .body(new AuditLogResponse(guildId, channelId))
                .asString();

        ObjectMapper mapper = new ObjectMapper();

        if(response.isSuccess()) {
            try {
                AuditLogResponse successResponse = mapper.readValue(response.getBody(), AuditLogResponse.class);
                return new ApiResult<>(successResponse, null);
            } catch (JsonProcessingException e) {
                Logger.error(e);
                return new ApiResult<>(null, new ErrorResponse(-1, e.getClass().getSimpleName(), e.getMessage(), LocalDateTime.now().toString(), Arrays.toString(e.getStackTrace())));
            }
        } else {
            try {
                ErrorResponse errorResponse = mapper.readValue(response.getBody(), ErrorResponse.class);
                return new ApiResult<>(null, errorResponse);
            } catch (JsonProcessingException e) {
                Logger.error(e);
                return new ApiResult<>(null, new ErrorResponse(-1, e.getClass().getSimpleName(), e.getMessage(), LocalDateTime.now().toString(), Arrays.toString(e.getStackTrace())));
            }
        }
    }

    public static ApiResult<AuditLogResponse, ErrorResponse> deleteRegisteredGuild(String guildId){

        HttpResponse<String> response = Unirest.delete(BASE_URL +"api/v1/log/audit/"+guildId)
                .basicAuth(API_KEY_NAME, API_KEY_VALUE)
                .asString();

        ObjectMapper mapper = new ObjectMapper();

        if(response.isSuccess()) {
            return new ApiResult<>(new AuditLogResponse("0", "0"), null);
        } else {
            try {
                ErrorResponse errorResponse = mapper.readValue(response.getBody(), ErrorResponse.class);
                return new ApiResult<>(null, errorResponse);
            } catch (JsonProcessingException e) {
                Logger.error(e);
                return new ApiResult<>(null, new ErrorResponse(-1, e.getClass().getSimpleName(), e.getMessage(), LocalDateTime.now().toString(), Arrays.toString(e.getStackTrace())));
            }
        }
    }
}
