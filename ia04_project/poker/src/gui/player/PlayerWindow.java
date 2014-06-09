package gui.player;

import gui.player.PersoIHM;
import gui.player.PersoIHM.Sens;
import gui.player.TokenPlayerIHM.ColorToken;
import gui.server.ServerWindow;
import jade.lang.acl.ACLMessage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

import poker.card.exception.CommunityCardsFullException;
import poker.card.helper.CardPickerHelper;
import poker.card.helper.CustomPickSequence;
import poker.card.heuristics.combination.exception.EmptyCardListException;
import poker.card.heuristics.combination.exception.UnexpectedCombinationIdenticCards;
import poker.card.heuristics.combination.helper.CardCombinations;
import poker.card.heuristics.combination.helper.HandComparator;
import poker.card.heuristics.combination.model.Combination;
import poker.card.heuristics.combination.model.Hand;
import poker.card.heuristics.probability.ProbabilityEvaluator;
import poker.card.heuristics.probability.ProbabilityEvaluator.CombinationProbabilityReport;
import poker.card.model.Card;
import poker.card.model.CardDeck;
import poker.card.model.CardRank;
import poker.card.model.CardSuit;
import poker.card.model.CommunityCards;
import poker.card.model.GameDeck;
import poker.card.model.UserDeck;
import sma.agent.HumanPlayerAgent;
import sma.message.FailureMessage;
import sma.message.Message;
import sma.message.MessageVisitor;

/**
 * 
 * Class used for testing purpose only.
 *
 */
public class PlayerWindow extends Application implements PropertyChangeListener {
		
	public enum PlayerGuiEvent {
		PLAYER_RECEIVED_UNKNOWN_CARD,
		PLAYER_RECEIVED_CARD,
		ADD_COMMUNITY_CARD,
		EMPTY_COMMUNITY_CARD,
		PLAYER_FOLDED,
		PLAYER_TABLE,
		PLAYER_RECEIVED_TOKENSET,
		PLAYER_BET,
		PLAYER_CHECK,
		BLIND_VALUE,
		CURRENT_PLAYER_CHANGED
	}
	
	private Button button_fold;
	private Button button_follow;
	private Button button_relaunch;
	private Button button_check;
	private Button button_add_bet;
	private Button button_sub_bet;
	
	private Label label_hand;
	private Label label_min_blind;
	
	private TextArea textarea_log;
	
	private Slider slider_bet;
	private TextField textfield_bet;
	
	private CommunautyCardIHM communauty_card;
	
	private Rectangle zone_carte;
	
	private HumanPlayerAgent human_player_agent;
	
	public void setHumanPlayerAgent(HumanPlayerAgent agent)
	{
		this.human_player_agent = human_player_agent;
	}
	
