package com.example;

import java.lang.reflect.Array;
import java.util.*;

public class GameEngine {

    final static int AWARD_BONUS_POINTS = 25;
    final static int POINTS_THRESHOLD = 50;
    final static int ROUNDS = 1000;

    public static Stack<Card> shuffle(Stack<Card> deck) {
        Card[] deckToArray = deck.toArray(new Card[deck.size()]);
        //i used this link to find out how to shuffle: https://www.geeksforgeeks.org/shuffle-a-deck-of-cards-3/
        Random rand = new Random();
        for (int i = 0; i < deckToArray.length; i++) {
            int r = i + rand.nextInt(deckToArray.length - i);
            Card temp = deckToArray[r];
            deckToArray[r] = deckToArray[i];
            deckToArray[i] = temp;
        }

        //used this link to cast array to stack: https://www.java-forums.org/new-java/3131-how-convert-java-array-java-stack.html
        List<Card> list = Arrays.asList(deckToArray);
        Stack<Card> shuffledDeck = new Stack<>();
        shuffledDeck.addAll(list);
        return shuffledDeck;
    }


    private static Set<Card> cardsInDeadweight(Stack<Card> hand, PlayerStrategy ps) {
        Set<Card> deadweightCards = (Set) hand;
        Set<Card> cardsInMelds = (Set) ps.getMelds();
        deadweightCards.removeAll(cardsInMelds);
        return deadweightCards;
    }

    private static int calculateDeadweightPoints(Stack<Card> hand, PlayerStrategy ps) {
        int deadweightPoints = 0;
        for (Card card : cardsInDeadweight(hand, ps)) {
            deadweightPoints += card.getPointValue();
        }
        return deadweightPoints;
    }

    /**
     * @param hand1 the hand of the player who called knock
     * @param ps1   the knocker
     * @param hand2 the hand of the player who didn't knock
     * @param ps2   the non-knocker
     * @return the difference btwn the deadweights of both players.
     * positive if ps1 is real winner, negative if ps2 actually has lower deadweight
     */
    public static int getWinner(Stack<Card> hand1, PlayerStrategy ps1, Stack<Card> hand2, PlayerStrategy ps2) {
        int ps1DWP = calculateDeadweightPoints(hand1, ps1);
        int ps2DWP = calculateDeadweightPoints(hand2, ps2);
        return ps1DWP - ps2DWP;
    }


    //make deck and discardPile stacks because its lifo
    public static void main(String[] args) {
        //EASY VS MEDIUM
        for (int i = 0; i < ROUNDS; i++) {
            //the playerStrategies can be switched around to compete with the other ones.
            PlayerStrategy ps1 = new easyPlayerStrategy();
            PlayerStrategy ps2 = new mediumPlayerStrategy();
            int player1Points = 0;
            int player2Points = 0;
            List<Card> deckAsList = new ArrayList<Card>(Card.getAllCards());
            Stack<Card> deck = new Stack<>();
            deck.addAll(deckAsList);
            shuffle(deck);

            //DETERMINE FIRST PLAYER
            double determineFirstPlayer = Math.random();
            PlayerStrategy firstPlayer;
            PlayerStrategy secondPlayer;

            if (determineFirstPlayer < .5) {
                firstPlayer = ps1;
                secondPlayer = ps2;
            } else {
                firstPlayer = ps2;
                secondPlayer = ps1;
            }

            Stack<Card> player1Hand = new Stack<>();
            Stack<Card> player2Hand = new Stack<>();
            for (int j = 0; j < 10; j++) {
                player1Hand.push(deck.pop());
                player2Hand.push(deck.pop());
            }

            firstPlayer.receiveInitialHand((List) player1Hand);
            secondPlayer.receiveInitialHand((List) player2Hand);

            Stack<Card> discardPile = new Stack<>();
            discardPile.push(deck.pop());

            boolean willTakeDiscard = false;
            willTakeDiscard = firstPlayer.willTakeTopDiscard(discardPile.peek());
            Card willTake;

            if (willTakeDiscard) {
                willTake = discardPile.pop();
            } else {
                willTake = deck.pop();
            }

            discardPile.push(firstPlayer.drawAndDiscard(willTake));
            firstPlayer.opponentEndTurnFeedback(willTakeDiscard, willTake, discardPile.peek());


            while (player1Points < POINTS_THRESHOLD || player2Points < POINTS_THRESHOLD) {
                willTakeDiscard = secondPlayer.willTakeTopDiscard(discardPile.peek());
                if (willTakeDiscard) {
                    willTake = discardPile.pop();
                } else {
                    willTake = deck.pop();
                }

                discardPile.push(secondPlayer.drawAndDiscard(willTake));
                secondPlayer.opponentEndTurnFeedback(willTakeDiscard, willTake, discardPile.peek());

                if (secondPlayer.knock()) {
                    int deadweightDifference = getWinner(player2Hand, secondPlayer, player1Hand, firstPlayer);
                    if (deadweightDifference < 0) {
                        player1Points += (-1 * deadweightDifference);
                    } else {
                        player2Points += deadweightDifference;
                    }
                }

                //turn goes back to player 1

                willTakeDiscard = firstPlayer.willTakeTopDiscard(discardPile.peek());
                if (willTakeDiscard) {
                    willTake = discardPile.pop();
                } else {
                    willTake = deck.pop();
                }

                discardPile.push(firstPlayer.drawAndDiscard(willTake));
                firstPlayer.opponentEndTurnFeedback(willTakeDiscard, willTake, discardPile.peek());

                if (firstPlayer.knock()) {
                    int deadweightDifference = getWinner(player1Hand, firstPlayer, player2Hand, secondPlayer);
                    if (deadweightDifference < 0) {
                        player2Points += (-1 * deadweightDifference);
                    } else {
                        player1Points += deadweightDifference;
                    }
                }

                if (player1Points >= POINTS_THRESHOLD) {
                    System.out.println("Winner: player1 ");
                    firstPlayer.opponentEndRoundFeedback(player1Hand, firstPlayer.getMelds());
                }
                if (player2Points >= POINTS_THRESHOLD) {
                    System.out.println("Winner: player2 ");
                    secondPlayer.opponentEndRoundFeedback(player2Hand, secondPlayer.getMelds());
                }
            }
        }
    }

}
