/*
 * MIT License
 *
 * Copyright (c) 2022 FarmerPlus
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

package net.code4me.anoduels.api.managers;

import net.code4me.anoduels.api.manager.Manager;
import net.code4me.anoduels.api.model.Config;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;

public interface ConfigManager extends Manager<String, Config> {
    @NotNull
    Config create(@NotNull String name, @NotNull String path);

    @NotNull
    Config create(@NotNull String name, @NotNull File file);

    @NotNull
    Config create(@NotNull String name, @NotNull String path, @NotNull InputStream inputStream);

    @NotNull
    Config create(@NotNull String name, @NotNull String path, @NotNull Class<?> loader, @NotNull String resource);

    @NotNull
    Config create(@NotNull String name, @NotNull File file, @NotNull Class<?> loader, @NotNull String resource);

    void injectKeys(@NotNull Config config, @NotNull Class<?> clazz);

    void injectKeys(@NotNull Config config, @NotNull Class<?> clazz, boolean prefix);
}
