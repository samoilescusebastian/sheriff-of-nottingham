package com.tema1.game;

import com.tema1.Player.Player;
import com.tema1.Player.BasePlayer;
import com.tema1.Player.BribePlayer;
import com.tema1.Player.GreedyPlayer;
import com.tema1.Player.PlayerComparator;
import com.tema1.Player.PlayersType;
import com.tema1.goods.Bonus;
import com.tema1.goods.Goods;
import com.tema1.goods.GoodsFactory;
import com.tema1.goods.LegalGoods;
import com.tema1.main.GameInput;

import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Collections;

import static com.tema1.common.Constants.BRIBE_INPUT;
import static com.tema1.common.Constants.BASIC_INPUT;
import static com.tema1.common.Constants.GREEDY_INPUT;
import static com.tema1.common.Constants.ZERO;
import static com.tema1.common.Constants.LEGAL_CARDS;
import static com.tema1.common.Constants.UNKNOWN;
import static com.tema1.common.Constants.BRIBE_OUTPUT;
import static com.tema1.common.Constants.BASIC_OUTPUT;
import static com.tema1.common.Constants.GREEDY_OUTPUT;
import static com.tema1.common.Constants.NONE;


public final class Game {
    private static GoodsFactory hashMap;
    private GameInput gameInput;
    private int playersNumber;
    private List<Player> players;

    // helpful for adding King and Queen bonuses
    private Map<Integer, Integer> goodsFrequency;

    public Game(final GameInput gameInput) {
        players = new ArrayList<>();
        this.gameInput = gameInput;
        playersNumber = gameInput.getPlayerNames().size();
        goodsFrequency = new HashMap<>();
        hashMap = GoodsFactory.getInstance();
        initializePlayers();
    }

    public void startGame() {
        for (int currentRound = 0; currentRound < gameInput.getRounds(); currentRound++) {
            boolean evenRound = (currentRound + 1) % 2 == ZERO;
            for (int currentSheriff = 0; currentSheriff < playersNumber; currentSheriff++) {
                splitCards(currentSheriff);
                makeBags(currentSheriff, evenRound);
                inspectsBags(currentSheriff);
                unpackBags(currentSheriff);
            }
        }
    }
    public void viewResults() {
        computeScores();
        computeItemsFrequency();
        addKQBonuses();
        makeStanding();
        printResults();
    }

    // create appropriate type of player
    private void initializePlayers() {
        List<String> playerNames = gameInput.getPlayerNames();
        for (int i = 0; i < playersNumber; i++) {
            players.add(createPlayer(playerNames.get(i), i));
        }
    }

    private void splitCards(final int sheriffId) {
        for (int playerId = 0; playerId < playersNumber; playerId++) {
            if (playerId != sheriffId) {
                players.get(playerId).drawCards(gameInput.getAssetIds());
            }
        }
    }

    private void makeBags(final int sheriffId, final boolean evenRound) {
        for (int playerId = 0; playerId < playersNumber; playerId++) {
            if (playerId != sheriffId) {
                Player currentPlayer = players.get(playerId);
                currentPlayer.setBag();
                if (evenRound && currentPlayer.getType() == PlayersType.GreedyPlayer) {
                    ((GreedyPlayer) currentPlayer).addIllegalCard();
                }
            }
        }
    }
    private void inspectsBags(final int sheriffId) {
        Player sheriff = players.get(sheriffId);
        // treat different case of bribe sheriff
        if (sheriff.getType() == PlayersType.BribePlayer) {
            int leftPlayerId = sheriffId - 1;
            int rightPlayer = sheriffId + 1;
            // round settlement logic
            if (leftPlayerId < ZERO) {
                leftPlayerId = playersNumber - 1;
            }
            if (rightPlayer == playersNumber) {
                rightPlayer = ZERO;
            }
            if (leftPlayerId != rightPlayer) {
                sheriff.inspect(players.get(leftPlayerId), gameInput.getAssetIds());
                sheriff.inspect(players.get(rightPlayer), gameInput.getAssetIds());
            } else {
                sheriff.inspect(players.get(leftPlayerId), gameInput.getAssetIds());
            }
            // collect bribe from all unchecked merchants
            List<Integer> restrictions = new ArrayList<>();
            restrictions.add(sheriffId);
            restrictions.add(leftPlayerId);
            restrictions.add(rightPlayer);
            for (int playerId = 0; playerId < playersNumber; playerId++) {
                if (!restrictions.contains(playerId)) {
                    sheriff.addMoney(players.get(playerId).takeBagBribe());
                }
            }
        } else {
            for (int playerId = 0; playerId < playersNumber; playerId++) {
                if (playerId != sheriffId) {
                    sheriff.inspect(players.get(playerId), gameInput.getAssetIds());
                }
            }
        }
    }

