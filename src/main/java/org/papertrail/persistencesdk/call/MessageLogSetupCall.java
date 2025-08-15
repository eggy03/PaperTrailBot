package org.papertrail.persistencesdk.call;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import org.papertrail.persistencesdk.response.ApiResponse;
import org.papertrail.persistencesdk.response.ErrorResponseObject;
import org.papertrail.persistencesdk.response.AuditLogSetupSuccessResponseObject;
import org.papertrail.persistencesdk.response.MessageLogSetupSuccessResponseObject;
import org.papertrail.utilities.EnvConfig;
import org.tinylog.Logger;

import java.time.LocalDateTime;

public class MessageLogSetupCall {

    private MessageLogSetupCall(){
        throw  new IllegalStateException("Utility Class");
    }

    private static final String BASE_URL = EnvConfig.get("API_URL");

    public static ApiResponse<MessageLogSetupSuccessResponseObject, ErrorResponseObject> registerGuild(String guildId, String channelId){

        HttpResponse<String> response = Unirest.post(BASE_URL +"api/v1/log/message")
                .header("Content-Type", "application/json")
                .body(new AuditLogSetupSuccessResponseObject(guildId, channelId))
                .asString();

        ObjectMapper mapper = new ObjectMapper();

        if(response.isSuccess()) {
            try {
                MessageLogSetupSuccessResponseObject successResponse = mapper.readValue(response.getBody(), MessageLogSetupSuccessResponseObject.class);
                return new ApiResponse<>(successResponse, null);
            } catch (JsonProcessingException e) {
                Logger.error(e);
                return new ApiResponse<>(null, new ErrorResponseObject(-1, e.getMessage(), LocalDateTime.now().toString()));
            }
        } else {
            try {
                ErrorResponseObject errorResponseObject = mapper.readValue(response.getBody(), ErrorResponseObject.class);
                return new ApiResponse<>(null, errorResponseObject);
            } catch (JsonProcessingException e) {
                Logger.error(e);
                return new ApiResponse<>(null, new ErrorResponseObject(-1, e.getMessage(), LocalDateTime.now().toString()));
            }
        }

    }

    public static ApiResponse<MessageLogSetupSuccessResponseObject, ErrorResponseObject> getRegisteredGuild(String guildId){

        HttpResponse<String> response = Unirest.get(BASE_URL +"api/v1/log/message/"+guildId)
                .asString();

        ObjectMapper mapper = new ObjectMapper();

        if(response.isSuccess()) {
            try {
                MessageLogSetupSuccessResponseObject successResponse = mapper.readValue(response.getBody(), MessageLogSetupSuccessResponseObject.class);
                return new ApiResponse<>(successResponse, null);
            } catch (JsonProcessingException e) {
                Logger.error(e);
                return new ApiResponse<>(null, new ErrorResponseObject(-1, e.getMessage(), LocalDateTime.now().toString()));
            }
        } else {
            try {
                ErrorResponseObject errorResponseObject = mapper.readValue(response.getBody(), ErrorResponseObject.class);
                return new ApiResponse<>(null, errorResponseObject);
            } catch (JsonProcessingException e) {
                Logger.error(e);
                return new ApiResponse<>(null, new ErrorResponseObject(-1, e.getMessage(), LocalDateTime.now().toString()));
            }
        }
    }

    public static ApiResponse<MessageLogSetupSuccessResponseObject, ErrorResponseObject> updateRegisteredGuild(String guildId, String channelId){

        HttpResponse<String> response = Unirest.put(BASE_URL +"api/v1/log/message")
                .header("Content-Type", "application/json")
                .body(new AuditLogSetupSuccessResponseObject(guildId, channelId))
                .asString();

        ObjectMapper mapper = new ObjectMapper();

        if(response.isSuccess()) {
            try {
                MessageLogSetupSuccessResponseObject successResponse = mapper.readValue(response.getBody(), MessageLogSetupSuccessResponseObject.class);
                return new ApiResponse<>(successResponse, null);
            } catch (JsonProcessingException e) {
                Logger.error(e);
                return new ApiResponse<>(null, new ErrorResponseObject(-1, e.getMessage(), LocalDateTime.now().toString()));
            }
        } else {
            try {
                ErrorResponseObject errorResponseObject = mapper.readValue(response.getBody(), ErrorResponseObject.class);
                return new ApiResponse<>(null, errorResponseObject);
            } catch (JsonProcessingException e) {
                Logger.error(e);
                return new ApiResponse<>(null, new ErrorResponseObject(-1, e.getMessage(), LocalDateTime.now().toString()));
            }
        }
    }

    public static ApiResponse<MessageLogSetupSuccessResponseObject, ErrorResponseObject> deleteRegisteredGuild(String guildId){

        HttpResponse<String> response = Unirest.delete(BASE_URL +"api/v1/log/message/"+guildId)
                .asString();

        ObjectMapper mapper = new ObjectMapper();

        if(response.isSuccess()) {
            return new ApiResponse<>(new MessageLogSetupSuccessResponseObject("0", "0"), null);
        } else {
            try {
                ErrorResponseObject errorResponseObject = mapper.readValue(response.getBody(), ErrorResponseObject.class);
                return new ApiResponse<>(null, errorResponseObject);
            } catch (JsonProcessingException e) {
                Logger.error(e);
                return new ApiResponse<>(null, new ErrorResponseObject(-1, e.getMessage(), LocalDateTime.now().toString()));
            }
        }

    }
}
