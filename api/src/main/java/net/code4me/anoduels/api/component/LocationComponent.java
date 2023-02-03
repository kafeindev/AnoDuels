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

package net.code4me.anoduels.api.component;

import org.jetbrains.annotations.NotNull;

public final class LocationComponent {
    public static final net.code4me.anoduels.api.serializer.Serializer<LocationComponent, String> SERIALIZER = new Serializer();

    @NotNull
    public static LocationComponent of(@NotNull String worldName, double x, double y, double z, float yaw, float pitch) {
        return new LocationComponent(worldName, x, y, z, yaw, pitch);
    }

    @NotNull
    private final String worldName;

    private final double x;

    private final double y;

    private final double z;

    private final float yaw;

    private final float pitch;

    private LocationComponent(@NotNull String worldName, double x, double y, double z, float yaw, float pitch) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @NotNull
    public String getWorldName() {
        return this.worldName;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    private static class Serializer implements net.code4me.anoduels.api.serializer.Serializer<LocationComponent, String> {
        @Override
        public @NotNull String serialize(@NotNull LocationComponent locationComponent) {
            return locationComponent.getWorldName() + ";" +
                    locationComponent.getX() + ";" + locationComponent.getY() + ";" + locationComponent.getZ() + ";" +
                    locationComponent.getYaw() + ";" + locationComponent.getPitch();
        }

        @Override
        public @NotNull LocationComponent deserialize(@NotNull String serialized) {
            String[] split = serialized.split(";");

            return LocationComponent.of(split[0],
                    Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]),
                    Float.parseFloat(split[4]), Float.parseFloat(split[5]));
        }
    }
}
