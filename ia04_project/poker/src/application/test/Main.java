package application.test;

import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import poker.card.exception.CommunityCardsFullException;
import poker.card.helper.CustomPickSequence;
import poker.card.heuristics.combination.exception.EmptyCardListException;
import poker.card.heuristics.combination.helper.CardCombinations;
import poker.card.heuristics.combination.helper.HandComparator;
import poker.card.heuristics.combination.model.Combination;
import poker.card.heuristics.combination.model.Hand;
import poker.card.heuristics.probability.ProbabilityEvaluator;
import poker.card.heuristics.probability.ProbabilityEvaluator.CombinationProbabilityReport;
import poker.card.model.Card;
import poker.card.model.CardRank;
import poker.card.model.CardSuit;
import poker.card.model.CommunityCards;
import poker.card.model.UserDeck;
import poker.game.exception.ExcessiveBetException;
import poker.game.player.model.Player;
import poker.token.exception.InvalidRepartitionException;
import poker.token.exception.InvalidTokenAmountException;
import poker.token.exception.InvalidTokenValueException;
import poker.token.factories.TokenSetFactory;
import poker.token.helpers.TokenSetValueEvaluator;
import poker.token.model.TokenRepartition;
import poker.token.model.TokenSet;
import poker.token.model.TokenType;
import poker.token.model.TokenValueDefinition;
import sma.message.FailureMessage;
import sma.message.Message;
import sma.message.MessageVisitor;

/**
 * 
 * Class used for testing purpose only.
 *
 */
public class Main extends Application {
		
	private Button btn;
	
	private Rectangle table;
	
	private Rectangle zone_carte;
	
