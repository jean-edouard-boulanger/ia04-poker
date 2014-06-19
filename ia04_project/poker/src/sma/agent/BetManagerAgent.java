package sma.agent;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Map.Entry;

import poker.game.exception.ExcessiveBetException;
import poker.game.exception.NoPlaceAvailableException;
import poker.game.exception.PlayerAlreadyRegisteredException;
import poker.game.model.BetContainer;
import poker.game.model.Game;
import poker.game.player.model.Player;
import poker.game.player.model.PlayerStatus;
import poker.token.exception.InvalidTokenAmountException;
import poker.token.helpers.TokenSetValueEvaluator;
import poker.token.model.TokenSet;
import poker.token.model.TokenValueDefinition;
import sma.agent.helper.AgentHelper;
import sma.agent.helper.DFServiceHelper;
import sma.agent.helper.TransactionBehaviour;
import sma.message.BooleanMessage;
import sma.message.FailureMessage;
import sma.message.Message;
import sma.message.MessageVisitor;
import sma.message.OKMessage;
import sma.message.SubscriptionOKMessage;
import sma.message.bet.request.AreBetsClosedRequest;
import sma.message.bet.request.BetRequest;
import sma.message.bet.request.DistributePotToWinnersRequest;
import sma.message.bet.request.DoesPlayerHaveToBetRequest;
import sma.message.bet.request.MergeBetsRequest;
import sma.message.environment.notification.BetsMergedNotification;
import sma.message.environment.notification.PlayerReceivedTokenSetNotification;
import sma.message.environment.notification.PlayerSitOnTableNotification;
import sma.message.environment.notification.PlayerStatusChangedNotification;
import sma.message.environment.notification.PotEmptiedNotification;
import sma.message.environment.notification.TokenValueDefinitionChangedNotification;
import sma.message.environment.notification.WinnerDeterminedNotification;
import sma.message.environment.request.GiveTokenSetToPlayerRequest;
import sma.message.environment.request.PlayerBetRequest;
import sma.message.environment.request.SendTokenSetToPlayerFromPotRequest;

public class BetManagerAgent extends Agent {

	BetManagerMessageVisitor msgVisitor;
	Game game;
	AID environment;

	public BetManagerAgent(){
		super();
		game = new Game();
		this.msgVisitor = new BetManagerMessageVisitor();
	}

	public void setup(){
		super.setup();
		DFServiceHelper.registerService(this, "BetManagerAgent","BetManager");
		this.environment = DFServiceHelper.searchService(this,"PokerEnvironment", "Environment");
		this.addBehaviour(new ReceiveRequestBehaviour(this));
		this.addBehaviour(new ReceiveNotificationBehaviour(this));
	}

	private boolean areBetsClosed(){
		BetContainer betContainer = game.getBetContainer();
		TokenValueDefinition valueDefinition = betContainer.getTokenValueDefinition();

		int currentBet = betContainer.getCurrentBetAmount();
		for(Entry<AID, TokenSet> bet : betContainer.getPlayersBets().entrySet()){
			if(game.getPlayersContainer().getPlayerByAID(bet.getKey()).getStatus() != PlayerStatus.FOLDED){
				if(TokenSetValueEvaluator.evaluateTokenSetValue(valueDefinition, bet.getValue()) < currentBet){
					return false;
				}
			}
		}
		return true;
	}

	private boolean doesPlayerHaveToBet(AID playerAID){
		
		BetContainer betContainer = this.game.getBetContainer();
		Player player = this.game.getPlayersContainer().getPlayerByAID(playerAID);
		
		if(betContainer.getCurrentBetAmount() == 0){
			return true;
		}
		
		if(betContainer.getPlayerCurrentBetAmount(player) < betContainer.getCurrentBetAmount()){
			return true;
		}
		
		return false;
	}

	private class ReceiveRequestBehaviour extends CyclicBehaviour {

		private AID environment;

		public ReceiveRequestBehaviour(Agent agent) {
			this.myAgent = agent;
			this.environment = DFServiceHelper.searchService(myAgent, "PokerEnvironment", "Environment");
			subscribeToEnvironment();
		}

		@Override
		public void action() {

			boolean msgReceived = AgentHelper.receiveMessage(this.myAgent, ACLMessage.REQUEST, ((BetManagerAgent)myAgent).getMsgVisitor());

			if(!msgReceived)
				block();
		}

