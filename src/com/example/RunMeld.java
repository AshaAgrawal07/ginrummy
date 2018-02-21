package com.example;

import java.lang.reflect.Array;
import java.util.*;

/**
 * A com.example.RunMeld represents a run of three or more cards of the same suit but sequential rank. Ace is a low card only.
 * {4 of Spades, 5 of Spades, 6 of Spades} is a valid com.example.RunMeld.
 * {Ace of Diamonds, 2 of Diamonds, 3 of Diamonds, 4 of Diamonds} is a valid com.example.RunMeld.
 * {Queen of Clubs, King of Clubs, Ace of Clubs} is not a valid com.example.RunMeld.
 * {6 of Hearts, 7 of Hearts, 8 of Diamonds} is not a valid com.example.RunMeld.
 */
public class RunMeld extends Meld {
    public static final int MIN_CARDS = 3;
    public static final int MAX_CARDS = 10;

    private List<Card> cardsInMeld;


    protected RunMeld(Card[] initialCards) {
        super();

        cardsInMeld = new ArrayList<Card>(Arrays.asList(initialCards));
    }

    /**
     * manually creates the runMelds that will be built later using the Meld class
     *
     * @return the list of Melds
     */
    public List<Meld> runMelds(List<Card> cards) {
        List<Card> hand = new ArrayList<>(cards);
        List<Meld> runMeldList = new ArrayList<>();

        for (Card card : cardsInMeld) {
            hand.add(card);
        }

        //use an array to sort into ascending order
        Card[] meldsAsArray = hand.toArray(new Card[hand.size()]);
        Arrays.sort(meldsAsArray);
        hand = Arrays.asList(meldsAsArray);

        //I will continue to loop through the arraylist until i downsize it completely and take out all possible runMelds
        //I am assuming that it is possible to have more than 2 runMelds in a single hand
        while (hand.size() != 0) {
            ArrayList<Card> meldToAdd = new ArrayList<>();
            meldToAdd.add(hand.get(0));
            hand.remove(0);

            //check through melds to get the runMelds that I can add
            for (Card card : hand) {
                //check if the card can be placed +1 in relation to previous card in meldsToAdd and is the same suit
                if (card.getSuit().equals(meldToAdd.get(0).getSuit())
                        && card.getRankValue() == meldToAdd.get(meldToAdd.size() - 1).getRankValue() + 1) {
                    meldToAdd.add(card);
                } else if (card.getSuit().equals(meldToAdd.get(0).getSuit())) {
                    hand.remove(card);
                }
            }

            //check if it fits the size parameters of a meld; if so, then add to runMeldList
            if (meldToAdd.size() >= MIN_CARDS) {
                runMeldList.add(Meld.buildRunMeld(meldToAdd));
            }
        }
        return runMeldList;
    }

    @Override
    public boolean containsCard(Card cardToCheck) {
        return cardsInMeld.contains(cardToCheck);
    }

    @Override
    public boolean canAppendCard(Card newCard) {
        if (cardsInMeld.size() >= MAX_CARDS || cardsInMeld.contains(newCard)) {
            return false;
        }

        if (newCard.getSuit() != cardsInMeld.get(0).getSuit()) {
            return false;
        }

        int firstCardRankValue = cardsInMeld.get(0).getRankValue();
        int lastCardRankValue = cardsInMeld.get(cardsInMeld.size() - 1).getRankValue();
        int newRankValue = newCard.getRankValue();

        return (newRankValue == firstCardRankValue - 1) || (newRankValue == lastCardRankValue + 1);
    }

    @Override
    public void appendCard(Card newCard) {
        if (!canAppendCard(newCard)) {
            throw new IllegalMeldModificationException();
        }

        int lastCardRankValue = cardsInMeld.get(cardsInMeld.size() - 1).getRankValue();
        int newRankValue = newCard.getRankValue();

        if (lastCardRankValue < newRankValue) {
            cardsInMeld.add(newCard);
        } else {
            cardsInMeld.add(0, newCard);
        }
    }

    @Override
    public boolean canRemoveCard(Card cardToRemove) {
        if (cardsInMeld.size() <= MIN_CARDS || !cardsInMeld.contains(cardToRemove)) {
            return false;
        }

        int indexOfCard = cardsInMeld.indexOf(cardToRemove);
        return (indexOfCard == 0) || (indexOfCard == cardsInMeld.size() - 1);
    }

    @Override
    public void removeCard(Card cardToRemove) {
        if (!canRemoveCard(cardToRemove)) {
            throw new IllegalMeldModificationException();
        }

        cardsInMeld.remove(cardToRemove);
    }

    @Override
    public Card[] getCards() {
        Card[] arrayToReturn = new Card[cardsInMeld.size()];
        return cardsInMeld.toArray(arrayToReturn);
    }
}
