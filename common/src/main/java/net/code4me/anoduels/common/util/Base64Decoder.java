package net.code4me.anoduels.common.util;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Base64;

public final class Base64Decoder {
    private Base64Decoder() {}

    @NotNull
    public static <T> T decode(@NotNull String s, @NotNull Class<T> clazz) {
        byte[] bytes = Base64.getDecoder().decode(s);
        InputStream inputStream = new ByteArrayInputStream(bytes);

        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            return clazz.cast(objectInputStream.readObject());
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @NotNull
    public static String encode(@NotNull Object obj) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(obj);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }

        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.getEncoder().encodeToString(bytes);
    }
}
