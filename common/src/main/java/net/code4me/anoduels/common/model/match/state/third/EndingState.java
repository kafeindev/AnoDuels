package net.code4me.anoduels.common.model.match.state.third;

import com.google.common.collect.ImmutableMap;
import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.model.match.Match;
import net.code4me.anoduels.api.model.match.MatchResult;
import net.code4me.anoduels.api.model.match.bet.Bet;
import net.code4me.anoduels.api.model.match.player.MatchPlayerResult;
import net.code4me.anoduels.api.model.match.reason.MatchFinishReason;
import net.code4me.anoduels.api.model.plugin.DuelPlugin;
import net.code4me.anoduels.api.model.user.User;
import net.code4me.anoduels.common.config.key.ConfigKeys;
import net.code4me.anoduels.common.model.match.state.AbstractMatchState;
import org.jetbrains.annotations.NotNull;

public final class EndingState extends AbstractMatchState {
    private static final Type TYPE = Type.ENDING;

    public EndingState(@NotNull Match match) {
        super(match);
    }

    @Override
    public void start(@NotNull DuelPlugin plugin) {
        this.startTime = System.currentTimeMillis();
        setRemainingTime(3000);

        this.match.getPlayers().forEach(matchPlayer -> {
            if (matchPlayer.getResult() == null) {
                matchPlayer.setResult(MatchPlayerResult.DRAW);
                this.match.createResultAndApply(true);
            }

            PlayerComponent playerComponent = matchPlayer.getHandle();
            if (matchPlayer.getResult() == MatchPlayerResult.DRAW) {
                playerComponent.sendMessage(ConfigKeys.Language.MATCH_ENDED_MESSAGE_DRAW.getValue(), ImmutableMap.of(
                        "%opponent%", this.match.getOpponent(matchPlayer).getName(),
                        "%reason%", this.match.getFinishReasonMessage() != null
                                ? this.match.getFinishReasonMessage()
                                : ConfigKeys.Language.MATCH_DRAW_REASON_UNKNOWN.getValue()
                ));
            } else {
                MatchResult matchResult = this.match.getResult();
                playerComponent.sendMessage(ConfigKeys.Language.MATCH_ENDED_MESSAGE.getValue(), ImmutableMap.of(
                        "%winner%", matchResult.getWinner().getName(),
                        "%loser%", matchResult.getLoser().getName()
                ));
            }

            String[] resultMessage;
            switch (matchPlayer.getResult()) {
                case WIN:
                    resultMessage = ConfigKeys.Language.MATCH_WON_MESSAGE.getValue().split("\n");
                    break;
                case LOSS:
                    resultMessage = ConfigKeys.Language.MATCH_LOST_MESSAGE.getValue().split("\n");
                    break;
                case DRAW:
                    resultMessage = ConfigKeys.Language.MATCH_DRAW_MESSAGE.getValue().split("\n");
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + matchPlayer.getResult());
            }
            if (resultMessage.length == 1) {
                playerComponent.sendTitle(resultMessage[0]);
            } else {
                playerComponent.sendTitle(resultMessage[0], resultMessage[1]);
            }

            if (this.match.getFinishReason() == MatchFinishReason.CANCELLED
                    && playerComponent.isOnline()) {
                Bet bet = matchPlayer.getBet();
                if (bet != null) {
                    playerComponent.giveItems(bet.getItems());
                }

                User user = plugin.getUserManager().get(playerComponent.getUniqueId());
                user.setHistory(null);
                plugin.getUserManager().saveUser(user);
            }
        });

        if (this.match.getFinishReason() == MatchFinishReason.CANCELLED) {
            this.match.setStateType(Type.ENDED);
        }
    }

    @Override
    public void update(@NotNull DuelPlugin plugin) {
        if (isFinished()) {
            this.match.getPlayers().forEach(matchPlayer -> {
                PlayerComponent playerComponent = matchPlayer.getHandle();
                if (!playerComponent.isOnline() || matchPlayer.getResult() == MatchPlayerResult.LOSS) {
                    return;
                }

                User user = plugin.getUserManager().get(playerComponent.getUniqueId());
                user.applyHistory();
                plugin.getUserManager().saveUser(user);
            });

            this.match.setStateType(Type.ENDED);
        } else {
            String contentMessage = ConfigKeys.Settings.TELEPORTING_COUNTDOWN.getValue();
            long countdown = getRemainingTime() / 1000;

            this.match.getPlayers().forEach(matchPlayer -> {
                if (matchPlayer.getResult() == MatchPlayerResult.LOSS) {
                    return;
                }

                PlayerComponent playerComponent = matchPlayer.getHandle();
                playerComponent.sendActionBar(contentMessage, ImmutableMap.of("%countdown%", Long.toString(countdown)));
            });
        }
    }

    @Override
    public @NotNull Type getType() {
        return TYPE;
    }
}
