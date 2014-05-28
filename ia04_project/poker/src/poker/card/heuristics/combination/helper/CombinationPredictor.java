package poker.card.heuristics.combination.helper;

import java.util.ArrayList;

import poker.card.helper.CustomPickSequence;
import poker.card.heuristics.combination.model.Combination;
import poker.card.heuristics.probability.ProbabilityEvaluator;
import poker.card.heuristics.probability.ProbabilityEvaluator.CombinationProbabilityReport;
import poker.card.model.Card;

public class CombinationPredictor {

	/**
	 * Determines all the combinations that are likely to be done with the community cards (List provided)
	 * For example, if K-Spades, 2-Diamonds, 6-Hearts, the possible combinations are:
	 * - High card
	 * - Pair
	 * - Three of a kind
	 * @param cards Community cards
	 * @return Combinations that could be done
	 */
	public static ArrayList<Combination> getPossibleCombinations(ArrayList<Card> cards){
		
		ArrayList<Combination> possibleCombinations = new ArrayList<Combination>();
		
		ProbabilityEvaluator pe = new 
			ProbabilityEvaluator.ProbabilityEvaluatorBuilder()
			.setDealSequence(CustomPickSequence.getFixedNumberCardsPickedDealSequence(2))
			.addAllPossibleCombinationsToExpectedCombinations()
			.setNumberTrials(10000)
			.setKnownCards(cards)
			.buildProbabilityEvaluator();
		CombinationProbabilityReport r = pe.evaluate();
		
		for(Combination c : pe.getExpectedCombinations()){
			if(r.getProbabilityForCombination(c) > 0){
				possibleCombinations.add(c);
			}
		}
		return possibleCombinations;
	}
	
	/**
	 * Determines the best combination that is likely to be done with the community cards (List provided)
	 * @param cards
	 * @return Best combination that could be done
	 */
	public static Combination getBestPossibleCombination(ArrayList<Card> cards){
		ArrayList<Combination> possibleCombinations = getPossibleCombinations(cards);
		
		Combination currentBestCombination = Combination.HIGH_CARD;
		
		for(Combination c : possibleCombinations){
			if(c.getCombination() > currentBestCombination.getCombination()){
				currentBestCombination = c;
			}
		}
		return currentBestCombination;
	}
	
}
