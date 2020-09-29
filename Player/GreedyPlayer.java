package com.tema1.Player;

import java.util.List;

import static com.tema1.common.Constants.UNKNOWN;
import static com.tema1.common.Constants.ZERO;

public final class GreedyPlayer extends BasePlayer {
    public GreedyPlayer(final int playerId) {
        super(PlayersType.GreedyPlayer, playerId);
    }
    public void addIllegalCard() {
        if (!bag.isFull()) {
            Integer bestIllegalCard = getBestIllegalCard();
            if (bestIllegalCard != UNKNOWN) {
                bag.addGood(bestIllegalCard);
            }
        }
    }
    public void inspect(final Player merchant, final List<Integer> assets) {
        int bagBribe = merchant.bag.getBribe();
        merchant.bag.setChecked();
        if (bagBribe > ZERO) {
            money += bagBribe;
            merchant.bag.takeBribe();
        } else {
            // use basic strategy
            super.inspect(merchant, assets);
        }
    }

}
