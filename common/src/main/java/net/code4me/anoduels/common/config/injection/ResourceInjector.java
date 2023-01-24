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

import net.code4me.anoduels.common.config.misc.FileProcessor;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ResourceInjector {
    public void inject(@NotNull Path path, @NotNull Class<?> loader, @NotNull String resource) {
        try (InputStream inputStream = loader.getResourceAsStream("/" + resource)){
            if (inputStream == null) {
                throw new IOException("Resource not found");
            }

            inject(path, inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void inject(@NotNull File file, @NotNull Class<?> loader, @NotNull String resource) {
        try (InputStream inputStream = loader.getResourceAsStream("/" + resource)){
            if (inputStream == null) {
                throw new IOException("Resource not found");
            }

            inject(file, inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void inject(@NotNull Path path, @NotNull InputStream inputStream) {
        FileProcessor.processFile(path);

        inject(path.toFile(), inputStream);
    }

    public void inject(@NotNull File file, @NotNull InputStream inputStream) {
        try (final OutputStream out = Files.newOutputStream(file.toPath())) {
            final byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            out.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
