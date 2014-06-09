package gui.player;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class TokenBetPlayerIHM extends Group {
	
	public TokenBetPlayerIHM(int x, int y, int bet)
	{
		Label label_bet = new Label(String.valueOf(Integer.valueOf(bet)));
		label_bet.setLayoutX(x);
		label_bet.setLayoutY(y);

		label_bet.getStyleClass().add("label-bet");
		
		
		this.getChildren().add(label_bet);
	}
}
