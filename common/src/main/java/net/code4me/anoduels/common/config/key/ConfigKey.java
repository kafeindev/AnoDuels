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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ConfigKey<V> {
    @NotNull
    public static <V> ConfigKey<V> create(@NotNull V defaultValue, @NotNull String configName,
                                          @NotNull String... keys) {
        return new ConfigKey<>(defaultValue, configName, keys);
    }

    @NotNull
    private final String configName;

    @NotNull
    private final String[] keys;

    @Nullable
    private V value;

    private ConfigKey(@Nullable V defaultValue, @NotNull String configName,
                      @NotNull String... keys) {
        this.value = defaultValue;
        this.configName = configName;
        this.keys = keys;
    }

    @Nullable
    public V getValue() {
        return value;
    }

    public void setValue(@NotNull V value) {
        this.value = value;
    }

    @NotNull
    public String[] getKeys() {
        return keys;
    }

    @NotNull
    public String getConfigName() {
        return configName;
    }
}
