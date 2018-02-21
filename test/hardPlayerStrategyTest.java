import com.example.Card;
import com.example.*;
import com.example.GameEngine;
import com.example.PlayerStrategy;
import com.example.easyPlayerStrategy;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.*;

public class hardPlayerStrategyTest {
    private static hardPlayerStrategy eps1 = new hardPlayerStrategy();
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
        List<Card> hand = (List) playerHand;
        eps1.recieveInitialHand((hand));
        discardPile.push(deck.pop());
    }
    //hand.add(deck.get(0));


    @Test
    public void getWillTakeTopDiscard() {
        Assertions.assertEquals(false, eps1.WillTakeTopDiscard(discardPile.peek()));
    }

}
