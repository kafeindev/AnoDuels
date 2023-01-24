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

package net.code4me.anoduels.common.config.injection;

import com.google.common.reflect.TypeToken;
import net.code4me.anoduels.api.model.Config;
import net.code4me.anoduels.common.config.key.ConfigKey;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.jetbrains.annotations.NotNull;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Locale;

@SuppressWarnings("unchecked")
public final class KeyInjector {
    public void inject(@NotNull Config config, @NotNull Class<?> clazz)
            throws IllegalAccessException, IllegalArgumentException, ObjectMappingException {
        inject(config, clazz, false);
    }

    public void inject(@NotNull Config config, @NotNull Class<?> clazz, boolean prefix)
            throws IllegalAccessException, IllegalArgumentException, ObjectMappingException {
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);

            ConfigKey<Object> configKey = (ConfigKey<Object>) field.get(null);
            Class<?> type = field.getType();

            ConfigurationNode node = config.getNode();
            if (prefix) node = node.getNode(config.getName().toLowerCase(Locale.ROOT));
            for (String path : configKey.getKeys()) node = node.getNode(path);

            Object value = (Collection.class.isAssignableFrom(type)
                    ? node.getList(TypeToken.of(type))
                    : node.getValue(TypeToken.of(type)));
            if (value != null) configKey.setValue(value);
        }
    }
}
