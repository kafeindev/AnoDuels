package net.code4me.anoduels.common.managers;

import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.managers.UserManager;
import net.code4me.anoduels.api.model.plugin.DuelPlugin;
import net.code4me.anoduels.api.model.user.User;
import net.code4me.anoduels.common.config.misc.FileProcessor;
import net.code4me.anoduels.common.config.misc.NodeProcessor;
import net.code4me.anoduels.common.manager.AbstractManager;
import net.code4me.anoduels.common.model.user.UserImpl;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public final class UserManagerImpl extends AbstractManager<UUID, User> implements UserManager {
    @NotNull
    private final DuelPlugin plugin;

    @NotNull
    private final Path dataPath;

    public UserManagerImpl(@NotNull DuelPlugin plugin) {
        this.plugin = plugin;
        this.dataPath = plugin.getDataPath().resolve("users");
        FileProcessor.processDirectory(this.dataPath);
    }

    @Override
    public void shutdown() {
        saveUsers(new HashSet<>(findAll()));
    }

    @Override
    public @NotNull CompletableFuture<Map<PlayerComponent, User>> loadUsers(@NotNull Set<PlayerComponent> playerComponents) {
        return CompletableFuture.supplyAsync(() -> {
            Map<PlayerComponent, User> users = new HashMap<>();

            for (PlayerComponent playerComponent : playerComponents) {
                User user = loadUser(playerComponent).join();
                users.put(playerComponent, user);
            }

            return users;
        });
    }

    @Override
    public @NotNull CompletableFuture<User> loadUser(@NotNull PlayerComponent playerComponent) {
        return CompletableFuture.supplyAsync(() -> {
            Path userPath = this.dataPath
                    .resolve(playerComponent.getUniqueId().toString() + ".json");

            ConfigurationNode node;
            if (Files.notExists(userPath)) {
                FileProcessor.processFile(userPath);
                try {
                    ConfigurationLoader<ConfigurationNode> loader = NodeProcessor.createGsonLoader(userPath.toFile());
                    node = loader.createEmptyNode();
                    node.getNode("uuid").setValue(playerComponent.getUniqueId().toString());
                    node.getNode("name").setValue(playerComponent.getName());

                    loader.save(node);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }else {
                node = NodeProcessor.processNode(userPath, true);
            }

            User user = UserImpl.fromStorage(plugin, node, playerComponent);
            return put(playerComponent.getUniqueId(), user);
        });
    }

    @Override
    public @NotNull CompletableFuture<User> loadUserAndApply(@NotNull PlayerComponent playerComponent,
                                                             @NotNull Consumer<User> consumer) {
        return CompletableFuture.supplyAsync(() -> {
            User user = loadUser(playerComponent).join();
            consumer.accept(user);

            return user;
        });
    }

    @Override
    public void saveUsers(@NotNull Set<User> users) {
        CompletableFuture.runAsync(() -> users.forEach(this::saveUser));
    }

    @Override
    public void saveUser(@NotNull User user) {
        CompletableFuture.runAsync(() -> {
            Path userPath = this.dataPath
                    .resolve(user.getUniqueId().toString() + ".json");
            FileProcessor.processFile(userPath);

            ConfigurationLoader<ConfigurationNode> loader = NodeProcessor.createGsonLoader(userPath.toFile());
            ConfigurationNode node = loader.createEmptyNode();
            try {
                user.save(plugin, node);
                loader.save(node);
            } catch (ObjectMappingException | IOException e) {
                throw new RuntimeException("Failed to save user: " + user.getUniqueId(), e);
            }
        });
    }

    @Override
    public void saveUserAndRemove(@NotNull User user) {
        CompletableFuture.runAsync(() -> {
            saveUser(user);
            remove(user.getUniqueId());
        });
    }

    @Override
    public void saveUserAndRemove(@NotNull PlayerComponent playerComponent) {
        CompletableFuture.runAsync(() -> find(playerComponent.getUniqueId()).ifPresent(user -> {
            saveUser(user);
            remove(user.getUniqueId());
        }));
    }

    @Override
    public User put(@NotNull UUID key, @NotNull User value) {
        return super.put(key, value);
    }
}
