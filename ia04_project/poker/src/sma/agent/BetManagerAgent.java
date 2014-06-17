package sma.agent;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import poker.game.exception.ExcessiveBetException;
import poker.game.exception.NoPlaceAvailableException;
import poker.game.exception.PlayerAlreadyRegisteredException;
import poker.game.model.BetContainer;
import poker.game.model.Game;
import poker.game.player.model.Player;
import poker.token.exception.InvalidTokenAmountException;
import poker.token.helpers.TokenSetValueEvaluator;
import poker.token.model.TokenSet;
import sma.agent.helper.AgentHelper;
import sma.agent.helper.DFServiceHelper;
import sma.agent.helper.TransactionBhv;
import sma.message.FailureMessage;
import sma.message.Message;
import sma.message.MessageVisitor;
import sma.message.OKMessage;
import sma.message.SubscriptionOKMessage;
import sma.message.bet.notification.BetsMergedNotification;
import sma.message.bet.notification.PotAmountNotification;
import sma.message.bet.request.BetRequest;
import sma.message.bet.request.GetPotAmountRequest;
import sma.message.bet.request.MergeBetsRequest;
import sma.message.environment.notification.PlayerReceivedTokenSetNotification;
import sma.message.environment.notification.PlayerSitOnTableNotification;
import sma.message.environment.notification.TokenValueDefinitionChangedNotification;
import sma.message.environment.notification.WinnerDeterminedNotification;
import sma.message.environment.request.GiveTokenSetToPlayerRequest;
import sma.message.environment.request.PlayerBetRequest;

public class BetManagerAgent extends Agent {

	BetContainer betContainer;
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
			
			TransactionBhv envSubscriptionBhv = new TransactionBhv(myAgent, null, environment, ACLMessage.SUBSCRIBE);
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
		
		TransactionBhv playerBetTransaction = new TransactionBhv(this, playerBetRequest, environment, ACLMessage.REQUEST);
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
		
		TransactionBhv giveTokenSetToPlayerTransaction = new TransactionBhv(this, giveTokenSetToPlayerRequest, environment, ACLMessage.REQUEST);
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
			
			int playerPot = TokenSetValueEvaluator.evaluateTokenSetValue(game.getBetContainer().getTokenValueDefinition(), player.getTokens());
			
			if(playerPot >= request.getBet() && request.getBet() >= game.getBetContainer().getCurrentBetAmount()) {
				try {
					//Removing tokens of the used if allowed to
					TokenSet tokenSetToSubstract = TokenSetValueEvaluator.tokenSetForBet(request.getBet(), game.getBetContainer().getTokenValueDefinition(), player.getTokens());
					player.getTokens().substractTokenSet(tokenSetToSubstract);
					
					//Creating extra token set user paid (Ex: Used paid 50 instead of 40)
					int amountToGenerate = TokenSetValueEvaluator.evaluateTokenSetValue(game.getBetContainer().getTokenValueDefinition(), tokenSetToSubstract) - request.getBet();
					
					TokenSet tokenSetToAddToPlayer = TokenSetValueEvaluator.tokenSetFromAmount(amountToGenerate, game.getBetContainer().getTokenValueDefinition());
					
					//Updating the player's bet amount
					game.getBetContainer().setPlayerCurrentBet(request.getPlayerAID(), TokenSetValueEvaluator.tokenSetFromAmount(request.getBet(), game.getBetContainer().getTokenValueDefinition()));
					
					//Notifying the environment: sequential behaviour					
					notifyBetToEnvironment(new PlayerBetRequest(tokenSetToSubstract, request.getPlayerAID(), request.getBet()), new GiveTokenSetToPlayerRequest(tokenSetToAddToPlayer, request.getPlayerAID()), aclMsg);					
				} catch (InvalidTokenAmountException | ExcessiveBetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				AgentHelper.sendReply(BetManagerAgent.this, aclMsg, ACLMessage.INFORM, new FailureMessage("Player does not have enough token or tried to place a too small bet."));
			}
			
			return true;
		}
		
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
			
			AgentHelper.sendReply(BetManagerAgent.this, aclMsg, ACLMessage.INFORM, new BetsMergedNotification());
			
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
		public boolean onGetPotAmountRequest(GetPotAmountRequest request, ACLMessage aclMsg) {

			PotAmountNotification potAmountNotification = new PotAmountNotification(game.getBetContainer().getPot());
			
			AgentHelper.sendReply(BetManagerAgent.this, aclMsg, ACLMessage.INFORM, potAmountNotification);
			
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
}
