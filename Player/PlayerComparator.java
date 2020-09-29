package com.tema1.Player;

import java.util.Comparator;

public class PlayerComparator implements Comparator<Player> {
    /**
     * @comparator create player comparator
     */
    public int compare(final Player playerA, final Player playerB) {
        return playerB.getScore() - playerA.getScore();
    }
}
