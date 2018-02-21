import com.example.Card;
import com.example.GameEngine;
import com.example.PlayerStrategy;
import com.example.mediumPlayerStrategy;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.*;

public class mediumPlayerStrategy {
    private static mediumPlayerStrategy eps1 = new mediumPlayerStrategy();
    private static List<Card> deckAsList = new ArrayList<Card>(Card.getAllCards());
    private static Stack<Card> deck = new Stack<>();
    private static Stack<Card> playerHand = new Stack<>();
    private static Stack<Card> discardPile = new Stack<>();

    @Before
    public void setUp() throws Exception {
        deck.addAll(deckAsList);
        for (int j = 0; j < 10; j++) {
            playerHand.push(deck.pop());
        }

        eps1.receiveInitialHand(playerHand);
        discardPile.push(deck.pop());
    }
    //hand.add(deck.get(0));

    @Test
    public void getWillTakeTopDiscard() {
        Assertions.assertEquals(true, eps1.willTakeTopDiscard(discardPile.peek()));
    }

    @Test
    public void drawAndDiscardTest() {
        Card toDiscard = playerHand.firstElement();
        Assertions.assertEquals(toDiscard, eps1.drawAndDiscard(discardPile.pop()));
    }

    @Test
    public void knockTest() {
        Assertions.assertEquals(true, eps1.knock());
    }

    @Test
    public void getMeldsTest() {
        Assertions.assertEquals(playerHand, eps1.getMelds());
    }
}
