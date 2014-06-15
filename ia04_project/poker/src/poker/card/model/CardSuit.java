package poker.card.model;

public enum CardSuit {
	HEARTS(1),
	SPADES(2),
	CLUBS(3),
	DIAMONDS(4);
	
	private int cardSuit;
	
	private CardSuit(int cardSuit){
		this.cardSuit = cardSuit;
	}
	
	public String getSymbol(){
		if(cardSuit == 1){
			return "h";
		}
		else if(cardSuit == 2){
			return "s";
		}
		else if(cardSuit == 3){
			return "c";
		}
		else{
			return "d";
		}
	}
}
