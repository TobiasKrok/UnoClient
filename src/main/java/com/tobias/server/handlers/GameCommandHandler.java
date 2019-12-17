package com.tobias.server.handlers;

import com.tobias.Main;
import com.tobias.game.ClientPlayer;
import com.tobias.game.GameManager;
import com.tobias.game.OpponentPlayer;
import com.tobias.game.card.Card;
import com.tobias.game.card.CardColor;
import com.tobias.game.card.CardType;
import com.tobias.server.ServerConnection;
import com.tobias.server.command.Command;
import com.tobias.server.command.CommandType;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GameCommandHandler extends AbstractCommandHandler {

    private GameManager gameManager;
    private ServerConnection serverConnection;
    private static final Logger LOGGER = LogManager.getLogger(GameCommandHandler.class.getName());

    public GameCommandHandler(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
    }

    @Override
    public void process(Command command) {
        switch (command.getType()) {
            case GAME_START:
                this.gameManager = new GameManager();
                List<OpponentPlayer> opponents = parseOpponentPlayers(command.getData());
                gameManager.createNewGame(new ClientPlayer(serverConnection.getId()),opponents);
                // Add to UnoController view. This creates a new OpponentPlayerView
                for(OpponentPlayer player : opponents) {
                    Main.getUnoController().addOpponent(player);
                }
                break;
            case GAME_SETCARD:
                List<Card> cards = parseCards(command.getData());
                gameManager.addCardToPlayer(cards);
                Main.getUnoController().addCardToPlayer(cards);
                break;
            case GAME_SETOPPONENTPLAYERCARDCOUNT:
                Map<Integer, Integer> cardCounts = parseOpponentPlayerIdCardCount(command.getData());
                gameManager.setOpponentPlayerCardCount(cardCounts);
                cardCounts.forEach((key,value) ->{
                    if(key != serverConnection.getId()) {
                        Platform.runLater(() ->  Main.getUnoController().getOpponentPlayerViewById(key).setCardCount(value));
                    }
                });
                break;
            case GAME_SETNEXTTURN:
                gameManager.setNextTurn(Integer.parseInt(command.getData()));
                break;
            case GAME_SETDECKCOUNT:
                gameManager.setDeckCount(Integer.parseInt(command.getData()));
          //      Main.getUnoController().setDeckCount(Integer.parseInt(command.getData()));
                break;
            case GAME_PLAYERDISCONNECT:
                gameManager.disconnectPlayer(Integer.parseInt(command.getData()));
                break;
            case GAME_CLIENTDRAWCARD:
                serverConnection.write(new Command(CommandType.GAME_CLIENTDRAWCARD,command.getData()));
                break;
            case GAME_OPPONENTDRAWCARD:
                Map<Integer, Integer> map = parseOpponentPlayerIdCardCount(command.getData());
                Map.Entry<Integer, Integer> entry = map.entrySet().iterator().next();
                Platform.runLater(()-> Main.getUnoController().addCardToOpponent(entry.getKey(),entry.getValue()));
                break;
            case GAME_CLIENTLAYCARD:
                serverConnection.write(new Command(CommandType.GAME_CLIENTLAYCARD,command.getData()));
                break;
            case GAME_OPPONENTLAYCARD:
                Map<Integer, Card> opponentInfo = parseOpponentPlayerLayCard(command.getData());
                // Get first value in map. Size should only be 1.
                Map.Entry<Integer,Card> mapEntry = opponentInfo.entrySet().iterator().next();
                Main.getUnoController().opponentAddCardToTable(mapEntry.getKey(),mapEntry.getValue());
                break;
            case GAME_SKIPTURN:
                serverConnection.write(new Command(CommandType.GAME_SKIPTURN,String.valueOf(serverConnection.getId())));
            case GAME_SETCOLOR:
                //TODO implement
                break;
            default:
                LOGGER.error("Could not process command: " + command.toString());
                break;
        }
    }

    private List<Card> parseCards(String cmdStr) {
        List<Card> cards = new ArrayList<>();
        Pattern p = Pattern.compile("\\[(.*?)\\]");
        Matcher m = p.matcher(cmdStr);
        String[] props;
        while (m.find()) {
            props = m.group(1).split(",");
            // Index 0 = CardType
            // Index 1 = CardColor
            // Index 2 = CardValue
            // Length will always be 3.
            ImageView cardImage;
            CardType cardType = CardType.valueOf(props[0]);
            if(cardType != CardType.NORMAL) {
              cardImage = Main.getUnoController().getCardImageViewByName(props[1] + "_" + props[0]);
            } else {
               cardImage = Main.getUnoController().getCardImageViewByName(props[1] + "_" + Integer.parseInt(props[2]));
            }
            Card card = new Card(cardType, CardColor.valueOf(props[1]), Integer.parseInt(props[2]),cardImage);
            cards.add(card);
        }
        return cards;
    }
    private Map<Integer, Card> parseOpponentPlayerLayCard(String cmdStr) {
        Map<Integer, Card> map = new HashMap<>();
        String[] arr = cmdStr.split(":");
        // Only 1 card is passed so we get index 0
        Card c = parseCards(cmdStr).get(0);
        map.put(Integer.parseInt(arr[0]),c);
        return map;
    }
    private Map<Integer, Integer> parseOpponentPlayerIdCardCount(String cmdStr) {
        Map<Integer, Integer> ids = new HashMap<>();
        String[] arr = cmdStr.split(":");
        ids.put(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]));
        return ids;
    }

    private List<OpponentPlayer> parseOpponentPlayers(String cmdStr) {
        List<OpponentPlayer> players = new ArrayList<>();
        for (String s : cmdStr.split(",")) {
            if (!(Integer.parseInt(s) == serverConnection.getId()))
                players.add(new OpponentPlayer(Integer.parseInt(s),s));
        }
        return players;
    }
}