	@Override
	public void start(Stage primaryStage) {
		
		//--------------------------------------
		
		primaryStage.setTitle("Poker");
        Pane root = new Pane();
        root.setId("root");
        
        Scene scene = new Scene(root, 700, 600);
        URL applicationCss = this.getClass().getResource("/gui/player/application.css");
        //scene.getStylesheets().add(applicationCss.toExternalForm());
        scene.setFill(Color.TRANSPARENT);
        
        label_hand = new Label("Main n°1");
        label_hand.setLayoutX(15);
        label_hand.setLayoutY(15);
        label_hand.getStyleClass().add("hand");
        
        label_min_blind = new Label("Min blind : 1");
        label_min_blind.setLayoutX(15);
        label_min_blind.setLayoutY(425);
        label_min_blind.getStyleClass().add("min-blind");
        
        textarea_log = new TextArea();
        textarea_log.setLayoutX(5);
        textarea_log.setLayoutY(470);
        textarea_log.setPrefHeight(125);
        textarea_log.setPrefWidth(200);
        textarea_log.setWrapText(true);
        textarea_log.setText("Log for the game");
        textarea_log.setEditable(false);
        textarea_log.getStyleClass().add("log");

        button_follow = new Button();
        button_follow.setLayoutX(335);
        button_follow.setLayoutY(490);
        button_follow.setText("Suivre à 2");
        button_follow.setPrefWidth(100);
        button_follow.getStyleClass().add("button_play");
        
        button_check = new Button();
        button_check.setLayoutX(335);
        button_check.setLayoutY(550);
        button_check.setText("Checker");
        button_check.setPrefWidth(100);
        button_check.getStyleClass().add("button_play");
        
        button_fold = new Button();
        button_fold.setLayoutX(225);
        button_fold.setLayoutY(490);
        button_fold.setText("Se coucher");
        button_fold.setPrefWidth(100);
        button_fold.getStyleClass().add("button_play");
        
        button_relaunch = new Button();
        button_relaunch.setLayoutX(225);
        button_relaunch.setLayoutY(550);
        button_relaunch.setText("Relancer à 5");
        button_relaunch.setPrefWidth(100);
        button_relaunch.getStyleClass().add("button_play");
        
        button_add_bet = new Button();
        button_add_bet.setLayoutX(660);
        button_add_bet.setLayoutY(550);
        button_add_bet.setText("+");
        button_add_bet.getStyleClass().add("button_slider");
        
        button_sub_bet = new Button();
        button_sub_bet.setLayoutX(450);
        button_sub_bet.setLayoutY(550);
        button_sub_bet.setText("-");
        button_sub_bet.getStyleClass().add("button_slider");
        
        zone_carte = new Rectangle();
        
        slider_bet = new Slider();
        slider_bet.setMin(0);
        slider_bet.setMax(50);
        slider_bet.setValue(5);
        slider_bet.setShowTickLabels(true);
        slider_bet.setShowTickMarks(true);
        slider_bet.setMajorTickUnit(5);
        slider_bet.setMinorTickCount(0);
        slider_bet.setSnapToTicks(true);
        slider_bet.setBlockIncrement(25);
        slider_bet.setLayoutX(485);
        slider_bet.setLayoutY(550);
        slider_bet.setPrefWidth(175);
        
        textfield_bet = new TextField();
        textfield_bet.setText("5");
        textfield_bet.setLayoutX(485);
        textfield_bet.setLayoutY(525);
        textfield_bet.setPrefWidth(175);
        textfield_bet.setEditable(false);
        
        communauty_card = new CommunautyCardIHM(250, 185);
        
        zone_carte.setX(0);
        zone_carte.setY(455);
        zone_carte.setWidth(950);
        zone_carte.setHeight(170);
        zone_carte.setFill(Color.DARKCYAN);
        
        /**************************************
         *  Player's tokens
         */
        TokenPlayerIHM token_white = new TokenPlayerIHM(485, 500, 25, ColorToken.WHITE);
        TokenPlayerIHM token_black = new TokenPlayerIHM(515, 500, 25, ColorToken.BLACK);
        TokenPlayerIHM token_blue = new TokenPlayerIHM(545, 500, 25, ColorToken.BLUE);
        TokenPlayerIHM token_green = new TokenPlayerIHM(575, 500, 25, ColorToken.GREEN);
        TokenPlayerIHM token_red = new TokenPlayerIHM(605, 500, 25, ColorToken.RED);
        
        /**************************************
         *  Players's bets
         */
        TokenBetPlayerIHM bet_player_1 = new TokenBetPlayerIHM(170, 145, 400);
        TokenBetPlayerIHM bet_player_2 = new TokenBetPlayerIHM(250, 115, 400);
        TokenBetPlayerIHM bet_player_3 = new TokenBetPlayerIHM(440, 115, 400);
        TokenBetPlayerIHM bet_player_4 = new TokenBetPlayerIHM(530, 145, 400);
        TokenBetPlayerIHM bet_player_5 = new TokenBetPlayerIHM(550, 205, 400);
        TokenBetPlayerIHM bet_player_6 = new TokenBetPlayerIHM(170, 260, 400);
        TokenBetPlayerIHM bet_player_7 = new TokenBetPlayerIHM(250, 290, 400);
        TokenBetPlayerIHM bet_player_8 = new TokenBetPlayerIHM(440, 290, 400);
        TokenBetPlayerIHM bet_player_9 = new TokenBetPlayerIHM(530, 260, 400);
        TokenBetPlayerIHM bet_player_10 = new TokenBetPlayerIHM(150, 205, 400);
        
        /**************************************
         *  Player's cards
         */
        ImageView im = new ImageView(new Image("images/ACE_DIAMONDS.png"));
        im.setX(295);
        im.setY(415);
        im.setFitWidth(40);
        im.setFitHeight(62);
        im.setRotate(-15);
        ImageView im2 = new ImageView(new Image("images/ACE_SPADES.png"));
        im2.setX(350);
        im2.setY(415);
        im2.setFitWidth(40);
        im2.setFitHeight(62);
        im2.setRotate(15);
        
        /**************************************
         *  Table
         */
        ImageView table = new ImageView(new Image("images/table_resize.png"));
        table.setX(75);
        table.setY(75);
        
        root.getChildren().add(label_hand);
        root.getChildren().add(label_min_blind);
        root.getChildren().add(table);
        root.getChildren().add(communauty_card);
        root.getChildren().add(zone_carte);
        root.getChildren().add(new PersoIHM(105, 90, "pseudo", Sens.HAUT));
        root.getChildren().add(new PersoIHM(250, 50, "pseudo", Sens.HAUT));
        root.getChildren().add(new PersoIHM(440, 50, "pseudo", Sens.HAUT));
        root.getChildren().add(new PersoIHM(600, 90, "pseudo", Sens.HAUT));
        root.getChildren().add(new PersoIHM(50, 215, "pseudo", Sens.GAUCHE));
        root.getChildren().add(new PersoIHM(650, 215, "pseudo", Sens.DROITE));
        root.getChildren().add(new PersoIHM(105, 330, "pseudo", Sens.BAS));
        root.getChildren().add(new PersoIHM(250, 370, "pseudo", Sens.BAS));
        root.getChildren().add(new PersoIHM(440, 370, "pseudo", Sens.BAS));
        root.getChildren().add(new PersoIHM(600, 330, "pseudo", Sens.BAS));
        root.getChildren().add(bet_player_1);
        root.getChildren().add(bet_player_2);
        root.getChildren().add(bet_player_3);
        root.getChildren().add(bet_player_4);
        root.getChildren().add(bet_player_5);
        root.getChildren().add(bet_player_6);
        root.getChildren().add(bet_player_7);
        root.getChildren().add(bet_player_8);
        root.getChildren().add(bet_player_9);
        root.getChildren().add(bet_player_10);
        root.getChildren().add(textarea_log);
        root.getChildren().add(button_add_bet);
        root.getChildren().add(button_sub_bet);
        root.getChildren().add(button_check);
        root.getChildren().add(button_fold);
        root.getChildren().add(button_follow);
        root.getChildren().add(button_relaunch);
        root.getChildren().add(slider_bet);
        root.getChildren().add(textfield_bet);
        root.getChildren().add(token_white);
        root.getChildren().add(token_black);
        root.getChildren().add(token_blue);
        root.getChildren().add(token_green);
        root.getChildren().add(token_red);
        root.getChildren().add(im);
        root.getChildren().add(im2);

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.getScene().getStylesheets().setAll(PlayerWindow.class.getResource("/gui/player/application.css").toString());
        primaryStage.show();

        initializeAction();
	}

