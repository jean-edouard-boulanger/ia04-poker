package gui.player.animation;

import java.util.ArrayList;
import java.util.List;

import poker.card.heuristics.combination.model.Hand;
import poker.card.model.Card;
import javafx.animation.Interpolator;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.TranslateTransitionBuilder;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class AnimateWinner extends Group {
	
	private final Rectangle background_notification = new Rectangle(950, 150);
	private Label label_notification = new Label();
	
	private SequentialTransition sequence;
	
	public AnimateWinner()
	{
		sequence = new SequentialTransition();
		
		background_notification.getStyleClass().add("background-notification");
		background_notification.setLayoutX(-950);
		background_notification.setLayoutY(150);
		label_notification.setLayoutX(-950);
		label_notification.setLayoutY(200);
		label_notification.setText(label_notification.getText().toUpperCase());
		label_notification.getStyleClass().add("label-notification");
		
		this.getChildren().add(background_notification);
		this.getChildren().add(label_notification);
	}
	
	
	private void prepareAnimation(ArrayList<Card> list_card) throws Exception
	{
		TranslateTransition background_translate_beginning = TranslateTransitionBuilder
                .create()
                .duration(new Duration(200))
                .node(background_notification)
                .fromX(-950)
                .toX(900)
                .cycleCount(1)
                .interpolator(Interpolator.EASE_BOTH)
                .build();
		
		TranslateTransition background_translate_ending = TranslateTransitionBuilder
                .create()
                .duration(new Duration(200))
                .node(background_notification)
                .toX(2500)
                .cycleCount(1)
                .interpolator(Interpolator.EASE_BOTH)
                .build();
		
		TranslateTransition label_translate_beginning = TranslateTransitionBuilder
                .create()
                .duration(new Duration(500))
                .node(label_notification)
                .fromX(-950)
                .toX(1000)
                .cycleCount(1)
                .interpolator(Interpolator.EASE_BOTH)
                .build();
		
		TranslateTransition label_translate_middle = TranslateTransitionBuilder
                .create()
                .duration(new Duration(5000))
                .node(label_notification)
                .toX(1150)
                .cycleCount(1)
                .interpolator(Interpolator.EASE_BOTH)
                .build();
		
		TranslateTransition label_translate_ending = TranslateTransitionBuilder
                .create()
                .duration(new Duration(500))
                .node(label_notification)
                .toX(2500)
                .cycleCount(1)
                .interpolator(Interpolator.EASE_BOTH)
                .build();
		
		
		sequence.getChildren().addAll(
				background_translate_beginning,
				label_translate_beginning,
				label_translate_middle);
		
		int x_card = 0;
		for(Card card : list_card)
		{
			ImageView image_card = new ImageView(new Image("images/" + card.getRank() + "_" + card.getSuit() + ".png"));
			image_card.setLayoutX(x_card);
			image_card.setLayoutY(250);
			image_card.setFitWidth(50);
			image_card.setFitHeight(72);
			x_card += 55;
			
			TranslateTransition t_card = TranslateTransitionBuilder
	                .create()
	                .duration(new Duration(500))
	                .node(image_card)
	                .toX(2500)
	                .cycleCount(1)
	                .interpolator(Interpolator.EASE_BOTH)
	                .build();
			
			sequence.getChildren().add(t_card);
		}
		
		sequence.getChildren().addAll(
				label_translate_ending,
				background_translate_ending
				);
				
		sequence.setCycleCount(1);
		
	}
	
	public SequentialTransition launchAnimation(String text_to_display, Hand hand)
	{
		try
		{
			label_notification.setText(text_to_display);
			prepareAnimation(hand.getCombinationCards());
			background_notification.toFront();
			label_notification.toFront();	
		}
		catch(Exception e) {
			System.out.println("[PlayerWindow] animation winner failed " + e.getMessage());
		}
		
		return sequence;
	}
}
