package net.code4me.anoduels.common.model.match.state.first;

import net.code4me.anoduels.api.component.ItemComponent;
import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.model.match.Match;
import net.code4me.anoduels.api.model.match.bet.Bet;
import net.code4me.anoduels.api.model.match.player.MatchPlayer;
import net.code4me.anoduels.api.model.menu.Menu;
import net.code4me.anoduels.api.model.plugin.DuelPlugin;
import net.code4me.anoduels.common.managers.MenuManagerImpl;
import net.code4me.anoduels.common.model.match.bet.BetImpl;
import net.code4me.anoduels.common.model.match.state.AbstractMatchState;
import org.jetbrains.annotations.NotNull;

public final class BettingState extends AbstractMatchState {
    private static final Type TYPE = Type.BETTING;

    public BettingState(@NotNull Match match) {
        super(match);
    }

    @Override
    public void start(@NotNull DuelPlugin plugin) {
        super.start(plugin);

        if (this.match.getProperties().getBetType() == Bet.Type.INVENTORY) {
            this.match.getPlayers().forEach(matchPlayer -> {
                PlayerComponent playerComponent = matchPlayer.getHandle();
                ItemComponent<?>[] items = playerComponent.getAllItems();

                Bet bet = BetImpl.create(items);
                matchPlayer.setBet(bet);
            });
            this.match.setStateType(Type.QUEUED);
        } else {
            Menu menu = plugin.getMenuManager().findByType(MenuManagerImpl.MenuType.BET_ACCEPTANCE)
                    .orElseThrow(() -> new IllegalStateException("No menu found for type " + MenuManagerImpl.MenuType.BET_ACCEPTANCE));
            this.match.getPlayers().forEach(matchPlayer -> {
                PlayerComponent playerComponent = matchPlayer.getHandle();
                playerComponent.openMenu(menu);
            });
        }
    }

    @Override
    public void update(@NotNull DuelPlugin plugin) {
        super.update(plugin);

        MatchPlayer player = this.match.getOwnerPlayer();
        if (player.getBet() != null) {
            this.match.setStateType(Type.QUEUED);
        }
    }

    @Override
    public @NotNull Type getType() {
        return TYPE;
    }
}
