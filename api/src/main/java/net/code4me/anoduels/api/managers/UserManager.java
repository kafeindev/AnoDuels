package net.code4me.anoduels.api.managers;

import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.manager.Manager;
import net.code4me.anoduels.api.model.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface UserManager extends Manager<UUID, User> {
    void shutdown();

    @NotNull
    CompletableFuture<Map<PlayerComponent, User>> loadUsers(@NotNull Set<PlayerComponent> playerComponents);

    @NotNull
    CompletableFuture<User> loadUser(@NotNull PlayerComponent playerComponent);

    @NotNull
    CompletableFuture<User> loadUserAndApply(@NotNull PlayerComponent playerComponent, @NotNull Consumer<User> consumer);

    void saveUsers(@NotNull Set<User> users);

    void saveUser(@NotNull User user);

    void saveUserAndRemove(@NotNull User user);

    void saveUserAndRemove(@NotNull PlayerComponent playerComponent);
}
