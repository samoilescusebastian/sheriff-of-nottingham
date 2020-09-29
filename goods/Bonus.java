package com.tema1.goods;

import static com.tema1.common.Constants.UNKNOWN;
import static com.tema1.common.Constants.ZERO;

public final class Bonus {
    private int kingId;
    private int kingCount;
    private int queenId;
    private int queenCount;

    public Bonus() {
        kingId = UNKNOWN;
        kingCount = ZERO;
        queenId = UNKNOWN;
        queenCount = ZERO;
    }

    public int getKingId() {
        return kingId;
    }

    public void setKingId(final int kingId) {
        this.kingId = kingId;
    }

    public int getKingCount() {
        return kingCount;
    }

    public void setKingCount(final int kingCount) {
        this.kingCount = kingCount;
    }

    public int getQueenId() {
        return queenId;
    }

    public void setQueenId(final int queenId) {
        this.queenId = queenId;
    }

    public int getQueenCount() {
        return queenCount;
    }

    public void setQueenCount(final int queenCount) {
        this.queenCount = queenCount;
    }

}
