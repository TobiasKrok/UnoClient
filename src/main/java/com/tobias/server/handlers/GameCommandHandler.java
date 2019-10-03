package com.tobias.server.handlers;

import com.tobias.game.GameManager;
import com.tobias.game.OpponentPlayer;
import com.tobias.game.Player;
import com.tobias.game.card.Card;
import com.tobias.game.card.CardColor;
import com.tobias.game.card.CardType;
import com.tobias.server.ServerConnection;
import com.tobias.server.command.Command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GameCommandHandler extends AbstractCommandHandler {

    private GameManager gameManager;
    private ServerConnection serverConnection;

    public GameCommandHandler(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
    }

    @Override
    public void process(Command command) {
        switch (command.getType()) {
            case GAME_START:
                this.gameManager = new GameManager();
                gameManager.createNewGame(new Player(serverConnection.getId()),parseOpponentPlayers(command.getData()));
                break;
            case GAME_SETCARD:
                gameManager.addCardToPlayer(parseCards(command.getData()));
                break;
            case GAME_SETTOPCARD:
                // We should only receive a single card here. That's why we reference index 0.
                gameManager.setTopCard(parseCards(command.getData()).get(0));
                break;
            case GAME_SETOPPONENTPLAYERCARDCOUNT:
                System.out.println(parseOpponentPlayerId(command.getData()));
                break;
        }
    }

    private List<Card> parseCards(String cmdStr) {
        List<Card> cards = new ArrayList<>();
        Pattern p = Pattern.compile("\\[(.*?)\\]");
        Matcher m = p.matcher(cmdStr);
        String[] props;
        while(m.find()) {
            props = m.group(1).split(",");
            Card card = new Card(CardType.valueOf(props[0]), CardColor.valueOf(props[1]),Integer.parseInt(props[2]));
            cards.add(card);
        }
        return cards;
    }
    private Map<Integer,Integer> parseOpponentPlayerId (String cmdStr){
        Map<Integer,Integer> ids = new HashMap<>();
        String[] arr = cmdStr.split(":");
        ids.put(Integer.parseInt(arr[0]),Integer.parseInt(arr[1]));
        return ids;
    }
    private List<OpponentPlayer> parseOpponentPlayers(String cmdStr) {
        List<OpponentPlayer> players = new ArrayList<>();
        for(String s : cmdStr.split(",")) {
            if(!(Integer.parseInt(s) == serverConnection.getId()))
            players.add(new OpponentPlayer(Integer.parseInt(s)));
        }
        return players;
    }
}
