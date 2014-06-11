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
import java.util.List;

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
	
	private Pane root;
	
	/** Interaction button */
	private Button button_fold;
	private Button button_follow;
	private Button button_relaunch;
	private Button button_check;
	private Button button_add_bet;
	private Button button_sub_bet;
	
	/** Hand number & min blind displaying */
	private Label label_hand;
	private Label label_min_blind;
	
	/** Log */
	private TextArea textarea_log;
	
	/** Slide for bet */
	private Slider slider_bet;
	private TextField textfield_bet;
	
	/** Communauty card */
	private CommunautyCardIHM communauty_card;
	
	private Rectangle zone_carte;
	
	/** Agent */
	private HumanPlayerAgent human_player_agent;
	
	/** Players number */
	private int nb_players;
	private int num_player;
	
	/** Token player */
	private TokenPlayerIHM token_white;
	private TokenPlayerIHM token_black;
	private TokenPlayerIHM token_blue;
	private TokenPlayerIHM token_green;
	private TokenPlayerIHM token_red;
	
	/** 10 players max */
	
	private List<PersoIHM> list_perso;
	
	PersoIHM perso_1;
	PersoIHM perso_2;
	PersoIHM perso_3;
	PersoIHM perso_4;
	PersoIHM perso_5;
	PersoIHM perso_6;
	PersoIHM perso_7;
	PersoIHM perso_8;
	PersoIHM perso_9;
	PersoIHM perso_10;
    
	private List<TokenBetPlayerIHM> list_token_bet;
	
	TokenBetPlayerIHM bet_player_1;
    TokenBetPlayerIHM bet_player_2;
    TokenBetPlayerIHM bet_player_3;
    TokenBetPlayerIHM bet_player_4;
    TokenBetPlayerIHM bet_player_5;
    TokenBetPlayerIHM bet_player_6;
    TokenBetPlayerIHM bet_player_7;
    TokenBetPlayerIHM bet_player_8;
    TokenBetPlayerIHM bet_player_9;
    TokenBetPlayerIHM bet_player_10;
    
    private List<CardPlayerIHM> list_card_player;
    
    CardPlayerIHM card_player_1;
    CardPlayerIHM card_player_2;
    CardPlayerIHM card_player_3;
    CardPlayerIHM card_player_4;
    CardPlayerIHM card_player_5;
    CardPlayerIHM card_player_6;
    CardPlayerIHM card_player_7;
    CardPlayerIHM card_player_8;
    CardPlayerIHM card_player_9;
    CardPlayerIHM card_player_10;
	
	public void setHumanPlayerAgent(HumanPlayerAgent agent)
	{
		this.human_player_agent = human_player_agent;
	}
	
	@Override
	public void start(Stage primaryStage) {
		
		//--------------------------------------
		
		primaryStage.setTitle("Poker");
        root = new Pane();
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
        token_white = new TokenPlayerIHM(485, 500, 25, ColorToken.WHITE);
        token_black = new TokenPlayerIHM(515, 500, 25, ColorToken.BLACK);
        token_blue = new TokenPlayerIHM(545, 500, 25, ColorToken.BLUE);
        token_green = new TokenPlayerIHM(575, 500, 25, ColorToken.GREEN);
        token_red = new TokenPlayerIHM(605, 500, 25, ColorToken.RED);
        
        /**************************************
         *  Players's perso
         */
        perso_1 = new PersoIHM(50, 215, "pseudo", Sens.GAUCHE);
        perso_2 = new PersoIHM(105, 90, "pseudo", Sens.HAUT);
    	perso_3 = new PersoIHM(250, 50, "pseudo", Sens.HAUT);
    	perso_4 = new PersoIHM(440, 50, "pseudo", Sens.HAUT);
    	perso_5 = new PersoIHM(600, 90, "pseudo", Sens.HAUT);
    	perso_6 = new PersoIHM(650, 215, "pseudo", Sens.DROITE);
    	perso_7 = new PersoIHM(600, 330, "pseudo", Sens.BAS);
    	perso_8 = new PersoIHM(440, 370, "pseudo", Sens.BAS);
    	perso_9 = new PersoIHM(250, 370, "pseudo", Sens.BAS);
    	perso_10 = new PersoIHM(105, 330, "pseudo", Sens.BAS);
    	
    	this.list_perso = new ArrayList<PersoIHM>();
    	
    	this.list_perso.add(perso_1);
        this.list_perso.add(perso_2);
        this.list_perso.add(perso_3);
        this.list_perso.add(perso_4);
        this.list_perso.add(perso_5);
        this.list_perso.add(perso_6);
        this.list_perso.add(perso_7);
        this.list_perso.add(perso_8);
        this.list_perso.add(perso_9);
        this.list_perso.add(perso_10);
    	
        /**************************************
         *  Players's bets
         */
        bet_player_1 = new TokenBetPlayerIHM(150, 205, 0);
        bet_player_2 = new TokenBetPlayerIHM(170, 145, 0);
        bet_player_3 = new TokenBetPlayerIHM(250, 115, 0);
        bet_player_4 = new TokenBetPlayerIHM(440, 115, 0);
        bet_player_5 = new TokenBetPlayerIHM(530, 145, 0);
        bet_player_6 = new TokenBetPlayerIHM(550, 205, 0);
        bet_player_7 = new TokenBetPlayerIHM(530, 260, 0);
        bet_player_8 = new TokenBetPlayerIHM(440, 270, 0);
        bet_player_9 = new TokenBetPlayerIHM(250, 270, 0);
        bet_player_10 = new TokenBetPlayerIHM(170, 260, 0);
        
        this.list_token_bet = new ArrayList<TokenBetPlayerIHM>();
        
        this.list_token_bet.add(bet_player_1);
        this.list_token_bet.add(bet_player_2);
        this.list_token_bet.add(bet_player_3);
        this.list_token_bet.add(bet_player_4);
        this.list_token_bet.add(bet_player_5);
        this.list_token_bet.add(bet_player_6);
        this.list_token_bet.add(bet_player_7);
        this.list_token_bet.add(bet_player_8);
        this.list_token_bet.add(bet_player_9);
        this.list_token_bet.add(bet_player_10);
        
        /**************************************
         *  Players's card
         */
        card_player_1 = new CardPlayerIHM(85, 205);
        card_player_2 = new CardPlayerIHM(125, 110);
        card_player_3 = new CardPlayerIHM(250, 75);
        card_player_4 = new CardPlayerIHM(440, 75);
        card_player_5 = new CardPlayerIHM(560, 110);
        card_player_6 = new CardPlayerIHM(600, 200);
        card_player_7 = new CardPlayerIHM(560, 280);
        card_player_8 = new CardPlayerIHM(440, 315);
        card_player_9 = new CardPlayerIHM(250, 315);
        card_player_10 = new CardPlayerIHM(125, 285);
        
        this.list_card_player = new ArrayList<CardPlayerIHM>();
        
        this.list_card_player.add(card_player_1);
        this.list_card_player.add(card_player_2);
        this.list_card_player.add(card_player_3);
        this.list_card_player.add(card_player_4);
        this.list_card_player.add(card_player_5);
        this.list_card_player.add(card_player_6);
        this.list_card_player.add(card_player_7);
        this.list_card_player.add(card_player_8);
        this.list_card_player.add(card_player_9);
        this.list_card_player.add(card_player_10);
        
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
        initializeGame(10, 2);
	}
	
	public void initializeGame(int nb_players, int num_player) {
		this.nb_players = nb_players;
		this.num_player = num_player;
		
		for(int i = 0; i < nb_players; i++)
		{
			this.list_perso.get(i).setPseudo("pseudo");
			root.getChildren().add(this.list_perso.get(i));
			root.getChildren().add(this.list_card_player.get(i));
	        root.getChildren().add(this.list_token_bet.get(i));
		}
	}

	public void launchWindow(String[] args) {
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
	                //communauty_card.addCommunautyCard(new Card(CardRank.ACE, CardSuit.CLUBS));
	            	
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
         *  -----  RECEIVED UKNOWN CARD -----
         */
		if(evt.getPropertyName().equals(PlayerGuiEvent.PLAYER_RECEIVED_UNKNOWN_CARD.toString()))
		{
				System.out.println("Empty community card");
		}
		
		/**
         *  -----  RECEIVED CARD -----
         */
		else if(evt.getPropertyName().equals(PlayerGuiEvent.PLAYER_RECEIVED_CARD.toString()))
		{
				System.out.println("Empty community card");
		}
		
		/**
         *  -----  ADD COMMUNITY CARD -----
         */
		else if(evt.getPropertyName().equals(PlayerGuiEvent.ADD_COMMUNITY_CARD.toString()))
		{
			if(evt.getNewValue() instanceof Card)
			{
				communauty_card.addCommunautyCard((Card)evt.getNewValue());
				System.out.println("Add community card");
			}
		}
		
		/**
         *  -----  EMPTY COMMUNITY CARD -----
         */
		else if(evt.getPropertyName().equals(PlayerGuiEvent.EMPTY_COMMUNITY_CARD.toString()))
		{
			communauty_card.emptyCommunautyCard();
			System.out.println("Empty community card");
		}
	}
}
