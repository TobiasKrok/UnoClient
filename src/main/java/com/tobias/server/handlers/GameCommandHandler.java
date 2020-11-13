package com.tobias.server.handlers;

import com.tobias.Main;
import com.tobias.game.GameManager;
import com.tobias.game.OpponentPlayer;
import com.tobias.game.card.Card;
import com.tobias.game.card.CardColor;
import com.tobias.game.card.CardType;
import com.tobias.server.ServerConnection;
import com.tobias.server.command.Command;
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
import java.util.stream.Collectors;


public class GameCommandHandler implements CommandHandler {

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
                try {
                    Main.loadGameWindow();
                } catch (Exception e) {
                    //todo better catch
                    e.printStackTrace();
                }
                this.gameManager = new GameManager();
                Main.getUnoController().deckAddCardToTable(parseCards(command.getData()).get(0));
                // Filter only OpponentPlayers objects and then add them as opponentplayers in the uno view
                List<OpponentPlayer> players = Main.getLobbyController().getConnectedPlayers().stream()
                        .filter(o -> o instanceof OpponentPlayer)
                        .map(o -> (OpponentPlayer) o)
                        .collect(Collectors.toList());
                Main.getUnoController().addOpponents(players);
                //Send a command letting the server know that the Uno window has been set up and
                // the player is ready to receive cards

                break;
            case GAME_SETCARD:
                List<Card> cards = parseCards(command.getData());

                gameManager.addCardToPlayer(cards);
                Main.getUnoController().addCardToPlayer(cards);
                Main.getUnoController().setWaitingForCard(false);
                break;
            case GAME_SETOPPONENTPLAYERCARDCOUNT:
                Map<Integer, Integer> cardCounts = parseOpponentPlayerIdCardCount(command.getData());
                gameManager.setOpponentPlayerCardCount(cardCounts);
                cardCounts.forEach((key,value) -> {
                    if(key != serverConnection.getId()) {
                        Platform.runLater(() ->  Main.getUnoController().getOpponentPlayerViewById(key).setCardCount(value));
                    }
                });
                break;
            case GAME_SETNEXTTURN:
                int id = Integer.parseInt(command.getData());
                gameManager.setNextTurn(id);
                if(gameManager.isClientPlayerTurn()) {
                    // -1 means that is it the clients turn
                    Main.getUnoController().setNextPlayerTurn(-1);
                } else {
                    Main.getUnoController().setNextPlayerTurn(id);
                }
                break;
                //todo remove
            case GAME_SETDECKCOUNT:
                gameManager.setDeckCount(Integer.parseInt(command.getData()));
          //      Main.getUnoController().setDeckCount(Integer.parseInt(command.getData()));
                break;
            case GAME_PLAYERDISCONNECT:
                gameManager.disconnectPlayer(Integer.parseInt(command.getData()));
                break;
            case GAME_OPPONENTDRAWCARD:
                Map<Integer, Integer> map = parseOpponentPlayerIdCardCount(command.getData());
                Map.Entry<Integer, Integer> entry = map.entrySet().iterator().next();
                Platform.runLater(()-> Main.getUnoController().addCardToOpponent(entry.getKey(),entry.getValue()));
                break;
            case GAME_OPPONENTLAYCARD:
                //todo pretty sure its possible to get the values without creating an iterator.
                Map<Integer, Card> opponentInfo = parseOpponentPlayerLayCard(command.getData());
                // Get first value in map. Size should only be 1.
                Map.Entry<Integer,Card> mapEntry = opponentInfo.entrySet().iterator().next();
                Main.getUnoController().opponentAddCardToTable(mapEntry.getKey(),mapEntry.getValue());
                break;
            case GAME_SKIPTURN:
                serverConnection.write(command);
            case GAME_SETCOLOR:
                if(command.getData().isEmpty() || command.getData().equals("NONE")) {
                    Platform.runLater(() -> Main.getUnoController().setNextColor(CardColor.NONE));
                } else {
                    Platform.runLater(() -> Main.getUnoController().setNextColor(CardColor.valueOf(command.getData())));
                    Main.getUnoController().addClientNotificationMessage("Next card color has been set to " + command.getData());
                }
                break;
            case GAME_UNO:
                // If data is empty, it means that the client is the one that sent this command and not the server.
                if(command.getData().isEmpty()) {
                    serverConnection.write(command);
                } else {
                    String userName = Main.getUnoController().getOpponentPlayerViewById(Integer.parseInt(command.getData())).getUsername();
                    Main.getUnoController().addClientNotificationMessage(userName + " has pressed UNO!");
                }
                break;

            case GAME_FORGOTUNO:
                if(command.getData().isEmpty() || command.getData().equals("OPPONENT") ) {
                    serverConnection.write(command);
                } else {
                    // If data is not empty or is not equal to OPPONENT, the data contains the ID of the player that forgot
                    // We must then show the forgotUNo button
                    String userName = Main.getUnoController().getOpponentPlayerViewById(Integer.parseInt(command.getData())).getUsername();
                    Main.getUnoController().addClientNotificationMessage(userName + " forgot to press UNO!");
                    Main.getUnoController().showForgotUnoButton();
                }
                break;
            case GAME_FINISHED:
                String username;
                if(Integer.parseInt(command.getData()) == serverConnection.getId()) {
                    username = "YOU";
                } else {
                 username=Main.getUnoController().getOpponentPlayerViewById(Integer.parseInt(command.getData())).getUsername();
                }
                Platform.runLater(() -> Main.getUnoController().showGameWonLabel(username));
                break;
            // all of the below commands just directly send the command to the server without any processing. here we just merge them because they all do the same.
            case GAME_CLIENTDRAWCARD:
            case GAME_CLIENTLAYCARD:
            case GAME_CLIENTSETCOLOR:
                serverConnection.write(command);
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
