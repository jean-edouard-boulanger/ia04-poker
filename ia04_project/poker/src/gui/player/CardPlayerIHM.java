package gui.player;

import java.util.ArrayList;
import java.util.List;

import poker.card.model.Card;
import javafx.animation.FadeTransition;
import javafx.animation.FadeTransitionBuilder;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.animation.TranslateTransitionBuilder;
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
	
	public void revealCard(Card card1, Card card2, boolean winner)
	{
		if(this.number_cards == 2)
		{
			SequentialTransition sequence = new SequentialTransition();
			ParallelTransition parallel_transition = new ParallelTransition();
		     
			this.list_card.get(0).setImage(new Image("images/"+card1.getRank()+"_"+card1.getSuit()+".png"));
			this.list_card.get(1).setImage(new Image("images/"+card2.getRank()+"_"+card2.getSuit()+".png"));
			
			ScaleTransition st_card1 = new ScaleTransition(Duration.millis(2000), this.list_card.get(0));
			st_card1.setByX(1f);
			st_card1.setByY(1f);
			st_card1.setCycleCount(1);
			st_card1.setAutoReverse(true);
		     
		     ScaleTransition st_card2 = new ScaleTransition(Duration.millis(2000), this.list_card.get(1));
		     st_card2.setByX(1f);
		     st_card2.setByY(1f);
		     st_card2.setCycleCount(1);
		     st_card2.setAutoReverse(true);
		     
		     TranslateTransition translate_card1 = TranslateTransitionBuilder
		                .create()
		                .duration(new Duration(1000))
		                .node(this.list_card.get(0))
		                .toX(-25)
		                .cycleCount(1)
		                .interpolator(Interpolator.EASE_BOTH)
		                .build();
		     
		     TranslateTransition translate_card2 = TranslateTransitionBuilder
		                .create()
		                .duration(new Duration(1000))
		                .node(this.list_card.get(1))
		                .toX(25)
		                .cycleCount(1)
		                .interpolator(Interpolator.EASE_BOTH)
		                .build();
		     
		     
		     
		     FadeTransition ft_card1 = FadeTransitionBuilder
		    		 .create()
		    		 .duration(new Duration(1000))
		    		 .node(this.list_card.get(0))
		    		 .toValue(0)
		    		 .build();
		     
		     FadeTransition ft_card2 = FadeTransitionBuilder
		    		 .create()
		    		 .duration(new Duration(1000))
		    		 .node(this.list_card.get(0))
		    		 .toValue(0)
		    		 .build();
		     
		     TranslateTransition winner_card1 = TranslateTransitionBuilder
		                .create()
		                .duration(new Duration(1000))
		                .node(this.list_card.get(0))
		                .toX(500 - this.list_card.get(0).getX())
		                .toY(250 - this.list_card.get(0).getY())
		                .cycleCount(1)
		                .interpolator(Interpolator.EASE_BOTH)
		                .build();
		     
		     TranslateTransition winner_card2 = TranslateTransitionBuilder
		                .create()
		                .duration(new Duration(1000))
		                .node(this.list_card.get(1))
		                .toX(550 - this.list_card.get(1).getX())
		                .toY(250 - this.list_card.get(1).getY())
		                .cycleCount(1)
		                .interpolator(Interpolator.EASE_BOTH)
		                .build();
		     
		     parallel_transition.getChildren().addAll(
		    		 st_card1,
		    		 st_card2,
		    		 translate_card1,
		    		 translate_card2
			 );

		     sequence.getChildren().addAll(
	    			 parallel_transition,
	    			 new PauseTransition(new Duration(1000))
	    	 );
		     
		     if(winner)
		     {
		    	 sequence.getChildren().add(winner_card1);
		    	 sequence.getChildren().add(winner_card2);
		     }
		     
	    	 sequence.getChildren().add(ft_card1);
	    	 sequence.getChildren().add(ft_card2);
	    	 
		     sequence.play();
		}
	}
	
	public void emptyCard()
	{
		final SequentialTransition sequence = new SequentialTransition();
		
		for(ImageView im : list_card)
		{
			TranslateTransition translate_card = TranslateTransitionBuilder
	                .create()
	                .duration(new Duration(500))
	                .node(im)
	                .toX(200 - im.getLayoutX())
	                .toY(175 - im.getLayoutY())
	                .cycleCount(1)
	                .interpolator(Interpolator.EASE_BOTH)
	                .build();
			
			FadeTransition ft_card = FadeTransitionBuilder
		    		 .create()
		    		 .duration(new Duration(200))
		    		 .node(im)
		    		 .toValue(0)
		    		 .build();
			
			sequence.getChildren().add(translate_card);
			sequence.getChildren().add(ft_card);
		}
		
		sequence.play();
		
		
		this.list_card.clear();
		this.current_position_x = x + 35;
		
		this.number_cards = 0;
	}
}
