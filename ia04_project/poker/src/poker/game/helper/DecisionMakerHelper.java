package poker.game.helper;

import gui.player.event.model.AIPlayRequestEventData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import poker.card.helper.CustomPickSequence;
import poker.card.heuristics.combination.model.Combination;
import poker.card.heuristics.probability.ProbabilityEvaluator;
import poker.card.heuristics.probability.ProbabilityEvaluator.CombinationProbabilityReport;
import poker.card.model.Card;
import poker.game.model.AIPlayerType;
import poker.game.model.BetType;
import poker.game.model.Decision;

public class DecisionMakerHelper {	

	public static Decision makeDecision(AIPlayRequestEventData eventData, AIPlayerType playerType) {
		
		
		ArrayList<Card> cards = eventData.getCards();		
				
		ProbabilityEvaluator pe = new ProbabilityEvaluator.ProbabilityEvaluatorBuilder()
				.setDealSequence(CustomPickSequence.getFixedNumberCardsPickedDealSequence(7 - cards.size()))
				.addAllPossibleCombinationsToExpectedCombinations()
				.setNumberTrials(10000)
				.setKnownCards(cards)
				.buildProbabilityEvaluator();
		
		CombinationProbabilityReport r = pe.evaluate();
			
		HashMap<Combination, Float> probabilities = (HashMap<Combination, Float>) r.getProbabilities();
		
		int combinationCount = 0;//Collections.frequency(probabilities.values(), 1);
			
		for(Float p : probabilities.values()) {
			if(p > 0.7) {
				combinationCount++;
			}
		}
		
		if(playerType == AIPlayerType.STATS) {
			return statsMakeDecision(eventData, combinationCount);
		}
		//else if(playerType == AIPlayerType.CALLER) {
			//return callerMakeDecision(eventData, combinationCount);
		//}
		
		return null;
	}
	
	private static Decision statsMakeDecision(AIPlayRequestEventData eventData, int combinationCount) {
		
		ArrayList<BetType> betActions = eventData.getAvailableActions();

		int minimumBetAmount = eventData.getMinimumBetAmount();
		int maximumBetAmount = eventData.getMinimumBetAmount();
		int callAmount = eventData.getCallAmount();

		Random random = new Random();
		
		int raiseDecision = random.nextInt(90);
		
		if(raiseDecision < 10) {
			return new Decision(BetType.FOLD, 0);
		}
		
		if(combinationCount == 0) {
			if(betActions.contains(BetType.CHECK)) {
				System.out.println("[AIPlayer] Decided to check.");
				return new Decision(BetType.CHECK, 0);
			}
			else if(betActions.contains(BetType.CALL)) {
				if(callAmount < ((7 * maximumBetAmount) / 10) && raiseDecision > 35) {
					System.out.println("[AIPlayer] Decided to call.");
					return new Decision(BetType.CALL, callAmount);
				}
				else {
					System.out.println("[AIPlayer] Decided to fold.");
					return new Decision(BetType.FOLD, 0);
				}
			}
			else {
				System.out.println("[AIPlayer] Decided to fold.");
				return new Decision(BetType.FOLD, 0);
			}
		}
		
		if(combinationCount == 1) {
			if(betActions.contains(BetType.CHECK)) {
				System.out.println("[AIPlayer] Decided to raise at " + minimumBetAmount + " because I have a combination.");
				return new Decision(BetType.RAISE, minimumBetAmount);
			}
			else if(betActions.contains(BetType.CALL)) {
				System.out.println("[AIPlayer] Decided to call.");
				return new Decision(BetType.CALL, callAmount);
			}
			else {
				System.out.println("[AIPlayer] Decided to fold.");
				return new Decision(BetType.FOLD, 0);
			}
		}
		
		if(combinationCount > 1) {
			System.out.println("[AIPlayer] Decided to raise at " + maximumBetAmount + " because I have " + combinationCount + " combinations.");
			return new Decision(BetType.RAISE, minimumBetAmount);
		}
		
		return null;
	}
	
	private static Decision callerMakeDecision(AIPlayRequestEventData eventData, int combinationCount) {
		
		ArrayList<BetType> betActions = eventData.getAvailableActions();

		int minimumBetAmount = eventData.getMinimumBetAmount();
		int maximumBetAmount = eventData.getMinimumBetAmount();
		int callAmount = eventData.getCallAmount();

		return new Decision(BetType.RAISE, minimumBetAmount);
	}
}