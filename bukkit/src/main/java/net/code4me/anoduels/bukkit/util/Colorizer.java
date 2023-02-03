package net.code4me.anoduels.bukkit.util;

import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Colorizer {
    private static final Pattern HEX_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}");

    @NotNull
    public static String colorize(@NotNull String text) {
        Matcher matcher = HEX_PATTERN.matcher(text);
        while (matcher.find()) {
            String hexCode = text.substring(matcher.start(), matcher.end());
            String replaceSharp = hexCode.replace('#', 'x');

            char[] ch = replaceSharp.toCharArray();
            StringBuilder builder = new StringBuilder();
            for (char c : ch) {
                builder.append("&").append(c);
            }
            text = text.replace(hexCode, builder.toString());
            matcher = HEX_PATTERN.matcher(text);
        }

        return ChatColor.translateAlternateColorCodes('&', text);
    }

    private Colorizer() {}
}
