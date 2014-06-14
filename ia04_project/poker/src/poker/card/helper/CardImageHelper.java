package poker.card.helper;



import javafx.scene.image.Image;
import poker.card.model.Card;

public class CardImageHelper {
	public static String getCardImageName(Card card) {
		return new String("images/" + card.getRank().toString() + "_" + card.getSuit().toString() + ".png");
	}
	
	public static Image getCardImage(Card card) {
		return new Image(getCardImageName(card));
	}
}
