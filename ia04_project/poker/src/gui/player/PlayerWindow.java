package gui.player;

import gui.player.PersoIHM.Sens;
import gui.player.TokenPlayerIHM.ColorToken;
import jade.gui.GuiEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import poker.card.helper.CardImageHelper;
import poker.card.model.Card;
import poker.game.player.model.Player;
import poker.token.model.TokenType;
import sma.agent.HumanPlayerAgent;

/**
 * 
 * Class used for testing purpose only.
 *
 */
public class PlayerWindow extends Application implements PropertyChangeListener {

	public enum PlayerGuiEvent {
		INITIALIZING_ME,
		INITIALIZING_OTHER,
		INITIALIZING_MIN_TOKEN,
		PLAYER_RECEIVED_UNKNOWN_CARD,
		PLAYER_RECEIVED_CARD,
		ADD_COMMUNITY_CARD,
		EMPTY_COMMUNITY_CARD,
		PLAYER_FOLDED,
		PLAYER_RECEIVED_TOKENSET_ME,
		PLAYER_RECEIVED_TOKENSET_OTHER,
		PLAYER_BET,
		PLAYER_CHECK,
		BLIND_VALUE,
		CURRENT_PLAYER_CHANGED,

		IHM_READY,
		SHOW_IHM
	}

	private final Pane root = new Pane();

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

	private int current_player = 0;

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

	private List<ImageView> player_cards;

	// scaling:
	private double scaleRatio = 1;
	private double stageInitialWidth = 0;
	private double stageInitialHeight = 0;
	private double aspectRatio;

	private Stage primaryStage;

	public void setHumanPlayerAgent(HumanPlayerAgent agent)
	{
		this.human_player_agent = agent;
	}

