package com.tema1.bag;

import java.util.ArrayList;
import java.util.List;
import static com.tema1.common.Constants.BAG_CAPACITY;
import static com.tema1.common.Constants.ZERO;

public final class Bag {
    private List<Integer> goods;
    private int goodsNumber;
    private int bribe;
    private int goodsType;
    private boolean checked;
    public Bag() {
        goods = new ArrayList<>();
        goodsNumber = ZERO;
        bribe = ZERO;
        checked = false;
    }
    public void setBag(final List<Integer> items, final int currentBribe,
                       final int currentGoodsType) {
        for (int item : items) {
            this.goods.add(item);
        }
        goodsNumber = items.size();
        bribe = currentBribe;
        goodsType = currentGoodsType;
    }
    public int getGoodsNumber() {
        return  goodsNumber;
    }
    public int getGood(final int itemIndex) {
        return goods.get(itemIndex);
    }
    public int getGoodsType() {
        return goodsType;
    }
    public void emptyBag() {
        goods.clear();
        goodsNumber = ZERO;
    }
    public void addGood(final int item) {
        goods.add(item);
        goodsNumber++;
    }
    public void deleteGood(final int item) {
        goods.remove(Integer.valueOf(item));
        goodsNumber--;
    }
    public boolean isFull() {
        return goodsNumber == BAG_CAPACITY;
    }
    public int getBribe() {
        return bribe;
    }
    public void takeBribe() {
        bribe = 0;
    }
    public void setChecked() {
        checked = true;
    }

}
