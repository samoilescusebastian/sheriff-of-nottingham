package com.tema1.cards;

import com.tema1.game.Game;
import com.tema1.goods.Goods;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static com.tema1.common.Constants.ZERO;

public final class Cards {
    private List<Integer> cards;
    private int cardsNumber;

    public Cards() {
        cards = new ArrayList<>();
        cardsNumber = ZERO;
    }
    public Cards(final Cards cards) {
        this.cards = new ArrayList<>();
        for (int i = 0; i < cards.getCardsNumber(); i++) {
            this.cards.add(cards.getCard(i));
        }
        cardsNumber = this.cards.size();
    }
    public int getCard(final int cardIndex) {
        return  cards.get(cardIndex);
    }
    public void addCard(final int card) {
        cards.add(card);
        cardsNumber++;
    }
    public void deleteCard(final int card) {
        cards.remove(Integer.valueOf(card));
        cardsNumber--;
    }
    public int getCardsNumber() {
        return cardsNumber;
    }

    @Override
    public String toString() {
        return "Cards{" + "cards=" + cards + ", cardsNumber=" + cardsNumber + '}';
    }
    public void sortCards() {
        boolean finish;
        do {
            finish = true;
            for (int i = 0; i < cardsNumber - 1; i++) {
                if (isUnordered(Game.getGoodById(cards.get(i)),
                    Game.getGoodById(cards.get(i + 1)))) {
                    Collections.swap(cards, i, i + 1);
                    finish = false;
                }
            }
        } while (!finish);
    }
    public boolean isUnordered(final Goods goodA, final Goods goodB) {
        if (goodA.getProfit() == goodB.getProfit()) {
            return goodA.getId() < goodB.getId();
        }
        return goodA.getProfit() < goodB.getProfit();

    }
    public void discardAll() {
        cards.clear();
        cardsNumber = ZERO;
    }
}