	@Override
	public void start(Stage primaryStage) {

		//--------------------------------------

		this.primaryStage = primaryStage;

		primaryStage.setTitle("Poker");

		root.setId("root");

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

		button_relaunch.setText("Relancer à  5");

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
		textfield_bet.setText("0");
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
		token_white = new TokenPlayerIHM(485, 500, 0, ColorToken.WHITE);
		token_black = new TokenPlayerIHM(515, 500, 0, ColorToken.BLACK);
		token_blue = new TokenPlayerIHM(545, 500, 0, ColorToken.BLUE);
		token_green = new TokenPlayerIHM(575, 500, 0, ColorToken.GREEN);
		token_red = new TokenPlayerIHM(605, 500, 0, ColorToken.RED);

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

		player_cards = new ArrayList<ImageView>();

		ImageView im = new ImageView();
		im.setX(295);
		im.setY(415);
		im.setFitWidth(40);
		im.setFitHeight(62);
		im.setRotate(-15);

		player_cards.add(im);

		ImageView im2 = new ImageView();
		im2.setX(350);
		im2.setY(415);
		im2.setFitWidth(40);
		im2.setFitHeight(62);
		im2.setRotate(15);

		player_cards.add(im2);

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

		final Pane background = new Pane();
		background.setId("background");
		background.getChildren().add(root);
		Scene scene = new Scene(background, 700, 600);
		scene.setFill(Color.TRANSPARENT);

		primaryStage.setScene(scene);
		primaryStage.setResizable(true);
		primaryStage.getScene().getStylesheets().setAll(PlayerWindow.class.getResource("/gui/player/application.css").toString());

		// scale the entire scene as the stage is resized (see https://community.oracle.com/thread/2415190).
		this.stageInitialWidth = scene.getWidth();
		this.stageInitialHeight = scene.getHeight();
		this.aspectRatio = ((double)scene.getWidth())/scene.getHeight();

		background.getScene().widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				double newWidth = newValue.doubleValue();
				if(background.getWidth()/background.getHeight() > aspectRatio)
					newWidth = (aspectRatio) * background.getHeight();

				scaleRatio = newWidth / stageInitialWidth;
				root.getTransforms().clear();
				Scale scale = new Scale(scaleRatio, scaleRatio, 0, 0);
				root.getTransforms().add(scale);
			}
		});

		background.getScene().heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				double newHeight = newValue.doubleValue();
				if(background.getWidth()/background.getHeight() < aspectRatio)
					newHeight = background.getWidth() / aspectRatio;

				scaleRatio = newHeight / stageInitialHeight;
				root.getTransforms().clear();
				Scale scale = new Scale(scaleRatio, scaleRatio, 0, 0);
				root.getTransforms().add(scale);
			}
		});


		initializeAction();

		GuiEvent ev = new GuiEvent(this, PlayerGuiEvent.IHM_READY.ordinal());
		human_player_agent.postGuiEvent(ev);
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

		// Rajouter notification property pour dire Ã  l'agent qu'on est projet n'afficher qu'Ã  ce moment les interfaces
	}

	public static PlayerWindow launchWindow(HumanPlayerAgent agent, PropertyChangeSupport changes) {
		try {
			PlayerWindow app1 = PlayerWindow.class.newInstance();
			app1.setHumanPlayerAgent(agent);
			changes.addPropertyChangeListener(app1);
			Stage newStage = new Stage();
			app1.start(newStage);

			return app1;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/**************************************
	 *  Interaction notification
	 */

	public void show()
	{
		PlatformHelper.run(new Runnable() {
			@Override public void run() {
				primaryStage.show();
			}
		});
	}

	public void initializeMe(final Player player)
	{
		PlatformHelper.run(new Runnable() {
			@Override public void run() {
				int position_player = player.getTablePositionIndex();

				PlayerWindow.this.list_perso.get(position_player).setPseudo(player.getNickname());
				PlayerWindow.this.list_perso.get(position_player).setMe();
				root.getChildren().add(PlayerWindow.this.list_perso.get(position_player));
				root.getChildren().add(PlayerWindow.this.list_card_player.get(position_player));
				root.getChildren().add(PlayerWindow.this.list_token_bet.get(position_player));

				PlayerWindow.this.list_perso.get(position_player).setCurrentPlayer();
			}
		});
	}

	public void initializeOther(final Player player)
	{
		PlatformHelper.run(new Runnable() {
			@Override public void run() {
				int position_player = player.getTablePositionIndex();

				PlayerWindow.this.list_perso.get(position_player).setPseudo(player.getNickname());
				root.getChildren().add(PlayerWindow.this.list_perso.get(position_player));
				root.getChildren().add(PlayerWindow.this.list_card_player.get(position_player));
				root.getChildren().add(PlayerWindow.this.list_token_bet.get(position_player));
			}
		});
	}
	
	public void initializeMinToken(final Integer min_token)
	{
		PlatformHelper.run(new Runnable() {
			@Override public void run() {
				slider_bet.setMajorTickUnit(min_token);
				slider_bet.setBlockIncrement(25);
			}
		});
	}

	public void initializePlayerReceivedCard(final Card card) {
		PlatformHelper.run(new Runnable() {
			@Override public void run() {
				Image image = CardImageHelper.getCardImage(card);

				if(player_cards.get(0).getImage() == null) {
					player_cards.get(0).setImage(image);
				}
				else if(player_cards.get(1).getImage() == null) {
					player_cards.get(1).setImage(image);
				}
			}
		});
	}

	public void initializePlayerReceivedUnknownCard(final Integer index) {
		list_card_player.get(index).addUnknownCard();	
	}

	public void addCommunityCard(final Card card) {
		PlatformHelper.run(new Runnable() {
			@Override public void run() {
				communauty_card.addCommunautyCard(card);
			}
		});
	}

	public void emptyCommunityCard() {
		PlatformHelper.run(new Runnable() {
			@Override public void run() {
				player_cards.get(0).setImage(null);
				player_cards.get(1).setImage(null);

				communauty_card.emptyCommunautyCard();
			}
		});
	}

	public void receivedTokensMe(final Player player) {
		PlatformHelper.run(new Runnable() {
			@Override public void run() {
				Map<TokenType, Integer> token_map = player.getTokens().getTokensAmount();
				token_white.setMise(token_map.get(TokenType.WHITE));
				token_green.setMise(token_map.get(TokenType.GREEN));
				token_blue.setMise(token_map.get(TokenType.BLUE));
				token_black.setMise(token_map.get(TokenType.BLACK));
				token_red.setMise(token_map.get(TokenType.RED));

				PlayerWindow.this.list_perso.get(player.getTablePositionIndex()).setScore(PersoIHM.calculateScore(player.getTokens()));
				PlayerWindow.this.slider_bet.setMax(PersoIHM.calculateScore(player.getTokens()));
			}
		});
	}

	public void receivedTokensOther(final Player player) {
		PlatformHelper.run(new Runnable() {
			@Override public void run() {
				PlayerWindow.this.list_perso.get(player.getTablePositionIndex()).setScore(PersoIHM.calculateScore(player.getTokens()));
			}
		});
	}

	public void changeCurrentPlayer(final Integer index_player) {
		PlatformHelper.run(new Runnable() {
			@Override public void run() {
				PlayerWindow.this.list_perso.get(PlayerWindow.this.current_player).unsetCurrentPlayer();
				PlayerWindow.this.list_perso.get(index_player).setCurrentPlayer();
				PlayerWindow.this.current_player = index_player;
			}
		});
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
	public void propertyChange(final PropertyChangeEvent evt) {
		PlatformHelper.run(new Runnable() {
			@Override public void run() {
				/**
				 *  -----  SHOW IHM -----
				 */
				if(evt.getPropertyName().equals(PlayerGuiEvent.SHOW_IHM.toString()))
				{
					primaryStage.show();
					System.out.println("[PlayerWindow] Showing IHM");
				}

				/**
				 *  -----  INITIALIZING ME -----
				 */
				else if(evt.getPropertyName().equals(PlayerGuiEvent.INITIALIZING_ME.toString()))
				{
					if(evt.getNewValue() instanceof Player)
					{
						Player player = (Player)evt.getNewValue();
						initializeMe(player);
						System.out.println("[PlayerWindow] Initialiazing me");
					}

					System.out.println("[PlayerWindow] Initialiazing me");
				}

				/**
				 *  -----  INITIALIZING AN OTHER PLAYER-----
				 */
				if(evt.getPropertyName().equals(PlayerGuiEvent.INITIALIZING_OTHER.toString()))
				{
					if(evt.getNewValue() instanceof Player)
					{
						Player player = (Player)evt.getNewValue();
						initializeOther(player);
					}

					System.out.println("[PlayerWindow] Initialiazing other");
				}
				
				/**
				 *  -----  INITIALIZING MIN TOKEN -----
				 */
				if(evt.getPropertyName().equals(PlayerGuiEvent.INITIALIZING_MIN_TOKEN.toString()))
				{
					if(evt.getNewValue() instanceof Integer)
					{
						Integer min_token = (Integer)evt.getNewValue();
						initializeMinToken(min_token);
					}

					System.out.println("[PlayerWindow] Initialiazing min token");
				}

				/**
				 *  -----  RECEIVED UKNOWN CARD -----
				 */
				else if(evt.getPropertyName().equals(PlayerGuiEvent.PLAYER_RECEIVED_UNKNOWN_CARD.toString()))
				{
					System.out.println("[PlayerWindow] Player "+ (Integer)evt.getNewValue() +" received an unknown card.");
					initializePlayerReceivedUnknownCard((Integer)evt.getNewValue());
				}

				/**
				 *  -----  RECEIVED CARD -----
				 */
				else if(evt.getPropertyName().equals(PlayerGuiEvent.PLAYER_RECEIVED_CARD.toString()))
				{
					initializePlayerReceivedCard((Card)evt.getNewValue());
					System.out.println("[PlayerWindow] Player received card." + (Card)evt.getNewValue());
				}

				/**
				 *  -----  ADD COMMUNITY CARD -----
				 */
				else if(evt.getPropertyName().equals(PlayerGuiEvent.ADD_COMMUNITY_CARD.toString()))
				{
					if(evt.getNewValue() instanceof Card)
					{
						addCommunityCard((Card)evt.getNewValue());
						System.out.println("[PlayerWindow] Add community card");
					}
				}

				/**
				 *  -----  EMPTY COMMUNITY CARD -----
				 */
				else if(evt.getPropertyName().equals(PlayerGuiEvent.EMPTY_COMMUNITY_CARD.toString()))
				{
					emptyCommunityCard();
					System.out.println("[PlayerWindow] Empty community card");
				}

				/**
				 *  -----  PLAYER RECEIVED TOKENSET ME -----
				 */
				else if(evt.getPropertyName().equals(PlayerGuiEvent.PLAYER_RECEIVED_TOKENSET_ME.toString()))
				{
					if(evt.getNewValue() instanceof Player)
					{
						receivedTokensMe((Player)evt.getNewValue());
						System.out.println("[PlayerWindow] Player received token set me");
					}
				}

				/**
				 *  -----  PLAYER RECEIVED TOKENSET OTHER -----
				 */
				else if(evt.getPropertyName().equals(PlayerGuiEvent.PLAYER_RECEIVED_TOKENSET_OTHER.toString()))
				{
					if(evt.getNewValue() instanceof Player)
					{
						receivedTokensOther((Player)evt.getNewValue());
						System.out.println("[PlayerWindow] Player received token set other");
					}
				}

				/**
				 *  -----  CURRENT PLAYER -----
				 */
				else if(evt.getPropertyName().equals(PlayerGuiEvent.CURRENT_PLAYER_CHANGED.toString()))
				{
					if(evt.getNewValue() instanceof Integer)
					{
						changeCurrentPlayer((Integer)evt.getNewValue());
						System.out.println("[PlayerWindow] Player current changed");
					}
				}	
			}
		});
	}
}
