/*
 * MIT License
 *
 * Copyright (c) 2022 DreamCoins
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.code4me.anoduels.common.config.key;

import com.google.common.collect.ImmutableList;

import java.util.List;

public final class ConfigKeys {

    private ConfigKeys() {}

    public static final class Settings {

        private Settings() {}

        public static final ConfigKey<String> INVITE_DURATION = ConfigKey.create("1d", "settings", "invite-duration");
        public static final ConfigKey<String> QUEUE_DURATION = ConfigKey.create("1d", "settings", "queue-duration");

        public static final ConfigKey<String> MATCH_EXPIRATION = ConfigKey.create("5d", "settings", "match", "expiration");

    }

    public static final class Language {

        private Language() {}

        public static final ConfigKey<String> PREFIX = ConfigKey.create("&6AnoDuels &8»", "language", "prefix");

        public static final ConfigKey<String> NO_PERMISSION = ConfigKey.create("&cYou do not have permission to use this command.", "language", "no-permission");
        public static final ConfigKey<String> NO_PLAYER = ConfigKey.create("&cYou must be a player to use this command.", "language", "no-player");

        public static final ConfigKey<List<String>> ADMIN_HELP = ConfigKey.create(ImmutableList.of(), "language", "admin", "help-admin-message");

        public static final ConfigKey<List<String>> ADMIN_ARENA_HELP = ConfigKey.create(ImmutableList.of(), "language", "admin", "arena", "help-arena-message");
        public static final ConfigKey<String> ADMIN_ARENA_CREATE = ConfigKey.create("&aArena created.", "language", "admin", "arena", "create-message");
        public static final ConfigKey<String> ADMIN_ARENA_DELETE = ConfigKey.create("&aArena deleted.", "language", "admin", "arena", "delete-message");
        public static final ConfigKey<List<String>> ADMIN_ARENA_LIST = ConfigKey.create(ImmutableList.of(), "language", "admin", "arena", "list-message");
        public static final ConfigKey<String> ADMIN_ARENA_SET_SPAWN = ConfigKey.create("&aSpawn set.", "language", "admin", "arena", "set-spawn-message");
        public static final ConfigKey<String> ADMIN_ARENA_TELEPORT = ConfigKey.create("&aTeleported to arena.", "language", "admin", "arena", "teleport-message");
        public static final ConfigKey<String> ADMIN_ARENA_SET_BOUNDS = ConfigKey.create("&aBounds set.", "language", "admin", "arena", "set-bounds-message");
        public static final ConfigKey<String> ADMIN_ARENA_SET_ICON = ConfigKey.create("&aIcon set.", "language", "admin", "arena", "set-icon-message");
        public static final ConfigKey<String> ADMIN_ARENA_ALREADY_EXISTS = ConfigKey.create("&cArena already exists.", "language", "admin", "arena", "already-exists-message");
        public static final ConfigKey<String> ADMIN_ARENA_DOES_NOT_EXIST = ConfigKey.create("&cArena does not exist.", "language", "admin", "arena", "does-not-exist-message");
        public static final ConfigKey<String> ADMIN_ARENA_INVALID_BOUNDS = ConfigKey.create("&cInvalid bounds.", "language", "admin", "arena", "invalid-bounds-message");
        public static final ConfigKey<String> ADMIN_ARENA_INVALID_SPAWN_POINT = ConfigKey.create("&cInvalid spawn point.", "language", "admin", "arena", "invalid-spawn-point-message");

        public static final ConfigKey<List<String>> ADMIN_KIT_HELP = ConfigKey.create(ImmutableList.of(), "language", "admin", "help-kit-message");
        public static final ConfigKey<String> ADMIN_KIT_CREATE = ConfigKey.create("&aKit created.", "language", "admin", "kit", "create-message");
        public static final ConfigKey<String> ADMIN_KIT_DELETE = ConfigKey.create("&aKit deleted.", "language", "admin", "kit", "delete-message");
        public static final ConfigKey<List<String>> ADMIN_KIT_LIST = ConfigKey.create(ImmutableList.of(), "language", "admin", "kit", "list-message");
        public static final ConfigKey<String> ADMIN_KIT_GET_CONTENTS = ConfigKey.create("&aKit contents set.", "language", "admin", "kit", "get-contents-message");
        public static final ConfigKey<String> ADMIN_KIT_SET_CONTENTS = ConfigKey.create("&aKit contents set.", "language", "admin", "kit", "set-contents-message");
        public static final ConfigKey<String> ADMIN_KIT_SET_ICON = ConfigKey.create("&aKit icon set.", "language", "admin", "kit", "set-icon-message");
        public static final ConfigKey<String> ADMIN_KIT_ALREADY_EXISTS = ConfigKey.create("&cKit already exists.", "language", "admin", "kit", "already-exists-message");
        public static final ConfigKey<String> ADMIN_KIT_DOES_NOT_EXIST = ConfigKey.create("&cKit does not exist.", "language", "admin", "kit", "does-not-exist-message");
        public static final ConfigKey<String> ADMIN_KIT_EMPTY_CONTENTS = ConfigKey.create("&cKit contents cannot be empty.", "language", "admin", "kit", "empty-contents-message");
    }
}
