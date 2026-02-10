package io.github.eggy03.papertrail.bot.listeners.audit.helper.stage.utils;

import io.github.eggy03.papertrail.bot.commons.utils.NumberParseUtils;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.StageInstance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class StageUtils {

    @NotNull
    public static String resolveStagePrivacyLevel(@Nullable Object stagePrivacyLevelInteger) {
        Integer privacyLevel = NumberParseUtils.parseInt(stagePrivacyLevelInteger);

        if(privacyLevel==null) return "N/A";

        //JDA removed this, and it technically shouldn't happen either since all stages are GUILD_ONLY now
        if(privacyLevel==1) return "PUBLIC (Deprecated)";

        return StageInstance.PrivacyLevel.fromKey(privacyLevel).name();
    }
}
