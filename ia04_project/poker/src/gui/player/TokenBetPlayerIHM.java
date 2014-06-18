package gui.player;

import com.sun.javafx.geom.Point2D;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class TokenBetPlayerIHM extends Group {
	
	private Label label_bet;
	private int bet;
	
	public TokenBetPlayerIHM(Point2D point)
	{
		label_bet = new Label("0");
		label_bet.setLayoutX(point.x);
		label_bet.setLayoutY(point.y);

		label_bet.getStyleClass().add("label-bet");
		
		this.bet = 0;
		
		this.getChildren().add(label_bet);
	}
	
	public void addBet(int bet)
	{
		this.bet += bet;
		label_bet.setText(String.valueOf(Integer.valueOf(this.bet)));
	}
	
	public void resetBet()
	{
		this.bet = 0;
		label_bet.setText("0");
	}
}
