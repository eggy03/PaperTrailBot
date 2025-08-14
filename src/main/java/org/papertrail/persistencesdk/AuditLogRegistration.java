package org.papertrail.persistencesdk;

import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import org.papertrail.utilities.EnvConfig;

public class AuditLogRegistration {

    private AuditLogRegistration(){
        throw  new IllegalStateException("Utility Class");
    }

    private static final String baseUrl = EnvConfig.get("API_URL");

    public static HttpResponse<AuditLogRequestDTO> registerGuild(String guildId, String channelId){

       return Unirest.post(baseUrl+"api/v1/log/audit")
                .header("Content-Type", "application/json")
                .body(new AuditLogRequestDTO(guildId, channelId))
               .asObject(AuditLogRequestDTO.class);

    }

    public static HttpResponse<AuditLogRequestDTO> getRegisteredGuild(String guildId){

       return Unirest.get(baseUrl+"api/v1/log/audit/"+guildId)
               .asObject(AuditLogRequestDTO.class);
    }

    public static HttpResponse<AuditLogRequestDTO> updateRegisteredGuild(String guildId, String channelId){

        return Unirest.put(baseUrl+"api/v1/log/audit")
                .header("Content-Type", "application/json")
                .body(new AuditLogRequestDTO(guildId, channelId))
                .asObject(AuditLogRequestDTO.class);
    }

    public static HttpResponse<Void> deleteRegisteredGuild(String guildId){

        return Unirest.delete(baseUrl+"api/v1/log/audit/"+guildId)
                .asObject(Void.class);

    }
}