    // string to player type
    private Player createPlayer(final String playerType, final int index) {
        Player player = null;
        if (playerType.equals(BASIC_INPUT)) {
            player = new BasePlayer(index);
        } else if (playerType.equals(GREEDY_INPUT)) {
            player = new GreedyPlayer(index);
        } else if (playerType.equals(BRIBE_INPUT)) {
            player = new BribePlayer(index);
        }
        return player;
    }

    // for each player, compute frequency of shop items
    private void computeItemsFrequency() {
        for (int i = 0; i < playersNumber; i++) {
            players.get(i).computeShopItemsFrequency(goodsFrequency);
        }
    }
    private void makeStanding() {
        Collections.sort(players, new PlayerComparator());
    }

    // put bags existing items in shop and take bribes back
    private void unpackBags(final int sheriffId) {
        for (int playerId = 0; playerId < playersNumber; playerId++) {
            if (playerId != sheriffId) {
                Player player = players.get(playerId);
                player.addMoney(player.takeBagBribe());
                player.getGoodsInShop();
            }

        }

    }
    private void computeScores() {
        for (Player player : players) {
            player.computeScore();
        }
    }
    private void initializeHashMap(final Map<Integer, Bonus> bonuses) {
        for (int i = 0; i < LEGAL_CARDS; i++) {
            bonuses.put(i, new Bonus());
        }

    }

    // for every item compute first and second max frequency, save ids of King and Queen
    private void makeBonusMap(final Map<Integer, Bonus> bonuses) {
        for (int i = 0; i < LEGAL_CARDS; i++) {
            for (Player player : players) {
                int goodsNumber = player.getNumberOfItemsInShopById(i);
                Bonus currentBonus = bonuses.get(i);
                if (goodsNumber > currentBonus.getKingCount()) {
                    currentBonus.setQueenCount(currentBonus.getKingCount());
                    currentBonus.setQueenId(currentBonus.getKingId());
                    currentBonus.setKingCount(goodsNumber);
                    currentBonus.setKingId(player.getPlayerId());
                } else if (goodsNumber > currentBonus.getQueenCount()) {
                    currentBonus.setQueenCount(goodsNumber);
                    currentBonus.setQueenId(player.getPlayerId());
                }
            }
        }
    }
    private void addKQBonuses() {
        Map<Integer, Bonus> bonuses = new HashMap<>();
        initializeHashMap(bonuses);
        makeBonusMap(bonuses);
        for (Integer goodId : bonuses.keySet()) {
            Goods good = hashMap.getGoodsById(goodId);
            int kingId = bonuses.get(goodId).getKingId();
            int queenId = bonuses.get(goodId).getQueenId();

            // UNKNOWN for a item that is not in any shop
            if (kingId != UNKNOWN) {
                players.get(kingId).addBonus(((LegalGoods) good).getKingBonus());
            }
            if (queenId != UNKNOWN) {
                players.get(queenId).addBonus(((LegalGoods) good).getQueenBonus());
            }
        }


    }

    private void printResults() {
        for (int i = 0; i < playersNumber; i++) {
            if (i > 0) {
                System.out.println();
            }
            Player player = players.get(i);
            System.out.print(player.getPlayerId() + " "
                             + playerTypeToString(player.getType()) + " "
                             + player.getScore());

        }
 }

    private String playerTypeToString(final PlayersType type) {
        if (type == PlayersType.BasePlayer) {
            return BASIC_OUTPUT;
        } else if (type == PlayersType.GreedyPlayer) {
            return GREEDY_OUTPUT;
        } else if (type == PlayersType.BribePlayer) {
            return BRIBE_OUTPUT;
        } else {
            return NONE;
        }

    }
    public static Goods getGoodById(final int id) {
        return hashMap.getGoodsById(id);
    }
}
