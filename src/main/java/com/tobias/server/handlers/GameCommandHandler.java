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
import javafx.scene.image.Image;
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
                gameManager.createNewGame(new ClientPlayer(serverConnection.getId()), parseOpponentPlayers(command.getData()));
                break;
            case GAME_SETCARD:
                List<Card> cards = parseCards(command.getData());
                gameManager.addCardToPlayer(cards);
                break;
            case GAME_SETTOPCARD:
                // We should only receive a single card here. That's why we reference index 0.
                if(!(command.getData().equals(""))) {
                    gameManager.setTopCard(parseCards(command.getData()).get(0));
           //         Main.getUnoController().setTopCard(parseCards(command.getData()).get(0));
                }
                break;
            case GAME_SETOPPONENTPLAYERCARDCOUNT:
                gameManager.setOpponentPlayerCardCount(parseOpponentPlayerId(command.getData()));
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
            case GAME_REQUESTCARD:
                serverConnection.write(new Command(CommandType.GAME_REQUESTCARD,command.getData()));
                break;
            case GAME_LAYCARD:
                serverConnection.write(new Command(CommandType.GAME_LAYCARD,command.getData()));
                break;
            case GAME_SKIPTURN:
                serverConnection.write(new Command(CommandType.GAME_SKIPTURN,String.valueOf(serverConnection.getId())));
            case GAME_SETCOLOR:
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
            Image cardImage;
            CardType cardType = CardType.valueOf(props[0]);
            if(cardType != CardType.NORMAL) {
              cardImage = Main.getUnoController().getCardImageByName(props[0] + "_" + Integer.parseInt(props[1]));
            } else {
               cardImage = Main.getUnoController().getCardImageByName(props[1] + "_" + Integer.parseInt(props[2]));
            }
            Card card = new Card(cardType, CardColor.valueOf(props[1]), Integer.parseInt(props[2]),cardImage);
            cards.add(card);
        }
        return cards;
    }

    private Map<Integer, Integer> parseOpponentPlayerId(String cmdStr) {
        Map<Integer, Integer> ids = new HashMap<>();
        String[] arr = cmdStr.split(":");
        ids.put(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]));
        return ids;
    }

    private List<OpponentPlayer> parseOpponentPlayers(String cmdStr) {
        List<OpponentPlayer> players = new ArrayList<>();
        for (String s : cmdStr.split(",")) {
            if (!(Integer.parseInt(s) == serverConnection.getId()))
                players.add(new OpponentPlayer(Integer.parseInt(s)));
        }
        return players;
    }
}
