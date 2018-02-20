package com.example;

import java.util.*;

/**
 * In this strategy, the player does not care about the opponent's moves or feedback
 * player will never pick a card up from the discard pile regardless of whether or not the discarded card benefits
 *      the player's current melds
 */
public class mediumPlayerStrategy implements PlayerStrategy{

    private Set<Card> cardsInHand = new HashSet<>();
    private SetMeld setMeld;
    private RunMeld runMeld;
    /**
     * Called by the game engine for each player at the beginning of each round to receive and
     * process their initial hand dealt.
     *
     * @param hand The initial hand dealt to the player
     */
    @Override
    public void receiveInitialHand(List<Card> hand) {
        cardsInHand = (Set)hand;
        setMeld = new SetMeld(hand);
        setMeld.setMelds();
        runMeld = new RunMeld(hand.toArray(new Card[hand.size()]));
        runMeld.runMelds();
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
        return false;
    }

    /**
     * Called by the game engine to prompt the player to take their turn given a
     * dealt card (and returning their card they've chosen to discard).
     *
     * @param drawnCard The card the player was dealt
     * @return The card the player has chosen to discard
     */
    @Override
    public Card drawAndDiscard(Card drawnCard) {
        //add to melds
        if(runMeld.canAppendCard(drawnCard)) { runMeld.appendCard(drawnCard);}
        if(setMeld.canAppendCard(drawnCard)) { setMeld.appendCard(drawnCard);}

        Set<Card> deadweight = cardsInDeadweight();
        Iterator<Card> iterator = deadweight.iterator();
        Iterator<Card> iterator2 = cardsInHand.iterator();
        Card toDiscard;

        if(!deadweight.isEmpty()) {
            toDiscard = iterator.next();
            iterator.remove();
            while(iterator2.hasNext()) {
                if (toDiscard.equals(iterator2.next())) {
                    iterator2.remove();
                }
            }
        } else {
            toDiscard = iterator2.next();
            iterator2.remove();
        }
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
        if(calculateDeadweightPoints() <= 10) {
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
        for(Card card: cardsInDeadweight()) {
            deadweightPoints += card.getPointValue();
        }
        return deadweightPoints;
    }

    /**
     * Called by the game engine when the opponent has finished their turn to provide the player
     * information on what the opponent just did in their turn.
     *
     * For easyPlayerStrategy, it does absolutely nothing
     *
     * @param drewDiscard        Whether the opponent took from the discard
     * @param previousDiscardTop What the opponent could have drawn from the discard if they chose to
     * @param opponentDiscarded  The card that the opponent discarded
     */
    @Override
    public void opponentEndTurnFeedback(boolean drewDiscard, Card previousDiscardTop, Card opponentDiscarded) {
        //does nothing
    }

    /**
     * Called by the game engine when the round has ended to provide this player strategy
     * information about their opponent's hand and selection of Melds at the end of the round.
     *
     * easyPlayerStrategy will do nothing since easyPlayer will always take from the top of the discard pile
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
