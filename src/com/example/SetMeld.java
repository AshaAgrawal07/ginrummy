package com.example;

import java.util.*;

/**
 * A com.example.SetMeld represents a set of three or more cards that are of the same rank but different suit.
 * {Ace of Spades, Ace of Diamonds, Ace of Clubs} represents a valid com.example.SetMeld
 * {2 of Diamonds, 2 of Clubs} is not a valid com.example.SetMeld
 * {3 of Clubs, 3 of Spades, 3 of Hearts, 4 of Diamonds} is not a valid com.example.SetMeld
 */
public class SetMeld extends Meld {
    public static final int MIN_CARDS = 3;
    public static final int MAX_CARDS = 4;

    private Set<Card> cardsInMeld;

    protected SetMeld(Collection<Card> initialCards) {
        super();

        cardsInMeld = new HashSet<Card>(initialCards);
    }

    /**
     * manually creates the setMelds that will be built later using the Meld class
     *
     * @return the list of Melds
     */
    public List<Meld> setMelds(List<Card> cards) {
        List<Meld> setMeldList = new ArrayList<>();
        ArrayList<Card> hand = new ArrayList<>(cards);

        //use an array to sort into ascending order
        Card[] cardsAsArray = cards.toArray(new Card[cards.size()]);
        Arrays.sort(cardsAsArray);

        //I will continue to loop through the arraylist until i downsize it completely and take out all possible runMelds
        //I am assuming that it is possible to have more than 2 runMelds in a single hand
        while (hand.size() != 0) {
            ArrayList<Card> meldToAdd = new ArrayList<>();
            meldToAdd.add(hand.get(0));
            hand.remove(0);

            //check through melds to get the setMelds that I can add
            for (Card card : hand) {
                //check if the card does not have the same suit as the previous card, but has the same rank
                if (!card.getSuit().equals(meldToAdd.get(0).getSuit())
                        && card.getRankValue() == meldToAdd.get(meldToAdd.size() - 1).getRankValue()) {
                    meldToAdd.add(card);
                } else if (card.getSuit().equals(meldToAdd.get(0).getSuit())) {
                    hand.remove(card);
                }
            }

            //check if it fits the size parameters of a meld; if so, then add to runMeldList
            if (meldToAdd.size() >= MIN_CARDS) {
                setMeldList.add(Meld.buildSetMeld(meldToAdd));
            }
        }
        return setMeldList;
    }

    protected SetMeld(Card[] initialCards) { this(Arrays.asList(initialCards));
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

        Card firstCard = cardsInMeld.iterator().next();
        return (newCard.getRank() == firstCard.getRank());
    }

    @Override
    public void appendCard(Card newCard) {
        if (!canAppendCard(newCard)) {
            throw new IllegalMeldModificationException();
        }

        cardsInMeld.add(newCard);
    }

    @Override
    public boolean canRemoveCard(Card cardToRemove) {
        if (cardsInMeld.size() <= MIN_CARDS || !cardsInMeld.contains(cardToRemove)) {
            return false;
        }

        return true;
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
