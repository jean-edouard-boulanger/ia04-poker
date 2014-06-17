package gui.player;

import java.util.ArrayList;
import java.util.List;

import poker.card.model.Card;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class CardPlayerIHM extends Group {
	
	private List<ImageView> list_card;
	
	private int x, y;
	
	private int current_position_x;
	
	private int number_cards = 0;
	
	public CardPlayerIHM(int x, int y)
	{
		list_card = new ArrayList<ImageView>();
		
		this.x = x;
		this.y = y;
		this.current_position_x = x + 35;
	}
	
	public void addUnknownCard()
	{
		if(this.number_cards < 2){
			this.current_position_x += 5;
			
			ImageView image_card = new ImageView(new Image("images/background_card.png"));
			image_card.setLayoutX(this.current_position_x);
			image_card.setLayoutY(y);
			image_card.setFitWidth(20);
			image_card.setFitHeight(32);
				
			this.list_card.add(image_card);
			this.getChildren().add(image_card);
			
			this.number_cards++;
		}
		else {
			System.err.println("ERROR [IHM] Tried to add add unkown card, while the player already had two.");
		}
	}
	
	public void revealCard(Card card1, Card card2)
	{
		if(this.number_cards == 2)
		{
			ParallelTransition parallel_transition = new ParallelTransition();
		     
			this.list_card.get(0).setImage(new Image("images/"+card1.getRank()+"_"+card1.getSuit()+".png"));
			this.list_card.get(1).setImage(new Image("images/"+card2.getRank()+"_"+card2.getSuit()+".png"));
			
			ScaleTransition st_card1 = new ScaleTransition(Duration.millis(2000), this.list_card.get(0));
			st_card1.setByX(1.5f);
			st_card1.setByY(1.5f);
			st_card1.setCycleCount(1);
			st_card1.setAutoReverse(true);
		     
		     ScaleTransition st_card2 = new ScaleTransition(Duration.millis(2000), this.list_card.get(0));
		     st_card2.setByX(1.5f);
		     st_card2.setByY(1.5f);
		     st_card2.setCycleCount(1);
		     st_card2.setAutoReverse(true);
		     
		     parallel_transition.getChildren().addAll(
		    		 st_card1,
		    		 st_card2
    		 );
		     
		     parallel_transition.play();
		}
	}
	
	public void emptyCard()
	{
		for(ImageView im : list_card)
			this.getChildren().remove(im);
		
		this.list_card.clear();
		this.current_position_x = x + 35;
		
		this.number_cards = 0;
	}
}
