package gui.player;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class TokenPlayerIHM extends Group {
	
	private static final String img_token_white = "images/chip_white.png";
	private static final String img_token_black = "images/chip_black.png";
	private static final String img_token_blue = "images/chip_blue.png";
	private static final String img_token_green = "images/chip_green.png";
	private static final String img_token_red = "images/chip_red.png";
	
	private Label label_mise;
	
	public enum ColorToken { WHITE, BLACK, BLUE, GREEN, RED }
	public TokenPlayerIHM(int x, int y, int mise, ColorToken color_token)
	{
		
		label_mise = new Label(String.valueOf(mise));
		label_mise.setLayoutX(x);
		label_mise.setLayoutY(y);
		label_mise.getStyleClass().add("label-token");
		
		String image = "";
		switch(color_token)
		{
			case WHITE:
				image = img_token_white;
				break;
			case BLACK:
				image = img_token_black;
				break;
			case BLUE:
				image = img_token_blue;
				break;
			case GREEN:
				image = img_token_green;
				break;
			case RED:
				image = img_token_red;
				break;
			default:
				image = img_token_white;
		}
		
		ImageView jeton = new ImageView(new Image(image));
        jeton.setX(x);
        jeton.setY(y-25);
        jeton.setFitWidth(20);
        jeton.setFitHeight(20);
	    
        this.getChildren().add(jeton);
		this.getChildren().add(label_mise);
	}
	
	public void setMise(int mise)
	{
		int new_bet = Integer.valueOf(label_mise.getText()).intValue() + mise;
		label_mise.setText(String.valueOf(new_bet));
	}
}
