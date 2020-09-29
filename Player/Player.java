package com.tema1.Player;

import com.tema1.bag.Bag;
import com.tema1.cards.Cards;
import com.tema1.game.Game;
import com.tema1.goods.Goods;
import com.tema1.goods.GoodsType;
import com.tema1.goods.IllegalGoods;
import com.tema1.shop.Shop;

import java.util.List;
import java.util.Map;

import static com.tema1.common.Constants.ZERO;
import static com.tema1.common.Constants.INITIAL_MONEY;
import static com.tema1.common.Constants.CARDS_IN_HAND;
public class Player {

    protected Shop shop;
    protected Bag bag;
    protected PlayersType type;
    protected Cards cards;

    protected int score;
    protected int money;
    protected int playerId;

    Player(final PlayersType type, final int playerId) {
        shop = new Shop();
        bag = new Bag();
        cards = new Cards();
        this.type = type;
        score = ZERO;
        money = INITIAL_MONEY;
        this.playerId = playerId;

    }
    /**
     * @return player score
     */

    public int getScore() {
        return score;
    }
    /**
     * @return bag goods number
     */
    protected int getBagItems() {
        return bag.getGoodsNumber();
    }
    /**
     * @return bag goods type
     */
    protected int getBagGoodsType() {
        return  bag.getGoodsType();
    }
    /**
     * @return bag good by index
     */
    protected int getBagItem(final int itemIndex) {
        return  bag.getGood(itemIndex);
    }
     /**
     * @return bag bribe
     */
    public int takeBagBribe() {
        int bribe = bag.getBribe();
        bag.takeBribe();
        return bribe;
    }
     /**
     * @return number of a good
     */
    public int getNumberOfItemsInShopById(final int goodId) {
        return shop.getNumberOfGoodsById(goodId);
    }
     /**
     * @add add a new good in bag
     */
    public void addMoney(final int moneyToAdd) {
        this.money += moneyToAdd;
    }
    /**
     * @update draw cards
     */
    public void drawCards(final List<Integer> assets) {
        while (assets.size() > ZERO && cards.getCardsNumber() < CARDS_IN_HAND) {
            cards.addCard(assets.get(ZERO));
            assets.remove(ZERO);
        }
    }
    /**
     * @update inspect bag
     */
    public void inspect(final Player merchant, final List<Integer> assets) {
        return;

    }
    /**
     * @update update store after a semi-round
     */
    public void getGoodsInShop() {
        for (int i = 0; i < bag.getGoodsNumber(); i++) {
            shop.addGood(bag.getGood(i));
            Goods good = Game.getGoodById(bag.getGood(i));
            if (good.getType() == GoodsType.Illegal) {
                Map<Goods, Integer> bonuses = ((IllegalGoods) good).getIllegalBonus();
                for (Goods bonusGood : bonuses.keySet()) {
                    int goodNumber = bonuses.get(bonusGood);
                    int goodId = bonusGood.getId();
                    for (int time = 0; time < goodNumber; time++) {
                        shop.addGood(goodId);
                    }

                }
            }
        }
        bag.emptyBag();
        cards.discardAll();
    }
    /**
     * @update get items frequency for KQ bonuses
     */
    public void computeShopItemsFrequency(final Map<Integer, Integer> itemFrequency) {
        for (int i = 0; i < shop.getGoodsNumber(); i++) {
            int itemId = shop.getGood(i);
            itemFrequency.put(itemId, itemFrequency.getOrDefault(itemId, 0) + 1);
        }

    }
    /**
     * @update get player score
     */
    public void computeScore() {
        for (int i = 0; i < shop.getGoodsNumber(); i++) {
            Goods good = Game.getGoodById(shop.getGood(i));
            score += good.getProfit();
        }
        score += money;
    }
    /**
     * @update add bonus to score
     */
    public void addBonus(final int bonus) {
        score += bonus;
    }
    /**
     * @override method to override
     */
    public void setBag() {
        return;
    }
    /**
     * @override toString method
     */
    @Override
    public String toString() {
        return "Player{" + "shop=" + shop + ", type=" + type + ", cards=" + cards
                         + ", score=" + score + ", money=" + money + ", playerId=" + playerId
                         + '}';
    }
    /**
     * @return type of player
     */
    public PlayersType getType() {
        return  type;
    }
    /**
     * @return id of player
     */
    public int getPlayerId() {
        return playerId;
    }

}
