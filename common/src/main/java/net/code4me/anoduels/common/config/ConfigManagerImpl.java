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

package net.code4me.anoduels.common.config;

import net.code4me.anoduels.api.managers.ConfigManager;
import net.code4me.anoduels.api.model.Config;
import net.code4me.anoduels.common.manager.AbstractManager;
import net.code4me.anoduels.common.config.injection.KeyInjector;
import net.code4me.anoduels.common.config.injection.ResourceInjector;
import net.code4me.anoduels.common.config.misc.NodeProcessor;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;

public final class ConfigManagerImpl extends AbstractManager<String, Config> implements ConfigManager {
    private final ResourceInjector resourceInjector = new ResourceInjector();
    private final KeyInjector keyInjector = new KeyInjector();

    @NotNull
    private final Path dataPath;

    public ConfigManagerImpl(@NotNull Path dataPath) {
        this.dataPath = dataPath;
    }

    @Override
    public @NotNull Config create(@NotNull String name, @NotNull String path) {
        ConfigurationNode node = NodeProcessor.processNode(dataPath.resolve(path));

        return ConfigImpl.create(name, node);
    }

    @Override
    public @NotNull Config create(@NotNull String name, @NotNull File file) {
        ConfigurationNode node = NodeProcessor.processNode(file);

        return ConfigImpl.create(name, node);
    }

    @Override
    public @NotNull Config create(@NotNull String name, @NotNull String path, @NotNull InputStream inputStream) {
        Path filePath = dataPath.resolve(path);
        resourceInjector.inject(filePath, inputStream);

        return create(name, filePath.toFile());
    }

    @Override
    public @NotNull Config create(@NotNull String name, @NotNull String path, @NotNull Class<?> loader, @NotNull String resource) {
        Path filePath = dataPath.resolve(path);
        resourceInjector.inject(filePath, loader, resource);

        return create(name, filePath.toFile());
    }

    @Override
    public @NotNull Config create(@NotNull String name, @NotNull File file, @NotNull Class<?> loader, @NotNull String resource) {
        resourceInjector.inject(file, loader, resource);

        return create(name, file);
    }

    @Override
    public void injectKeys(@NotNull Config config, @NotNull Class<?> clazz) {
        try {
            keyInjector.inject(config, clazz);
        } catch (IllegalAccessException | ObjectMappingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void injectKeys(@NotNull Config config, @NotNull Class<?> clazz, boolean prefix) {
        try {
            keyInjector.inject(config, clazz, prefix);
        } catch (IllegalAccessException | ObjectMappingException e) {
            e.printStackTrace();
        }
    }
}
