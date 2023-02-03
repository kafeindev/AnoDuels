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

package net.code4me.anoduels.common.config.misc;

import com.google.common.reflect.TypeToken;
import net.code4me.anoduels.api.component.LocationComponent;
import net.code4me.anoduels.api.shape.Cuboid;
import net.code4me.anoduels.common.config.adapter.CuboidAdapter;
import net.code4me.anoduels.common.config.adapter.LocationComponentAdapter;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.gson.GsonConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public final class NodeProcessor {
    static {
        TypeSerializerCollection.defaults()
                .register(TypeToken.of(LocationComponent.class), new LocationComponentAdapter())
                .register(TypeToken.of(Cuboid.class), new CuboidAdapter());
    }

    @NotNull
    public static ConfigurationNode processNode(@NotNull Path path) {
        FileProcessor.processFile(path);

        return processNode(path.toFile());
    }

    @NotNull
    public static ConfigurationNode processNode(@NotNull Path path, boolean gson) {
        FileProcessor.processFile(path);

        return processNode(path.toFile(), gson);
    }

    @NotNull
    public static ConfigurationNode processNode(@NotNull File file) {
        ConfigurationLoader<ConfigurationNode> loader = createLoader(file);

        try {
            return loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public static ConfigurationNode processNode(@NotNull File file, boolean gson) {
        ConfigurationLoader<ConfigurationNode> loader = gson
                ? createGsonLoader(file)
                : createLoader(file);

        try {
            return loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public static ConfigurationNode processNode(@NotNull BufferedReader reader) {
        ConfigurationLoader<ConfigurationNode> loader = createLoader(reader);

        try {
            return loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public static ConfigurationNode processNode(@NotNull BufferedReader reader, boolean gson) {
        ConfigurationLoader<ConfigurationNode> loader = gson
                ? createGsonLoader(reader)
                : createLoader(reader);

        try {
            return loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public static ConfigurationLoader<ConfigurationNode> createLoader(@NotNull File file) {
        return YAMLConfigurationLoader.builder()
                .setFile(file)
                .build();
    }

    @NotNull
    public static ConfigurationLoader<ConfigurationNode> createLoader(@NotNull BufferedReader reader) {
        return YAMLConfigurationLoader.builder()
                .setSource(() -> reader)
                .build();
    }

    @NotNull
    public static ConfigurationLoader<ConfigurationNode> createGsonLoader(@NotNull File file) {
        return GsonConfigurationLoader.builder()
                .setFile(file)
                .build();
    }

    @NotNull
    public static ConfigurationLoader<ConfigurationNode> createGsonLoader(@NotNull BufferedReader reader) {
        return GsonConfigurationLoader.builder()
                .setSource(() -> reader)
                .build();
    }

    private NodeProcessor() {}
}
