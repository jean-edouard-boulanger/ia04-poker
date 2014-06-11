package gui.player;

import java.util.ArrayList;
import java.util.List;

import poker.card.model.Card;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class CardPlayerIHM extends Group {
	
	private List<ImageView> list_card;
	
	private int x, y;
	
	private int current_position_x;
	
	public CardPlayerIHM(int x, int y)
	{
		
		list_card = new ArrayList<ImageView>();
		
		this.x = x;
		this.y = y;
		
		ImageView image_card = new ImageView(new Image("images/background_card.png"));
		image_card.setLayoutX(x);
		image_card.setLayoutY(y);
		image_card.setFitWidth(20);
		image_card.setFitHeight(32);
		
		this.getChildren().add(image_card);
		addUnknownCard();
	}
	
	public void addUnknownCard()
	{
		this.current_position_x += x + 5;
		
		ImageView image_card = new ImageView(new Image("images/background_card.png"));
		image_card.setLayoutX(this.current_position_x);
		image_card.setLayoutY(y);
		image_card.setFitWidth(20);
		image_card.setFitHeight(32);
			
		
		this.list_card.add(image_card);
		this.getChildren().add(image_card);
	}
	
	public void emptyCard()
	{
		for(ImageView im : list_card)
			this.getChildren().remove(im);
		
		this.list_card.clear();
		this.current_position_x = x + 35;
	}
}
