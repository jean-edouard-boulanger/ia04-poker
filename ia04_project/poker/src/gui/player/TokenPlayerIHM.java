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
	
	public enum ColorToken { WHITE, BLACK, BLUE, GREEN, RED }
	public TokenPlayerIHM(int x, int y, int mise, ColorToken color_token)
	{
		
		Label label_mise = new Label(String.valueOf(mise));
		label_mise.setLayoutX(x);
		label_mise.setLayoutY(y);
		
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
        jeton.setX(x-10);
        jeton.setY(y-10);
        jeton.setFitWidth(20);
        jeton.setFitHeight(20);
	    
        this.getChildren().add(jeton);
		this.getChildren().add(label_mise);
	}
}
