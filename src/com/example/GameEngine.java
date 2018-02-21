package com.example;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class GameEngine {

    final static int AWARD_BONUS_POINTS = 25;
    final static int POINTS_THRESHOLD = 50;
    final static int ROUNDS = 1000;

    public static Stack<Card> shuffle(Stack<Card> deck) {
        Card[] deckToArray = deck.toArray(new Card[deck.size()]);
        //i used this link to find out how to shuffle: https://www.geeksforgeeks.org/shuffle-a-deck-of-cards-3/
        Random rand = new Random();
        for(int i = 0; i < deckToArray.length; i++) {
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

    public static PlayerStrategy getWinner(PlayerStrategy ps1, PlayerStrategy ps2) {
        if(ps1.)
    }

    //make deck and discardPile stacks because its lifo
    public static void main(String[] args) {
        //easy vs medium
        for(int i =  0; i < ROUNDS; i++) {
            PlayerStrategy ps1 = new easyPlayerStrategy();
            PlayerStrategy ps2 = new mediumPlayerStrategy();
            int player1Points = 0;
            int player2Points = 0;
            Stack<Card> deck = (Stack) Card.getAllCards();
            shuffle(deck);

            Stack<Card> player1Hand = new Stack<>();
            Stack<Card> player2Hand = new Stack<>();
            for(int j = 0; j < 10; j++) {
                player1Hand.push(deck.pop());
                player2Hand.push(deck.pop());
            }

            ps1.receiveInitialHand((List)player1Hand);
            ps2.receiveInitialHand((List)player2Hand);

            Stack<Card> discardPile = new Stack<>();
            discardPile.push(deck.pop());

            double determineFirstPlayer = Math.random();
            PlayerStrategy firstPlayer;
            PlayerStrategy secondPlayer;

            if(determineFirstPlayer < .5) {
                firstPlayer = ps1;
                secondPlayer = ps2;
            } else {
                firstPlayer = ps2;
                secondPlayer = ps1;
            }

            if(firstPlayer.willTakeTopDiscard(discardPile.peek())) {
                discardPile.push(firstPlayer.drawAndDiscard(discardPile.pop()));
            }

            while(player1Points < POINTS_THRESHOLD || player2Points < POINTS_THRESHOLD) {
                if(secondPlayer.willTakeTopDiscard(discardPile.peek())) {
                    discardPile.push(secondPlayer.drawAndDiscard(discardPile.pop()));
                }

                if(firstPlayer.willTakeTopDiscard(discardPile.peek())) {
                    discardPile.push(firstPlayer.drawAndDiscard(discardPile.pop()));
                }
            }
        }
    }

}
