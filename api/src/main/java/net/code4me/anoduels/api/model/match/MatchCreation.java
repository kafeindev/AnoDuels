package net.code4me.anoduels.api.model.match;

import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.model.match.properties.MatchProperties;
import org.jetbrains.annotations.NotNull;

public interface MatchCreation {
    @NotNull
    PlayerComponent getCreator();

    @NotNull
    PlayerComponent getOpponent();

    @NotNull
    MatchProperties getProperties();

    boolean isAccepted();

    void accept();
}
