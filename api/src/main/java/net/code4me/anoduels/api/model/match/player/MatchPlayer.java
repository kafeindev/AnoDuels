package net.code4me.anoduels.api.model.match.player;

import net.code4me.anoduels.api.component.ItemComponent;
import net.code4me.anoduels.api.component.LocationComponent;
import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.model.match.bet.Bet;
import net.code4me.anoduels.api.model.plugin.DuelPlugin;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface MatchPlayer {
    void save(@NotNull DuelPlugin plugin, @NotNull ConfigurationNode node) throws ObjectMappingException;

    @NotNull PlayerComponent getHandle();

    @NotNull
    default UUID getUniqueId() {
        return getHandle().getUniqueId();
    }

    @NotNull
    default String getName() {
        return getHandle().getName();
    }

    boolean isOwner();

    @Nullable
    MatchPlayerResult getResult();

    void setResult(@Nullable MatchPlayerResult result);

    @Nullable
    LocationComponent getOldLocation();

    void setOldLocation(@Nullable LocationComponent location);

    @Nullable Bet getBet();

    void setBet(@NotNull Bet bet);

    void setBetAccepted(boolean accepted);

    boolean isBetAccepted();

    @NotNull ItemComponent<?>[] getOldItems();

    @NotNull ItemComponent<?>[] getOldArmors();

    void setOldContents(@NotNull ItemComponent<?>[] items, @NotNull ItemComponent<?>[] armors);
}
