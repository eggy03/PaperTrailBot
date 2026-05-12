package io.github.eggy03.papertrail.bot.commons.utils;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

@UtilityClass
@Deprecated(forRemoval = true)
@SuppressWarnings({"java:S1123", "java:S1133"})
public class StackWalkerUtils {

    private static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    // will show call hierarchies
    @NotNull
    @Deprecated(forRemoval = true)
    public static String getCallHierarchy() {
        return STACK_WALKER.walk(frames -> frames
                .skip(1)
                .limit(3)
                .map(frame -> frame.getDeclaringClass().getSimpleName() +
                        "#" + frame.getMethodName() +
                        ":" + frame.getLineNumber()
                ).collect(Collectors.joining(" -> "))
        );
    }
}