		private void subscribeToEnvironment(){

			TransactionBehaviour envSubscriptionBhv = new TransactionBehaviour(myAgent, null, environment, ACLMessage.SUBSCRIBE);
			envSubscriptionBhv.setResponseVisitor(new MessageVisitor(){

				@Override
				public boolean onSubscriptionOK(SubscriptionOKMessage msg, ACLMessage aclMsg) {
					System.out.println("[" + myAgent.getLocalName() + "] subscription to environment succeded.");
					((BetManagerAgent)myAgent).game = msg.getGame();
					return true;
				}

				@Override
				public boolean onFailureMessage(FailureMessage msg, ACLMessage aclMsg) {
					System.out.println("[" + myAgent.getLocalName() + "] subscription to environment failed: " + msg.getMessage());
					return true;
				}

			});
			myAgent.addBehaviour(envSubscriptionBhv);
		}
	}

	private class ReceiveNotificationBehaviour extends CyclicBehaviour {
		public ReceiveNotificationBehaviour(Agent agent) {
			myAgent = agent;
		}

		@Override
		public void action() {
			boolean msgReceived = AgentHelper.receiveMessage(this.myAgent, ACLMessage.PROPAGATE, ((BetManagerAgent)myAgent).getMsgVisitor());

			if(!msgReceived)
				block();
		}	
	}

	private void notifyBetToEnvironment(PlayerBetRequest playerBetRequest, GiveTokenSetToPlayerRequest giveTokenSetToPlayerRequest, final ACLMessage messageToAnswer) {

		SequentialBehaviour sequentialBehaviour = new SequentialBehaviour(BetManagerAgent.this);

		TransactionBehaviour playerBetTransaction = new TransactionBehaviour(this, playerBetRequest, environment, ACLMessage.REQUEST);
		playerBetTransaction.setResponseVisitor(new MessageVisitor(){

			@Override
			public boolean onOKMessage(OKMessage msg, ACLMessage aclMsg) {
				System.out.println("[" + BetManagerAgent.this.getLocalName() + "] player bet succeded.");
				return true;
			}

			@Override
			public boolean onFailureMessage(FailureMessage msg, ACLMessage aclMsg) {
				System.out.println("[" + BetManagerAgent.this.getLocalName() + "] player bet failed: " + msg.getMessage());
				return true;
			}
		});

		TransactionBehaviour giveTokenSetToPlayerTransaction = new TransactionBehaviour(this, giveTokenSetToPlayerRequest, environment, ACLMessage.REQUEST);
		giveTokenSetToPlayerTransaction.setResponseVisitor(new MessageVisitor(){

			@Override
			public boolean onOKMessage(OKMessage msg, ACLMessage aclMsg) {
				System.out.println("[" + BetManagerAgent.this.getLocalName() + "] added token set to player.");
				return true;
			}

			@Override
			public boolean onFailureMessage(FailureMessage msg, ACLMessage aclMsg) {
				System.out.println("[" + BetManagerAgent.this.getLocalName() + "] added token set to player: " + msg.getMessage());
				return true;
			}
		});

		sequentialBehaviour.addSubBehaviour(playerBetTransaction);
		sequentialBehaviour.addSubBehaviour(giveTokenSetToPlayerTransaction);
		sequentialBehaviour.addSubBehaviour(new OneShotBehaviour() {

			@Override
			public void action() {
				AgentHelper.sendReply(BetManagerAgent.this, messageToAnswer, ACLMessage.INFORM, new OKMessage());
			}
		});


		BetManagerAgent.this.addBehaviour(sequentialBehaviour);
	}

	private class BetManagerMessageVisitor extends MessageVisitor {	
		
