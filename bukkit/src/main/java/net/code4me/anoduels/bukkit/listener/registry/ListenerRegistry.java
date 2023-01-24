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

package net.code4me.anoduels.bukkit.listener.registry;

import com.google.common.base.Preconditions;
import net.code4me.anoduels.common.plugin.DuelPlugin;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public final class ListenerRegistry {
    private ListenerRegistry() {
    }

    public static void register(@NotNull DuelPlugin plugin, @NotNull List<Class<?>> listenerClasses) {
        Preconditions.checkArgument(!listenerClasses.isEmpty(), "Listener list is empty");

        Plugin handle = JavaPlugin.getProvidingPlugin(ListenerRegistry.class);
        PluginManager pluginManager = handle.getServer().getPluginManager();

        for (Class<?> listenerClass : listenerClasses) {
            Listener listener = cast(plugin, listenerClass);

            pluginManager.registerEvents(listener, handle);
        }
    }

    private static Listener cast(@NotNull DuelPlugin plugin, @NotNull Class<?> clazz) {
        try {
            if (clazz.getConstructors()[0].getParameterCount() == 0) {
                return (Listener) clazz
                        .getConstructor()
                        .newInstance();
            } else {
                return (Listener) clazz
                        .getConstructor(DuelPlugin.class)
                        .newInstance(plugin);
            }
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Cannot cast class to listener: " + clazz.getName(), e);
        }
    }
}
