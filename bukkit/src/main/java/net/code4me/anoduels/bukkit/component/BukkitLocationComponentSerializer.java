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

package net.code4me.anoduels.bukkit.component;

import net.code4me.anoduels.api.component.LocationComponent;
import net.code4me.anoduels.api.serializer.Serializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class BukkitLocationComponentSerializer implements Serializer<LocationComponent, Location> {
    public static final BukkitLocationComponentSerializer INSTANCE = new BukkitLocationComponentSerializer();

    private BukkitLocationComponentSerializer() {
    }

    @Override
    public @NotNull Location serialize(@NotNull LocationComponent locationComponent) {
        @Nullable World world = Bukkit.getWorld(locationComponent.getWorldName());
        Objects.requireNonNull(world, "Couldn't find a world with the name: " + locationComponent.getWorldName());

        return new Location(world,
                locationComponent.getX(), locationComponent.getY(), locationComponent.getZ(),
                locationComponent.getYaw(), locationComponent.getPitch());
    }

    @Override
    public @NotNull LocationComponent deserialize(@NotNull Location location) {
        Objects.requireNonNull(location.getWorld(), "Location world cannot be null");

        return LocationComponent.of(location.getWorld().getName(),
                location.getBlockX(), location.getBlockY(), location.getBlockZ(),
                location.getYaw(), location.getPitch());
    }
}