	@Override
	public void start(Stage primaryStage) throws InvalidTokenValueException, InvalidTokenAmountException, InvalidRepartitionException, ExcessiveBetException {
		
	    TokenValueDefinition defaultTokenValueDefinition = new TokenValueDefinition();
	    defaultTokenValueDefinition.setValueForTokenType(TokenType.WHITE, 1);
	    defaultTokenValueDefinition.setValueForTokenType(TokenType.RED, 10);
	    defaultTokenValueDefinition.setValueForTokenType(TokenType.GREEN, 20);
	    defaultTokenValueDefinition.setValueForTokenType(TokenType.BLUE, 50);
	    defaultTokenValueDefinition.setValueForTokenType(TokenType.BLACK, 100);
	    
	    TokenRepartition defaultTokenRepartiton = new TokenRepartition();
	    defaultTokenRepartiton.setRepartitionForToken(TokenType.WHITE, 40);
	    defaultTokenRepartiton.setRepartitionForToken(TokenType.RED, 30);
	    defaultTokenRepartiton.setRepartitionForToken(TokenType.GREEN, 15);
	    defaultTokenRepartiton.setRepartitionForToken(TokenType.BLUE, 10);
	    defaultTokenRepartiton.setRepartitionForToken(TokenType.BLACK, 5);

	    int nbTokens = 40;
	    TokenSet tokenSet = TokenSetFactory.createTokenSet(defaultTokenRepartiton, nbTokens);
	//    TokenSet ts = TokenSetValueEvaluator.tokenSetFromAmount(500, defaultTokenValueDefinition);
		TokenSet ts = TokenSetValueEvaluator.tokenSetForBet(323, defaultTokenValueDefinition, tokenSet);
		System.out.println(ts);
		UserDeck userDeck = new UserDeck();
		
		userDeck.setCard1(new Card(CardRank.JACK, CardSuit.CLUBS));
		userDeck.setCard2(new Card(CardRank.SIX, CardSuit.CLUBS));
		
		UserDeck userDeck2 = new UserDeck();

		userDeck2.setCard1(new Card(CardRank.SEVEN, CardSuit.CLUBS));
		userDeck2.setCard2(new Card(CardRank.SEVEN, CardSuit.SPADES));

		
		CommunityCards communityCards = new CommunityCards();
		try {
		//	communityCards.pushCard(new Card(CardRank.EIGHT, CardSuit.SPADES));
		//	communityCards.pushCard(new Card(CardRank.FOUR, CardSuit.SPADES));
			communityCards.pushCard(new Card(CardRank.TEN, CardSuit.DIAMONDS));
			communityCards.pushCard(new Card(CardRank.QUEEN, CardSuit.SPADES));
		//	communityCards.pushCard(new Card(CardRank.FIVE, CardSuit.DIAMONDS));
	//		communityCards.pushCard(new Card(CardRank.JACK, CardSuit.HEARTS));
	//		communityCards.pushCard(new Card(CardRank.KING, CardSuit.DIAMONDS));
			communityCards.pushCard(new Card(CardRank.ACE, CardSuit.CLUBS));
		} catch (CommunityCardsFullException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			Hand h1 = CardCombinations.bestHandFromCards(userDeck.getCards());
			Hand h2 = CardCombinations.bestHandFromCards(userDeck2.getCards());
			
			ArrayList<Hand> hands = new ArrayList<Hand>();
			
			hands.add(h1);
			hands.add(h2);
		//	System.out.println(hands);
			System.out.println(HandComparator.bestHand(hands));

			
			Player p1 =  new Player();
			p1.setDeck(userDeck);
			Player p2 =  new Player();
			p2.setDeck(userDeck2);
			
			
			
			//Copying the cards of the players
			Map<Player, Hand> winners = new HashMap<Player, Hand>();

			ArrayList<Hand> winningHands = new ArrayList<Hand>();
			ArrayList<Player> potentialWinners = new ArrayList<Player>();
			potentialWinners.add(p1);
			potentialWinners.add(p2);
			
			for(Player p : potentialWinners) {
				ArrayList<Card> playerHandCards = p.getDeck().getCards();
				playerHandCards.addAll(communityCards.getCommunityCards());
				
				try {
					Hand h = CardCombinations.bestHandFromCards(playerHandCards);
					winners.put(p, h);
					winningHands.add(h);
				} catch (EmptyCardListException e) {
					e.printStackTrace();
				}
			}

			//Getting the best hands (more than one in case of equality)
			winningHands = HandComparator.bestHand(winningHands);
			
			Map<Player, Hand> winners2 = new HashMap<Player, Hand>();
			
			//Removing players without a winning hand
			for(Hand h : winningHands) {
				for(Entry<Player, Hand> entry : winners.entrySet()) {
					if(h == entry.getValue()) {
						winners2.put(entry.getKey(), entry.getValue());
					}
				}
			}
			
			
			for(Player p : winners2.keySet()) {
				System.out.println(winners.get(p));
			}
			
			
			
			
		} catch (EmptyCardListException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		ArrayList<Card> cards = new ArrayList<Card>();
		
		cards.add(new Card(CardRank.TWO, CardSuit.DIAMONDS));
		cards.add(new Card(CardRank.THREE, CardSuit.DIAMONDS));
		cards.add(new Card(CardRank.THREE, CardSuit.SPADES));
		/*cards.add(new Card(CardRank.FIVE, CardSuit.CLUBS));
		cards.add(new Card(CardRank.TEN, CardSuit.DIAMONDS));
		cards.add(new Card(CardRank.TWO, CardSuit.HEARTS));
		cards.add(new Card(CardRank.EIGHT, CardSuit.SPADES));
		cards.add(new Card(CardRank.NINE, CardSuit.SPADES));
		cards.add(new Card(CardRank.QUEEN, CardSuit.DIAMONDS));
		cards.add(new Card(CardRank.NINE, CardSuit.DIAMONDS));
		cards.add(new Card(CardRank.KING, CardSuit.DIAMONDS));
		cards.add(new Card(CardRank.ACE, CardSuit.DIAMONDS));
		cards.add(new Card(CardRank.JACK, CardSuit.DIAMONDS));
		cards.add(new Card(CardRank.TEN, CardSuit.DIAMONDS));
		cards.add(new Card(CardRank.THREE, CardSuit.DIAMONDS));*/
		
		
		ProbabilityEvaluator pe = new 
				ProbabilityEvaluator.ProbabilityEvaluatorBuilder()
				.setDealSequence(CustomPickSequence.getFixedNumberCardsPickedDealSequence(7 - cards.size()))
				//.addExpectedCombination(Combination.STRAIGHT)
				.addAllPossibleCombinationsToExpectedCombinations()
				.setNumberTrials(10000)
				.setKnownCards(cards)
				.buildProbabilityEvaluator();
		
		CombinationProbabilityReport r = pe.evaluate();
		
		for(Combination c : pe.getExpectedCombinations()){
			System.out.println(c + " : " + r.getProbabilityForCombination(c) * 100 + " %");
		}
		
		// Serialization tests:
		try {
			FailureMessage msg = new FailureMessage("test failure message");
			String json = msg.toJson();
			Message msg2 = Message.fromJson(json);
			msg2.accept(new MessageVisitor(){
				@Override
				public boolean onFailureMessage(FailureMessage msg, ACLMessage aclMsg){
					System.out.println(msg.getMessage());
					return true;					
				}
			}, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public void initializeAction()
	{
		 btn.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                System.out.println("Hello World");
            }
        });
	}
}
