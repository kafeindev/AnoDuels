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

        public static final ConfigKey<String> MATCH_DURATION = ConfigKey.create("5d", "settings", "match", "duration");

        public static final ConfigKey<String> WAITING_COUNTDOWN = ConfigKey.create("10s", "settings", "match", "waiting-countdown");
        public static final ConfigKey<String> QUEUE_COUNTDOWN = ConfigKey.create("10s", "settings", "match", "waiting-queue-countdown");
        public static final ConfigKey<String> TELEPORTING_COUNTDOWN = ConfigKey.create("10s", "settings", "match", "teleporting-countdown");

        public static final ConfigKey<String> STARTING_COUNTDOWN_SOUND = ConfigKey.create("BLOCK_NOTE_BLOCK_PLING", "settings", "match", "starting-countdown", "sound");
        public static final ConfigKey<List<String>> STARTING_COUNTDOWN_MESSAGES = ConfigKey.create(ImmutableList.of(), "settings", "match", "starting-countdown", "messages");
        public static final ConfigKey<List<String>> STARTING_COUNTDOWN_TITLES = ConfigKey.create(ImmutableList.of(), "settings", "match", "starting-countdown", "titles");

        public static final ConfigKey<String> STARTED_SOUND = ConfigKey.create("BLOCK_NOTE_BLOCK_PLING", "settings", "match", "started", "sound");
        public static final ConfigKey<String> STARTED_TITLE = ConfigKey.create("", "settings", "match", "started", "title");
        public static final ConfigKey<List<String>> STARTED_MESSAGES = ConfigKey.create(ImmutableList.of(), "settings", "match", "started", "messages");

        public static final ConfigKey<Boolean> COMMAND_BLOCKER_ENABLED = ConfigKey.create(true, "settings", "command-blocker", "enabled");
        public static final ConfigKey<String> COMMAND_BLOCKER_TYPE = ConfigKey.create("WHITELIST", "settings", "command-blocker", "type");
        public static final ConfigKey<List<String>> COMMAND_BLOCKER_COMMANDS = ConfigKey.create(ImmutableList.of(), "settings", "command-blocker", "commands");
    }

    public static final class Language {

        private Language() {}

        public static final ConfigKey<String> PREFIX = ConfigKey.create("&6AnoDuels &8»", "language", "prefix");

        public static final ConfigKey<String> RELOADED = ConfigKey.create("&aReloaded.", "language", "reloaded");
        public static final ConfigKey<String> NO_PERMISSION = ConfigKey.create("&cYou do not have permission to use this command.", "language", "no-permission");
        public static final ConfigKey<String> NO_PLAYER = ConfigKey.create("&cYou must be a player to use this command.", "language", "no-player");

        public static final ConfigKey<String> PLAYER_NOT_FOUND = ConfigKey.create("&cPlayer not found.", "language", "player-not-found");
        public static final ConfigKey<String> CANNOT_DUEL_YOURSELF = ConfigKey.create("&cYou cannot duel yourself.", "language", "cannot-duel-yourself");
        public static final ConfigKey<String> COMMAND_CANCELLED = ConfigKey.create("&cCommand cancelled.", "language", "command-cancelled");
        public static final ConfigKey<String> WITH_YOUR_OWN_ITEMS = ConfigKey.create("", "language", "with-your-own-items");

        public static final ConfigKey<String> BET_FULL_INVENTORY = ConfigKey.create("&cYour inventory is full.", "language", "bet", "full-inventory");
        public static final ConfigKey<String> BET_ITEM_SELECTION = ConfigKey.create("&aSelect an item to bet.", "language", "bet", "item-selection");

        public static final ConfigKey<String> MATCH_CANCELLED = ConfigKey.create("&cMatch cancelled.", "language", "match", "cancelled");
        public static final ConfigKey<List<String>> MATCH_ENDED_MESSAGE = ConfigKey.create(ImmutableList.of(), "language", "match", "ended-message");
        public static final ConfigKey<List<String>> MATCH_ENDED_MESSAGE_DRAW = ConfigKey.create(ImmutableList.of(), "language", "match", "ended-message-draw");
        public static final ConfigKey<String> MATCH_DRAW_REASON_EXPIRED = ConfigKey.create("", "language", "match", "draw-reason-expired");
        public static final ConfigKey<String> MATCH_DRAW_REASON_QUIT = ConfigKey.create("", "language", "match", "draw-reason-quit");
        public static final ConfigKey<String> MATCH_DRAW_REASON_UNKNOWN = ConfigKey.create("", "language", "match", "draw-reason-unknown");

        public static final ConfigKey<String> MATCH_WON_MESSAGE = ConfigKey.create("&aYou won the match.", "language", "match", "won-message");
        public static final ConfigKey<String> MATCH_LOST_MESSAGE = ConfigKey.create("&cYou lost the match.", "language", "match", "lost-message");
        public static final ConfigKey<String> MATCH_DRAW_MESSAGE = ConfigKey.create("&eThe match ended in a draw.", "language", "match", "draw-message");

        public static final ConfigKey<String> CANNOT_INVITED = ConfigKey.create("", "language", "invite", "cannot-invited");
        public static final ConfigKey<String> CANNOT_ACCEPTED = ConfigKey.create("", "language", "invite", "cannot-accepted");
        public static final ConfigKey<String> CANNOT_FOUND_INVITE = ConfigKey.create("&cYou do not have an invite.", "language", "invite", "cannot-found-invite");
        public static final ConfigKey<List<String>> INVITE_SENT_MESSAGE = ConfigKey.create(ImmutableList.of(), "settings", "invite", "sent-message");
        public static final ConfigKey<List<String>> INVITE_RECEIVED_MESSAGE = ConfigKey.create(ImmutableList.of(), "settings", "invite", "received-message");
        public static final ConfigKey<List<String>> INVITE_ACCEPTED_MESSAGE_FOR_SENDER = ConfigKey.create(ImmutableList.of(), "settings", "invite", "accepted-message-for-sender");
        public static final ConfigKey<List<String>> INVITE_ACCEPTED_MESSAGE_FOR_RECEIVER = ConfigKey.create(ImmutableList.of(), "settings", "invite", "accepted-message-for-receiver");
        public static final ConfigKey<List<String>> INVITE_DENIED_MESSAGE_FOR_SENDER = ConfigKey.create(ImmutableList.of(), "settings", "invite", "denied-message-for-sender");
        public static final ConfigKey<List<String>> INVITE_DENIED_MESSAGE_FOR_RECEIVER = ConfigKey.create(ImmutableList.of(), "settings", "invite", "denied-message-for-receiver");

        public static final ConfigKey<List<String>> ADMIN_HELP = ConfigKey.create(ImmutableList.of(), "language", "admin", "help-admin-message");

        public static final ConfigKey<List<String>> ADMIN_ARENA_HELP = ConfigKey.create(ImmutableList.of(), "language", "admin", "arena", "help-arena-message");
        public static final ConfigKey<String> ADMIN_ARENA_CREATE = ConfigKey.create("&aArena created.", "language", "admin", "arena", "create-message");
        public static final ConfigKey<String> ADMIN_ARENA_CREATE_CATEGORY = ConfigKey.create("&aArena category created.", "language", "admin", "arena", "create-category-message");
        public static final ConfigKey<String> ADMIN_ARENA_DELETE = ConfigKey.create("&aArena deleted.", "language", "admin", "arena", "delete-message");
        public static final ConfigKey<String> ADMIN_ARENA_DELETE_CATEGORY = ConfigKey.create("&aArena category deleted.", "language", "admin", "arena", "delete-category-message");
        public static final ConfigKey<List<String>> ADMIN_ARENA_LIST = ConfigKey.create(ImmutableList.of(), "language", "admin", "arena", "list-message");
        public static final ConfigKey<String> ADMIN_ARENA_SET_SPAWN = ConfigKey.create("&aSpawn set.", "language", "admin", "arena", "set-spawn-message");
        public static final ConfigKey<String> ADMIN_ARENA_TELEPORT = ConfigKey.create("&aTeleported to arena.", "language", "admin", "arena", "teleport-message");
        public static final ConfigKey<String> ADMIN_ARENA_SET_BOUNDS = ConfigKey.create("&aBounds set.", "language", "admin", "arena", "set-bounds-message");
        public static final ConfigKey<String> ADMIN_ARENA_SET_ICON = ConfigKey.create("&aIcon set.", "language", "admin", "arena", "set-icon-message");
        public static final ConfigKey<String> ADMIN_ARENA_SET_DESCRIPTION = ConfigKey.create("&aDescription set.", "language", "admin", "arena", "set-description-message");
        public static final ConfigKey<String> ADMIN_ARENA_ALREADY_EXISTS = ConfigKey.create("&cArena already exists.", "language", "admin", "arena", "already-exists-message");
        public static final ConfigKey<String> ADMIN_ARENA_DOES_NOT_EXIST = ConfigKey.create("&cArena does not exist.", "language", "admin", "arena", "does-not-exist-message");
        public static final ConfigKey<String> ADMIN_ARENA_INVALID_BOUNDS = ConfigKey.create("&cInvalid bounds.", "language", "admin", "arena", "invalid-bounds-message");
        public static final ConfigKey<String> ADMIN_ARENA_INVALID_SPAWN_POINT = ConfigKey.create("&cInvalid spawn point.", "language", "admin", "arena", "invalid-spawn-point-message");

        public static final ConfigKey<List<String>> ADMIN_KIT_HELP = ConfigKey.create(ImmutableList.of(), "language", "admin", "kit", "help-kit-message");
        public static final ConfigKey<String> ADMIN_KIT_CREATE = ConfigKey.create("&aKit created.", "language", "admin", "kit", "create-message");
        public static final ConfigKey<String> ADMIN_KIT_DELETE = ConfigKey.create("&aKit deleted.", "language", "admin", "kit", "delete-message");
        public static final ConfigKey<List<String>> ADMIN_KIT_LIST = ConfigKey.create(ImmutableList.of(), "language", "admin", "kit", "list-message");
        public static final ConfigKey<String> ADMIN_KIT_GET_CONTENTS = ConfigKey.create("&aKit contents get.", "language", "admin", "kit", "get-contents-message");
        public static final ConfigKey<String> ADMIN_KIT_SET_CONTENTS = ConfigKey.create("&aKit contents set.", "language", "admin", "kit", "set-contents-message");
        public static final ConfigKey<String> ADMIN_KIT_SET_ICON = ConfigKey.create("&aKit icon set.", "language", "admin", "kit", "set-icon-message");
        public static final ConfigKey<String> ADMIN_KIT_ALREADY_EXISTS = ConfigKey.create("&cKit already exists.", "language", "admin", "kit", "already-exists-message");
        public static final ConfigKey<String> ADMIN_KIT_DOES_NOT_EXIST = ConfigKey.create("&cKit does not exist.", "language", "admin", "kit", "does-not-exist-message");
        public static final ConfigKey<String> ADMIN_KIT_EMPTY_CONTENTS = ConfigKey.create("&cKit contents cannot be empty.", "language", "admin", "kit", "empty-contents-message");

        public static final ConfigKey<String> ENABLED = ConfigKey.create("&aEnabled.", "language", "enabled");
        public static final ConfigKey<String> DISABLED = ConfigKey.create("&cDisabled.", "language", "disabled");
    }
}