		@Override
		public boolean onDistributePotToWinnersRequest(DistributePotToWinnersRequest request, final ACLMessage aclMsg) {
			
			System.out.println("DEBUG [BetManagerAgent:onDistributePotToWinnersRequest] Distribute pot to winner request received");

			SequentialBehaviour sendTokenSetRequestsBehaviour = new SequentialBehaviour(BetManagerAgent.this);

			int nbWinners = request.getWinnersAIDs().size();

			int totalAmount = game.getBetContainer().getPotAmount();
			int singleAmount = totalAmount / nbWinners;

			TokenSet sentTokenSet = TokenSetValueEvaluator.tokenSetFromAmount(singleAmount, game.getBetContainer().getTokenValueDefinition());
			SendTokenSetToPlayerFromPotRequest sentRequest = new SendTokenSetToPlayerFromPotRequest();
			sentRequest.setSentTokenSet(sentTokenSet);

			for(AID playerAID : request.getWinnersAIDs()){
				sentRequest.setPlayerAID(playerAID);

				TransactionBehaviour tokenSetSendTransactionBehaviour = new TransactionBehaviour(BetManagerAgent.this, sentRequest, environment);
				tokenSetSendTransactionBehaviour.setResponseVisitor(new SendTokenSetToWinnerMessageVisitor(playerAID, sentTokenSet));
				
				sendTokenSetRequestsBehaviour.addSubBehaviour(tokenSetSendTransactionBehaviour);							
			}

			sendTokenSetRequestsBehaviour.addSubBehaviour(new OneShotBehaviour() {
				@Override
				public void action() {
					System.out.println("DEBUG [BetManagerAgent:onDistributePotToWinnersRequest] Replied OK to " + aclMsg.getSender().getLocalName());
					AgentHelper.sendReply(BetManagerAgent.this, aclMsg, ACLMessage.INFORM, new OKMessage());
				}
			});

			addBehaviour(sendTokenSetRequestsBehaviour);
			
			return true;
		}
		
		@Override
		public boolean onDoesPlayerHaveToBetRequest(DoesPlayerHaveToBetRequest request, ACLMessage aclMessage){
			
			BooleanMessage response = new BooleanMessage(doesPlayerHaveToBet(request.getPlayerAID()));
			AgentHelper.sendReply(BetManagerAgent.this, aclMessage, ACLMessage.INFORM, response);
			
			return true;
		}
		
		@Override
		public boolean onPlayerReceivedTokenSetNotification(PlayerReceivedTokenSetNotification notification, ACLMessage aclMsg){

			System.out.println("[" + getLocalName() + "]" + "PlayerReceivedTokenSetNotification of amount(" + TokenSetValueEvaluator.evaluateTokenSetValue(game.getBetContainer().getTokenValueDefinition(), notification.getReceivedTokenSet()) + "), for player (" + notification.getPlayerAID() + ").");

			Player player = game.getPlayersContainer().getPlayerByAID(notification.getPlayerAID());
			player.setTokens(player.getTokens().addTokenSet(notification.getReceivedTokenSet()));

			return true;
		}

