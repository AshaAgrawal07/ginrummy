package com.example;

import java.util.*;

/**
 * In this strategy, the player does care about the opponent's moves or feedback
 * the player will also be keeping track of the remaining cards from the pick-up pile to see which move will be most
 * beneficial to itself, but not to its opponent (its kind of guessed since player doesn't see opponent's melds
 * until the end of the round)
 * player will also try to discard the least beneficial card to the opponent to the discard-pile as long as it does not
 * compromise its own melds (best meld that it currently has)
 * taking point values into account, if there are multiple cards that can be discarded (which follows the restriction
 * placed above) then it will discard the
 */
public class hardPlayerStrategy implements PlayerStrategy {

    private Set<Card> cardsInHand = new HashSet<>();
    private SetMeld setMeld;
    private RunMeld runMeld;
    private Card previousDiscardedCard = null;
    private Card opponentDiscardedCard = null;

    /**
     * Called by the game engine for each player at the beginning of each round to receive and
     * process their initial hand dealt.
     *
     * @param hand The initial hand dealt to the player
     */
    @Override
    public void receiveInitialHand(List<Card> hand) {
        Card[] handAsArray = hand.toArray(new Card[hand.size()]);
        Arrays.sort(handAsArray);
        cardsInHand = new HashSet<>(Arrays.asList(handAsArray));

        setMeld = new SetMeld(hand);
        setMeld.setMelds(hand);
        runMeld = new RunMeld(hand.toArray(new Card[hand.size()]));
        runMeld.runMelds(hand);
    }

    /**
     * Called by the game engine to prompt the player on whether they want to take the top card
     * from the discard pile or from the deck.
     *
     * @param card The card on the top of the discard pile
     * @return whether the user takes the card on the discard pile
     */
    @Override
    public boolean willTakeTopDiscard(Card card) {
        int maxMeldLength = 0;
        List<Card> list = new ArrayList<>(cardsInHand);

        for (Meld runs : runMeld.runMelds(list)) {
            if (runs.canAppendCard(card)) {
                if (((Set) runs).size() > maxMeldLength) {
                    maxMeldLength = ((Set) runs).size();
                }
            }
        }

        for (Meld runs : setMeld.setMelds(list)) {
            if (runs.canAppendCard(card)) {
                if (((Set) runs).size() > maxMeldLength) {
                    maxMeldLength = ((Set) runs).size();
                }
            }
        }

        if(maxMeldLength == 0) {
            return false;
        }
        return true;
    }

    /**
     * Called by the game engine to prompt the player to take their turn given a
     * dealt card (and returning their card they've chosen to discard).
     * <p>
     * will use previousDiscardedCard and opponentDiscardedCard to see what kind of cards do not fit their runMelds or
     * setMelds.  Accordingly, as long as this player's melds are not compromised, then player will discard card a
     * similar card
     * <p>
     * want to ask myself:
     * 1. why did the opponent not need the previousDiscardedCard?
     * 2. why did the opponent need the opponentDiscardedCard?
     * this means, I get info on 2 melds that the opponent in not building (which also means 4 melds in total if we
     * take into account that they are also setMelds and runMelds
     *
     * @param drawnCard The card the player was dealt
     * @return The card the player has chosen to discard
     */
    @Override
    public Card drawAndDiscard(Card drawnCard) {
        cardsInHand.add(drawnCard);
        List<Card> list = new ArrayList<>(cardsInHand);
        //add to all possible melds
        for (Meld runs : runMeld.runMelds(list)) {
            if (runs.canAppendCard(drawnCard)) {
                runs.appendCard(drawnCard);
            }
        }

        for (Meld runs : setMeld.setMelds(list)) {
            if (runs.canAppendCard(drawnCard)) {
                runs.appendCard(drawnCard);
            }
        }

        Set<Card> deadweight = cardsInDeadweight();
        Iterator<Card> iterator = deadweight.iterator();
        Iterator<Card> iterator2 = cardsInHand.iterator();
        Card toDiscard = null;

        for(Card card: deadweight) {
            //check that the new card does not fit into meld definitions with either of the discarded cards
            if(!(card.getSuit().equals(previousDiscardedCard.getSuit()) &&
                    (card.getRankValue() == previousDiscardedCard.getRankValue() + 1) ||
                        card.getRankValue() == previousDiscardedCard.getRankValue() - 1)&&
                    !(card.getSuit().equals(opponentDiscardedCard.getSuit()) &&
                            (card.getRankValue() == opponentDiscardedCard.getRankValue() + 1) ||
                                card.getRankValue() == opponentDiscardedCard.getRankValue() - 1) &&
                    !card.getRank().equals(opponentDiscardedCard.getRank()) &&
                    !card.getRank().equals(previousDiscardedCard.getRank())) {
                //toDiscard will continuously update until the highest value that I can discard is set to it
                toDiscard = card;
            }
        }
        //if there is no such card, then I will remove the greatest value card that I have and hope that I don't disrupt a meld
        if (toDiscard == null) {
            toDiscard = (Card)((TreeSet) cardsInHand).last();
            cardsInHand.remove(toDiscard);
        }
        //update all melds since a meld of size 3 could've been disrupted and resort hand
        receiveInitialHand((List)cardsInHand);
        return toDiscard;
    }

