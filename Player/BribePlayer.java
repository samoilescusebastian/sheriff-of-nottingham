package com.tema1.Player;

import com.tema1.cards.Cards;
import com.tema1.game.Game;
import com.tema1.goods.Goods;
import com.tema1.goods.GoodsType;
import java.util.ArrayList;
import java.util.List;
import static com.tema1.common.Constants.LOW_BRIBE;
import static com.tema1.common.Constants.ZERO;
import static com.tema1.common.Constants.THRESHOLD_GOODS;
import static com.tema1.common.Constants.HIGH_BRIBE;
import static com.tema1.common.Constants.BAG_CAPACITY;
import static com.tema1.common.Constants.ILLEGAL_PENALTY;

public final class BribePlayer extends BasePlayer {
    private Cards sortedCards;
    public BribePlayer(final int playerId) {
        super(PlayersType.BribePlayer, playerId);
    }
    public void setBag() {
        sortedCards = new Cards(cards);
        sortedCards.sortCards();
        // get most profitable illegal cards
        List<Integer> goodsToAdd = getGoods();
        // use basic strategy
        if (money <= LOW_BRIBE || goodsToAdd.size() == ZERO) {
            super.setBag();
        } else {
            // remove goods from bag in case of impossibility to play penalty
            resizeBag(goodsToAdd);
            int bribe = ZERO;
            if (goodsToAdd.size() > THRESHOLD_GOODS) {
                bribe = HIGH_BRIBE;
            } else if (money > LOW_BRIBE) {
                bribe = LOW_BRIBE;
            }

            // add extra legal cards in limit of current money
            int index = ZERO;
            int penalty = ILLEGAL_PENALTY * goodsToAdd.size();
            while (goodsToAdd.size() < BAG_CAPACITY && index < sortedCards.getCardsNumber()) {
                int currentCard = sortedCards.getCard(index);
                Goods currentGood = Game.getGoodById(currentCard);
                if (currentGood.getType() == GoodsType.Legal) {
                    penalty += currentGood.getPenalty();
                    if (money - penalty > ZERO) {
                        goodsToAdd.add(currentCard);
                    } else {
                        break;
                    }
                }
                index++;
            }
            money -= bribe;
            bag.setBag(goodsToAdd, bribe, ZERO);
        }


    }
    protected List<Integer> getGoods() {
        List<Integer> goods = new ArrayList<>();
        int penalty = 0;
        for (int i = 0; i < sortedCards.getCardsNumber(); i++) {
            Goods good = Game.getGoodById(sortedCards.getCard(i));
            penalty += good.getPenalty();
            if (good.getType() == GoodsType.Illegal && goods.size() < BAG_CAPACITY) {
                if (money - penalty > ZERO) {
                    goods.add(sortedCards.getCard(i));
                } else {
                    return goods;
                }

            }
        }
        return goods;
    }
    private void resizeBag(final List<Integer> goodsToAdd) {
        int size = goodsToAdd.size();
        while (size * ILLEGAL_PENALTY >= money && size > THRESHOLD_GOODS) {
            goodsToAdd.remove(size - 1);
            size = goodsToAdd.size();
        }
        while (size * ILLEGAL_PENALTY  >= money && size > ZERO) {
            goodsToAdd.remove(size - 1);
            size = goodsToAdd.size();
        }
    }

}

