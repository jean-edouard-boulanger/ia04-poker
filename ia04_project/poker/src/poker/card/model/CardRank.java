package poker.card.model;

public enum CardRank {
	TWO(2),
	THREE(3),
	FOUR(4),
	FIVE(5),
	SIX(6),
	SEVEN(7),
	EIGHT(8),
	NINE(9),
	TEN(10),
	JACK(11),
	QUEEN(12),
	KING(13),
	ACE(14);

	private int cardRank;

	private CardRank(int rank) {
		this.cardRank = rank;
	}

	public int getCardRank() {
		return cardRank;
	}
	
	public CardRank getNext() {
		return values()[(ordinal()+1) % values().length];
	}
	
	public String getSymbol(){
		if(this.cardRank >= 2 && this.cardRank < 11){
			return String.valueOf(cardRank);
		}
		else if(this.cardRank == 11){
			return "J";
		}
		else if(this.cardRank == 12){
			return "Q";
		}
		else if(this.cardRank == 13){
			return "K";
		}
		else{
			return "A";
		}
	}
}