	public static void launchWindow(String[] args) {
		launch(args);
	}
	
	public void initializeAction()
	{
		/**************************************
         *  Slide interaction to see the player's bet
         */
		 button_add_bet.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                slider_bet.setValue(slider_bet.getValue() + 1);
                textfield_bet.setText(String.valueOf(Double.valueOf(slider_bet.getValue()).intValue() + 1));
            }
        });
		 
		 button_sub_bet.setOnAction(new EventHandler<ActionEvent>() {

	            public void handle(ActionEvent event) {
	                slider_bet.setValue(slider_bet.getValue() - 1);
	                textfield_bet.setText(String.valueOf(Double.valueOf(slider_bet.getValue()).intValue() - 1));
	            }
	     });
		 
		 slider_bet.valueProperty().addListener(new ChangeListener<Number>() {
			    @Override
			    public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
			        PlayerWindow.this.textfield_bet.setText(String.valueOf(newValue.intValue()));
			    }
			});
		 
		 button_check.setOnAction(new EventHandler<ActionEvent>() {

	            public void handle(ActionEvent event) {
	                communauty_card.addCommunautyCard(new Card(CardRank.ACE, CardSuit.CLUBS));
	            }
	        });
		 
		 button_fold.setOnAction(new EventHandler<ActionEvent>() {

	            public void handle(ActionEvent event) {
	                communauty_card.emptyCommunautyCard();
	            }
	        });
	}

	/**************************************
     *  Notification
     */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		
		/**
         *  -----  ADD COMMUNITY CARD -----
         */
		if(evt.getPropertyName().equals(PlayerGuiEvent.ADD_COMMUNITY_CARD.toString()))
		{
			if(evt.getNewValue() instanceof Card)
			{
				System.out.println("Add community card");
			}
		}
		
		/**
         *  -----  EMPTY COMMUNITY CARD -----
         */
		else if(evt.getPropertyName().equals(PlayerGuiEvent.EMPTY_COMMUNITY_CARD.toString()))
		{
				System.out.println("Empty community card");
		}
	}
}
