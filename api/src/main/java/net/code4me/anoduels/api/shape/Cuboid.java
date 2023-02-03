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

package net.code4me.anoduels.api.shape;

import net.code4me.anoduels.api.component.LocationComponent;
import org.jetbrains.annotations.NotNull;

public final class Cuboid {
    public static final Serializer SERIALIZER = new Serializer();

    @NotNull
    public static Cuboid fromLocationComponents(@NotNull LocationComponent min, @NotNull LocationComponent max) {
        return of((int) min.getX(), (int) max.getX(),
                (int) min.getY(), (int) max.getY(),
                (int) min.getZ(), (int) max.getZ());
    }

    @NotNull
    public static Cuboid of(int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
        return new Cuboid(minX, maxX, minY, maxY, minZ, maxZ);
    }

    private final int minX;
    private final int maxX;
    private final int minY;
    private final int maxY;
    private final int minZ;
    private final int maxZ;

    private Cuboid(int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.minZ = minZ;
        this.maxZ = maxZ;
    }

    public boolean isInCuboid(int x, int y, int z) {
        return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
    }

    public boolean isInCuboid(@NotNull LocationComponent locationComponent) {
        return isInCuboid((int) locationComponent.getX(), (int) locationComponent.getY(), (int) locationComponent.getZ());
    }

    public int getMinX() {
        return minX;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMinY() {
        return minY;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getMinZ() {
        return minZ;
    }

    public int getMaxZ() {
        return maxZ;
    }

    public static final class Serializer implements net.code4me.anoduels.api.serializer.Serializer<Cuboid, String> {

        @Override
        public @NotNull String serialize(@NotNull Cuboid cuboid) {
            return String.format("%d;%d;%d;%d;%d;%d",
                    cuboid.minX, cuboid.maxX,
                    cuboid.minY, cuboid.maxY,
                    cuboid.minZ, cuboid.maxZ);
        }

        @Override
        public @NotNull Cuboid deserialize(@NotNull String serialized) {
            final String[] split = serialized.split(";");

            return new Cuboid(Integer.parseInt(split[0]), Integer.parseInt(split[1]),
                    Integer.parseInt(split[2]), Integer.parseInt(split[3]),
                    Integer.parseInt(split[4]), Integer.parseInt(split[5]));
        }
    }
}
