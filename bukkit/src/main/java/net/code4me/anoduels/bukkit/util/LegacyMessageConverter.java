package net.code4me.anoduels.bukkit.util;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class LegacyMessageConverter {
    private static final char AMPERSAND_CHAR = '&';
    private static final ImmutableMap<String, String> COLOR_MAP = ImmutableMap.<String, String>builder()
            .put("0", "black")
            .put("1", "dark_blue")
            .put("2", "dark_green")
            .put("3", "dark_aqua")
            .put("4", "dark_red")
            .put("5", "dark_purple")
            .put("6", "gold")
            .put("7", "gray")
            .put("8", "dark_gray")
            .put("9", "blue")
            .put("a", "green")
            .put("b", "aqua")
            .put("c", "red")
            .put("d", "light_purple")
            .put("e", "yellow")
            .put("f", "white")
            .put("l", "bold")
            .put("n", "underline")
            .put("o", "italic")
            .put("r", "reset")
            .build();

    @NotNull
    public static String convert(@NotNull String message) {
        for (Map.Entry<String, String> entry : COLOR_MAP.entrySet()) {
            message = message.replaceAll(AMPERSAND_CHAR + entry.getKey(), "<" + entry.getValue() + ">");
        }
        
        return message;
    }

    private LegacyMessageConverter() {}
}