		@Override
		public boolean onBetRequest(BetRequest request, ACLMessage aclMsg) {

			System.out.println("[" + getLocalName() + "]" + "BetRequest of amount(" + request.getBet() + "), for player (" + request.getPlayerAID() + ").");

			Player player = game.getPlayersContainer().getPlayerByAID(request.getPlayerAID());

			int playerCurrentBet = game.getBetContainer().getPlayerCurrentBetAmount(player);
			
			int playerPot = TokenSetValueEvaluator.evaluateTokenSetValue(game.getBetContainer().getTokenValueDefinition(), player.getTokens()) + playerCurrentBet;
			int currentBetAmount = game.getBetContainer().getCurrentBetAmount();

			if(playerPot < request.getBet())
			{
				AgentHelper.sendReply(BetManagerAgent.this, aclMsg, ACLMessage.INFORM, new FailureMessage("Your bankroll is too small to place this bet"));
			}
			else if(request.getBet() < currentBetAmount && playerPot != request.getBet())
			{
				if(playerPot >= currentBetAmount)
					AgentHelper.sendReply(BetManagerAgent.this, aclMsg, ACLMessage.INFORM, new FailureMessage("You must bet " + currentBetAmount + " in order to call"));
				else
					AgentHelper.sendReply(BetManagerAgent.this, aclMsg, ACLMessage.INFORM, new FailureMessage("You must go all in."));
			}
			else if(request.getBet() > currentBetAmount && request.getBet() < 2 * currentBetAmount && playerPot != request.getBet())
			{
				AgentHelper.sendReply(BetManagerAgent.this, aclMsg, ACLMessage.INFORM, new FailureMessage("You must bet at leat " + 2 * currentBetAmount + " in order to raise"));
			}
			else 
			{
				try {
					//Removing tokens of the used if allowed to
					TokenSet tokenSetToSubstract = TokenSetValueEvaluator.tokenSetForBet(request.getBet() - playerCurrentBet, game.getBetContainer().getTokenValueDefinition(), player.getTokens());
					player.getTokens().substractTokenSet(tokenSetToSubstract);

					//Creating extra token set user paid (Ex: User paid 50 instead of 40)
					int amountToGenerate = TokenSetValueEvaluator.evaluateTokenSetValue(game.getBetContainer().getTokenValueDefinition(), tokenSetToSubstract) - (request.getBet() - playerCurrentBet);

					TokenSet tokenSetToAddToPlayer = TokenSetValueEvaluator.tokenSetFromAmount(amountToGenerate, game.getBetContainer().getTokenValueDefinition());

					//Updating the player's bet amount
					game.getBetContainer().setPlayerCurrentBet(request.getPlayerAID(), TokenSetValueEvaluator.tokenSetFromAmount(request.getBet(), game.getBetContainer().getTokenValueDefinition()));
					
					//Notifying the environment: sequential behaviour					
					notifyBetToEnvironment(new PlayerBetRequest(tokenSetToSubstract, request.getPlayerAID(), request.getBet()), new GiveTokenSetToPlayerRequest(tokenSetToAddToPlayer, request.getPlayerAID()), aclMsg);
				} 
				catch (InvalidTokenAmountException | ExcessiveBetException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			return true;
		}
		
		// -----------------------------------------
		// Environment event handling
		// -----------------------------------------

		@Override
		public boolean onTokenValueDefinitionChangedNotification(TokenValueDefinitionChangedNotification notif, ACLMessage aclMsg) {

			game.getBetContainer().setTokenValueDefinition(notif.getTokenValueDefinition());

			return true;
		}

		@Override
		public boolean onWinnerDeterminedNotification(WinnerDeterminedNotification notification, ACLMessage aclMsg) {

			//End of the round, have to reset the bets of the players
			game.getBetContainer().clearPlayerBets();

			return true;
		}

		@Override
		public boolean onMergeBetsRequest(MergeBetsRequest request, ACLMessage aclMsg) {

			game.getBetContainer().transferCurrentBetsToPot();

			AgentHelper.sendReply(BetManagerAgent.this, aclMsg, ACLMessage.INFORM, new OKMessage());

			AgentHelper.sendSimpleMessage(BetManagerAgent.this, environment, ACLMessage.INFORM, new BetsMergedNotification());
			
			return true;
		}

		@Override
		public boolean onPlayerSitOnTableNotification(PlayerSitOnTableNotification notification, ACLMessage aclMsg) {
			try {
				game.getPlayersContainer().addPlayer(notification.getNewPlayer());
			} catch (PlayerAlreadyRegisteredException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoPlaceAvailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}

		@Override
		public boolean onPlayerStatusChangedNotification(PlayerStatusChangedNotification notification, ACLMessage aclMsg) {

			Player player = game.getPlayersContainer().getPlayerByAID(notification.getPlayerAID());
			if(player != null){
				player.setStatus(notification.getNewStatus());
			}

			return true;
		}

		@Override
		public boolean onAreBetsClosedRequest(AreBetsClosedRequest request, ACLMessage aclMsg) {

			if(areBetsClosed()){
				AgentHelper.sendReply(BetManagerAgent.this, aclMsg, ACLMessage.INFORM, new BooleanMessage(true));
			}
			else {
				AgentHelper.sendReply(BetManagerAgent.this, aclMsg, ACLMessage.INFORM, new BooleanMessage(false));
			}

			return true;
		}
		
		@Override
		public boolean onPotEmptiedNotification(PotEmptiedNotification potEmptiedNotification, ACLMessage aclMsg) {
			game.getBetContainer().clearPot();
			return true;
		}

		// All other environment changes are discarded.
		@Override
		public boolean onEnvironmentChanged(Message notif, ACLMessage aclMsg) {	return true; }
	}

	public BetManagerMessageVisitor getMsgVisitor() {
		return msgVisitor;
	}

	public void setMsgVisitor(BetManagerMessageVisitor msgVisitor) {
		this.msgVisitor = msgVisitor;
	}
	
	private class SendTokenSetToWinnerMessageVisitor extends MessageVisitor{
		
		AID playerAID;
		TokenSet tokenSet;
		
		public SendTokenSetToWinnerMessageVisitor(AID playerAID, TokenSet tokenSet){
			this.playerAID = playerAID;
			this.tokenSet = tokenSet;
		}
		
		public boolean onFailureMessage(FailureMessage msg, ACLMessage aclMsg) {
			System.err.println("ERROR [BetManagerAgent] Could not send TokenSet to winner " + playerAID.getLocalName());
			return true;
		}
			
		public boolean onOKMessage(OKMessage okMessage, ACLMessage aclMsg) {
			System.out.print("DEBUG [BetManagerAgent] TokenSet sent to winner " + playerAID.getLocalName());
			game.getPlayersContainer().getPlayerByAID(playerAID).getTokens().addTokenSet(tokenSet);
			return true;
		}
	}
}
