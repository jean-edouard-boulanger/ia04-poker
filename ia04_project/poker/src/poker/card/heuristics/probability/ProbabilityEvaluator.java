package poker.card.heuristics.probability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import poker.card.helper.CardPickerHelper;
import poker.card.helper.CustomPickSequence;
import poker.card.heuristics.combination.CardCombinations;
import poker.card.heuristics.combination.exception.EmptyCardListException;
import poker.card.heuristics.combination.exception.UnexpectedCombinationIdenticCards;
import poker.card.heuristics.combination.model.Combination;
import poker.card.model.Card;
import poker.card.model.CardDeck;

/**
 * Calculates the probability to get a combination based on several parameters
 */
public class ProbabilityEvaluator {

	private final ArrayList<Combination> expectedCombinations;
	private final ArrayList<Card> knownCards;
	private final int nbTrials;
	private final CustomPickSequence dealSequence;
	
	private ProbabilityEvaluator(ProbabilityEvaluatorBuilder builder){
		this.expectedCombinations = builder.expectedCombinations;
		this.knownCards = builder.knownCards;
		this.nbTrials = builder.nbTrials;
		this.dealSequence = builder.dealSequence;
	}
	
	/**
	 * Calculates the probability to get a combination based on several parameters
	 * The calculation is done thanks to the Monte-Carlo method
	 * @return CombinationProbabilityReport Combination probability report
	 */
	public CombinationProbabilityReport evaluate(){
		
		CombinationProbabilityReport report = new CombinationProbabilityReport();
		RandomCardDeckGenerator deckGenerator = new RandomCardDeckGenerator(this.knownCards);
		HashMap<Combination, Integer> outcomes = new HashMap<Combination, Integer>();
		ArrayList<Card> pickedCards = null;
		CardDeck d = null;
		
		for(Combination c : this.expectedCombinations){
			outcomes.put(c, 0);
		}
		
		for(int i = 0; i < this.nbTrials; i++){
			d = deckGenerator.GenerateNext();
			pickedCards = CardPickerHelper.pickCardsFromDeck(d, this.dealSequence);
			pickedCards.addAll(this.knownCards);
			
			try{
				if(CardCombinations.highestOfKing(pickedCards, 3) != null){
					outcomes.put(Combination.THREE_OF_A_KIND, outcomes.get(Combination.THREE_OF_A_KIND) + 1);
				}
			}
			catch(Exception ex){}
		}
		
		for(Combination c : this.expectedCombinations){
			report.setProbabilityForCombination(c, (float)outcomes.get(c) / this.nbTrials);
		}
		
		return report;
	}
	
	/**
	 * Class that builds a ProbabilityEvaluator 
	 */
	public static class ProbabilityEvaluatorBuilder{
		
		private ArrayList<Combination> expectedCombinations;
		private ArrayList<Card> knownCards;
		private int nbTrials = 1000;
		private CustomPickSequence dealSequence;
		
		public ProbabilityEvaluatorBuilder(){
			this.expectedCombinations = new ArrayList<Combination>();
			this.knownCards = new ArrayList<Card>();
		}
		
		/**
		 * Add a combination probability to evaluate during the calculation process
		 * @param c Expected combination
		 * @return ProbabilityEvaluatorBuilder
		 */
		public ProbabilityEvaluatorBuilder addExpectedCombination(Combination c){
			if(!this.expectedCombinations.contains(c)){
				this.expectedCombinations.add(c);
			}
			return this;
		}
		
		/**
		 * Set the combination probability to evaluate during the calculation process
		 * @param c Expected combination
		 * @return ProbabilityEvaluatorBuilder
		 */
		public ProbabilityEvaluatorBuilder setExpectedCombination(Combination c){
			this.expectedCombinations.clear();
			this.expectedCombinations.add(c);
			return this;
		}
		
		/**
		 * Add all the possible combinations to the list of combinations to evaluate during the calculation process
		 * @return ProbabilityEvaluatorBuilder
		 */
		public ProbabilityEvaluatorBuilder addAllPossibleCombinationsToExpectedCombinations(){
			this.expectedCombinations.clear();
			for(Combination c : Combination.values()){
				if(c != Combination.HIGH_CARD){
					this.expectedCombinations.add(c);
				}
			}
			return this;
		}
		
		/**
		 * Add specified combinations to the list of combinations to evaluate during the calculation process
		 * @param c Expected combinations
		 * @return ProbabilityEvaluatorBuilder
		 */
		public ProbabilityEvaluatorBuilder addExpectedCombinations(ArrayList<Combination> expectedCombinations){
			if(expectedCombinations != null){
				expectedCombinations.remove(Combination.HIGH_CARD);
				this.expectedCombinations = expectedCombinations;
			}
			else{
				this.expectedCombinations = new ArrayList<Combination>();
			}
			return this;
		}
		
		/**
		 * Indicates which cards are already known
		 * @param knownCards Known cards
		 * @return ProbabilityEvaluatorBuilder
		 */		
		public ProbabilityEvaluatorBuilder setKnownCards(ArrayList<Card> knownCards){
			this.knownCards = new ArrayList<Card>(knownCards);
			return this;
		}
		
		/**
		 * Set the number of simulations that will be done to evaluate the probability
		 * @param nbTrials Number of simulations
		 * @return ProbabilityEvaluatorBuilder
		 */
		public ProbabilityEvaluatorBuilder setNumberTrials(int nbTrials){
			this.nbTrials = nbTrials;
			return this;
		}
		
		/**
		 * Set the deal sequence
		 * @param dealSequence Deal sequence
		 * @return ProbabilityEvaluatorBuilder
		 */
		public ProbabilityEvaluatorBuilder setDealSequence(CustomPickSequence dealSequence){
			this.dealSequence = dealSequence;
			return this;
		}
		
		/**
		 * Instanciates a new ProbabilityEvaluator
		 * @return ProbabilityEvaluator
		 */
		public ProbabilityEvaluator buildProbabilityEvaluator(){
			return new ProbabilityEvaluator(this);
		}
	}
	
	/**
	 * Combination probability report
	 */
	public class CombinationProbabilityReport {

		private Map<Combination, Float> probabilities;
		
		private CombinationProbabilityReport(){
			this.probabilities = new HashMap<Combination, Float>();
		}
		
		public void setProbabilityForCombination(Combination c, Float probability){
			this.probabilities.put(c, probability);
		}
		
		public float getProbabilityForCombination(Combination c){
			Float probability = this.probabilities.get(c);
			if(probability == null){
				return 0.0f;
			}
			return probability;
		}
		
		public void setProbabilities(Map<Combination, Float> probabilities){
			this.probabilities = probabilities;
		}
		
		public Map<Combination, Float> getProbabilities(){
			return this.probabilities;
		}
	}
}
