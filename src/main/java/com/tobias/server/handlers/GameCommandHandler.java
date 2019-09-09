package com.tobias.server.handlers;

import com.tobias.game.GameManager;
import com.tobias.game.card.Card;
import com.tobias.game.card.CardColor;
import com.tobias.game.card.CardType;
import com.tobias.server.ServerConnection;
import com.tobias.server.command.Command;
import com.tobias.server.command.CommandType;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GameCommandHandler implements CommandHandler {

    private GameManager gameManager;
    private ServerConnection serverConnection;
    public GameCommandHandler(GameManager gameManager, ServerConnection serverConnection) {
        this.gameManager = gameManager;
    }

    @Override
    public void process(Command command) {
        if(command.getType() == CommandType.PLAYER_DRAWCARD) {
            
        }
    }

    private List<Card> parseCards(String cardStr) {
        List<Card> cards = new ArrayList<>();
        Pattern p = Pattern.compile("\\[(.*?)\\]");
        Matcher m = p.matcher(cardStr);
        String[] props;
        while(m.find()) {
            props = m.group(1).split(",");
            Card card = new Card(CardType.valueOf(props[0]), CardColor.valueOf(props[1]),Integer.parseInt(props[2]));
            cards.add(card);
        }
        return cards;
    }
}