    /**
     * Called by the game engine to prompt the player is whether they would like to
     * knock.
     *
     * @return True if the player has decided to knock
     */
    @Override
    public boolean knock() {
        if (calculateDeadweightPoints() <= 10) {
            return true;
        }
        return false;
    }

    /**
     * helper function that finds all of the deadweight cards in the current hadn
     *
     * @return deadweight cards in current hand
     */
    private Set<Card> cardsInDeadweight() {
        Set<Card> deadweightCards = cardsInHand;
        Set<Card> cardsInMelds = (Set) getMelds();
        deadweightCards.removeAll(cardsInMelds);
        return deadweightCards;
    }

    /**
     * helper function that calculates the pointvalue of the deadweight pile in the current hand
     *
     * @return points in hand
     */
    private int calculateDeadweightPoints() {
        int deadweightPoints = 0;
        for (Card card : cardsInDeadweight()) {
            deadweightPoints += card.getPointValue();
        }
        return deadweightPoints;
    }

    /**
     * Called by the game engine when the opponent has finished their turn to provide the player
     * information on what the opponent just did in their turn.
     *
     * @param drewDiscard        Whether the opponent took from the discard
     * @param previousDiscardTop What the opponent could have drawn from the discard if they chose to
     * @param opponentDiscarded  The card that the opponent discarded
     */
    @Override
    public void opponentEndTurnFeedback(boolean drewDiscard, Card previousDiscardTop, Card opponentDiscarded) {
        previousDiscardedCard = previousDiscardTop;
        opponentDiscardedCard = opponentDiscarded;
    }

    /**
     * Called by the game engine when the round has ended to provide this player strategy
     * information about their opponent's hand and selection of Melds at the end of the round.
     * <p>
     * don't think this bears any relevance to this player's strategy- this player will do nothing with opponent's
     * round feedback
     *
     * @param opponentHand  The opponent's hand at the end of the round
     * @param opponentMelds The opponent's Melds at the end of the round
     */
    @Override
    public void opponentEndRoundFeedback(List<Card> opponentHand, List<Meld> opponentMelds) {
        //does nothing
    }

    /**
     * Called by the game engine to allow access the player's current list of Melds.
     *
     * @return The player's list of melds.
     */
    @Override
    public List<Meld> getMelds() {
        List<Meld> melds = new ArrayList<>();
        melds.add(setMeld);
        melds.add(runMeld);
        return melds;
    }

    /**
     * Called by the game engine to allow this player strategy to reset its internal state before
     * competing it against a new opponent.
     */
    @Override
    public void reset() {
        cardsInHand.removeAll(cardsInHand);
    }
}
