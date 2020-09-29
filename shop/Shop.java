package com.tema1.shop;


import java.util.ArrayList;
import java.util.List;

public class Shop {
    private List<Integer> goods;
    private int goodsNumber;

    public Shop() {
        goods = new ArrayList<>();
        goodsNumber = 0;
    }
     /**
     * @update add a good in shop
     */
    public void addGood(final int goodId) {
        goods.add(goodId);
        goodsNumber++;
    }
    /**
     * @update get a good from shop
     */
    public int getGood(final int goodId) {
        if (goodId < goodsNumber) {
            return goods.get(goodId);
        }
        return -1;
    }
    /**
     * @return get shop goods number
     */
    public int getGoodsNumber() {
        return  goodsNumber;
    }
    /**
     * @return good frequency in shop
     */
    public int getNumberOfGoodsById(final int goodId) {
        int count = 0;
        for (int i = 0; i < goodsNumber; i++) {
            if (goodId == goods.get(i)) {
                count++;
            }
        }
        return count;
    }
}

