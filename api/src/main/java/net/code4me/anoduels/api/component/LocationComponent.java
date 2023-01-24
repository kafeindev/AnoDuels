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
    public static LocationComponent of(@NotNull String worldName, int x, int y, int z) {
        return new LocationComponent(worldName, x, y, z);
    }

    /**
     * The world name of the location.
     */
    @NotNull
    private final String worldName;

    /**
     * The x component of the location.
     */
    private final int x;

    /**
     * The y component of the location.
     */
    private final int y;

    /**
     * The z component of the location.
     */
    private final int z;

    private LocationComponent(@NotNull String worldName, int x, int y, int z) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @NotNull
    public String getWorldName() {
        return worldName;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    private static class Serializer implements net.code4me.anoduels.api.serializer.Serializer<LocationComponent, String> {
        @Override
        public @NotNull String serialize(@NotNull LocationComponent locationComponent) {
            return String.format("%s;%d;%d;%d", locationComponent.worldName,
                    locationComponent.x, locationComponent.y, locationComponent.z);
        }

        @Override
        public @NotNull LocationComponent deserialize(@NotNull String serialized) {
            String[] split = serialized.split(";");

            return new LocationComponent(split[0],
                    Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
        }
    }
}
