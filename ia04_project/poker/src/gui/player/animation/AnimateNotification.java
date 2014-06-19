package gui.player.animation;

import javafx.animation.Interpolator;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.TranslateTransitionBuilder;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class AnimateNotification extends Group {
	
	private final Rectangle background_notification = new Rectangle(950, 150);
	private Label label_notification = new Label();
	
	private SequentialTransition sequence;
	
	public AnimateNotification()
	{
		
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
	
	
	public void prepareAnimation(Duration time)
	{
		sequence = new SequentialTransition();
		
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
                .duration(time)
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
				label_translate_middle,
				label_translate_ending,
				background_translate_ending
				);
				
		sequence.setCycleCount(1);
		
	}
	
	public void launchAnimation(String text_to_display)
	{
		label_notification.setText(text_to_display);
		background_notification.toFront();
		label_notification.toFront();
		sequence.play();
	}
}
