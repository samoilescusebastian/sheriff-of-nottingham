package com.tema1.Player;

import com.tema1.cards.Cards;
import com.tema1.game.Game;
import com.tema1.goods.Goods;
import com.tema1.goods.GoodsType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tema1.common.Constants.ZERO;
import static com.tema1.common.Constants.ILLEGAL_PENALTY;
import static com.tema1.common.Constants.BAG_CAPACITY;
import static com.tema1.common.Constants.UNKNOWN;
import static com.tema1.common.Constants.UNDER_SUM;

public class BasePlayer extends Player {
    private Map<Integer, Integer> frequencies;
    private Cards sortedCards;
    public BasePlayer(final int playerId) {
        super(PlayersType.BasePlayer, playerId);
    }
    BasePlayer(final PlayersType type, final int playerId) {
        super(type, playerId);
    }
    /**
     * @override set base bag
     */
    public void setBag() {
        sortedCards = new Cards(cards);
        sortedCards.sortCards();
        List<Integer> goodsToAdd = getBestGoods();
        // delete bag goods from hand
        deleteBagCards(goodsToAdd);
        int type;
        boolean legalOnly = true;
        // set goods type
        if (Game.getGoodById(goodsToAdd.get(ZERO)).getType() == GoodsType.Legal) {
            type = goodsToAdd.get(ZERO);
        } else {
            legalOnly = false;
            type = ZERO;
        }
        // remove good from bag in case of impossibility to play penalty
        if (!legalOnly && money < ILLEGAL_PENALTY) {
            goodsToAdd.remove(ZERO);
        }
        bag.setBag(goodsToAdd, ZERO, type);
    }
    private List<Integer> getBestGoods() {
        List<Integer> goodsToAdd = new ArrayList<>();
        // resize bag to max capacity
        int maxFrequency = getMaxFrequency();
        if (maxFrequency > BAG_CAPACITY) {
            maxFrequency = BAG_CAPACITY;
        }
        if (maxFrequency == 0) {
            Integer bestIllegalCard = getBestIllegalCard();
            goodsToAdd.add(bestIllegalCard);
        } else {
            // add most frequent card of maxFrequency times
            Integer bestLegalCard = getBestLegalCard(maxFrequency);
            for (int i = 0; i < maxFrequency; i++) {
                goodsToAdd.add(bestLegalCard);
            }
        }
        return  goodsToAdd;
    }
    private int getMaxFrequency() {
        frequencies = new HashMap<>();
        int maxFrequency = ZERO;
        for (int i = 0; i < sortedCards.getCardsNumber(); i++) {
            int goodId = sortedCards.getCard(i);
            if (Game.getGoodById(goodId).getType() == GoodsType.Illegal) {
                continue;
            } else {
                frequencies.put(goodId, frequencies.getOrDefault(goodId, ZERO) + 1);
                if (frequencies.get(goodId) > maxFrequency) {
                    maxFrequency = frequencies.get(goodId);
                }
            }
        }
        return maxFrequency;
    }
    /**
     * @return max profit illegal card
     */
    protected int getBestIllegalCard() {
        for (int i = 0; i < sortedCards.getCardsNumber(); i++) {
            Goods good = Game.getGoodById(sortedCards.getCard(i));
            if (good.getType() == GoodsType.Illegal) {
                return good.getId();
            }
        }
        return UNKNOWN;
    }
    private int getBestLegalCard(final int maxFrequency) {
        int bestLegalItem = UNKNOWN;
        int maxProfit = ZERO;
        for (Integer key : frequencies.keySet()) {
            if (frequencies.get(key) == maxFrequency) {
                Goods good = Game.getGoodById(key);
                int goodProfit = good.getProfit();
                if (goodProfit > maxProfit) {
                    bestLegalItem = key;
                    maxProfit = goodProfit;
                } else if (goodProfit == maxProfit) {
                    if (key > bestLegalItem) {
                        bestLegalItem = key;
                    }
                }
            }
        }
        return  bestLegalItem;
    }
    private void deleteBagCards(final List<Integer> goodsToAdd) {
        for (Integer good : goodsToAdd) {
            sortedCards.deleteCard(good);
        }
    }
    /**
     * @override insect merchant bag
     */
    public void inspect(final Player merchant, final List<Integer> assets) {
        if (money < UNDER_SUM) {
            return;
        }
        List<Integer> confiscatedItems = verifyGoods(merchant);
        if (confiscatedItems.size() > 0) {
            confiscateItems(merchant, confiscatedItems, assets);
        } else {
            int penalty = Game.getGoodById(merchant.getBagGoodsType()).getPenalty();
            int givenMoney = merchant.getBagItems() * penalty;
            money -= givenMoney;
            merchant.addMoney(givenMoney);

        }
    }
    /**
     * @verify confiscate undeclared items
     */
    protected List<Integer> verifyGoods(final Player merchant) {
        int goodsType = merchant.getBagGoodsType();
        final List<Integer> confiscatedItems = new ArrayList<>();
        for (int i = 0; i < merchant.getBagItems(); i++) {
            int currentItem = merchant.getBagItem(i);
            Goods currentGood = Game.getGoodById(currentItem);
            if (currentItem != goodsType
                    || currentGood.getType() == GoodsType.Illegal) {
                confiscatedItems.add(currentItem);
                money += currentGood.getPenalty();
                merchant.addMoney(-currentGood.getPenalty());
            }
        }
        return confiscatedItems;
    }
    /**
     * @update confiscate item from merchant bag
     */
    protected void confiscateItems(final Player merchant, final List<Integer> confiscatedItems,
                                   final List<Integer> assets) {
        for (Integer confiscatedItem : confiscatedItems) {
            merchant.bag.deleteGood(confiscatedItem);
        }

        for (int i = 0; i < cards.getCardsNumber(); i++) {
            final int currentCard = cards.getCard(i);
            if (confiscatedItems.contains(currentCard)) {
                assets.add(currentCard);
                confiscatedItems.remove(currentCard);
            }
        }
    }

}
