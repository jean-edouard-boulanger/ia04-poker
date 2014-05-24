package poker.card.handler.combination.model;

public enum Combination {
	HIGH_CARD(1), //Hauteur
	ONE_PAIR(2), //Une paire
	TWO_PAIR(3), //Deux paires
	THREE_OF_A_KING(4), //Brelan, 3 cartes identiques
	STRAIGHT(5), //Suite de 5 cartes
	FLUSH(6), //Couleur
	FULL_HOUSE(7), //Full (1 paire et 1 brelan)
	FOUR_OF_A_KING(8), //Carré
	STRAIGHT_FLUSH(9); //Quinte flush: suite + couleur

	private int combination;

	private Combination(int combination) {
		this.combination = combination;
	}

	public int getCombination() {
		return combination;
	}
}