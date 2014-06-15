package sma.agent;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.HashMap;

import poker.card.model.CardDeck;
import poker.game.exception.NoPlaceAvailableException;
import poker.game.exception.NotRegisteredPlayerException;
import poker.game.exception.PlayerAlreadyRegisteredException;
import poker.game.model.Round;
import poker.game.model.PlayersContainer;
import poker.game.model.PlayersContainer.PlayerCircularIterator;
import poker.game.player.model.Player;
import poker.game.player.model.PlayerStatus;
import sma.agent.helper.AgentHelper;
import sma.agent.helper.DFServiceHelper;
import sma.agent.helper.TransactionBhv;
import sma.message.FailureMessage;
import sma.message.Message;
import sma.message.MessageVisitor;
import sma.message.OKMessage;
import sma.message.SubscriptionOKMessage;
import sma.message.dealer.request.DealRequest;
import sma.message.environment.notification.DealerChangedNotification;
import sma.message.environment.notification.PlayerFoldedNotification;
import sma.message.environment.notification.PlayerSitOnTableNotification;
import sma.message.environment.request.AddCommunityCardRequest;
import sma.message.environment.request.DealCardToPlayerRequest;

public class DealerAgent extends Agent {

    PlayersContainer playersContainer;
    CardDeck cardDeck;

    private HashMap<String, ArrayList<String>> dealTransactionsErrors;

    DealerMessageVisitor messageVisitor;

    public DealerAgent(){
	super();
	this.dealTransactionsErrors = new HashMap<String, ArrayList<String>>();
	this.messageVisitor = new DealerMessageVisitor();
    }

    public void setup(){
	DFServiceHelper.registerService(this, "DealerAgent","Dealer");
	this.addBehaviour(new ReceiveEnvironmentNotificationBehaviour(this));
	this.addBehaviour(new DealCardsBehaviour());
    }

    private void registerDealTransaction(String cid){
	if(!dealTransactionsErrors.containsKey(cid)){
	    dealTransactionsErrors.put(cid, null);
	}
    }

    private void addErrorForDealTransaction(String cid, String error){
	if(!dealTransactionsErrors.containsKey(cid)){
	    dealTransactionsErrors.put(cid, null);
	}
	dealTransactionsErrors.get(cid).add(error);
    }

    private ArrayList<String> getErrorsForDealTransaction(String cid){
	return this.dealTransactionsErrors.get(cid);
    }

    private void releaseDealTransaction(String cid){
	this.dealTransactionsErrors.remove(cid);
    }


    private class ReceiveEnvironmentNotificationBehaviour extends CyclicBehaviour
    {

	private DealerAgent dealerAgent;
	private AID environment;

	public ReceiveEnvironmentNotificationBehaviour(DealerAgent agent){
	    super(agent);
	    this.dealerAgent = agent;
	    this.environment = DFServiceHelper.searchService(dealerAgent,"PokerEnvironment", "Environment");
	    subscribeToEnvironment();
	}

	/**
	 * Handle environment events.
	 */
	@Override
	public void action() 
	{

	    boolean msgReceived = AgentHelper.receiveMessage(this.myAgent,MessageTemplate.MatchPerformative(ACLMessage.PROPAGATE), new MessageVisitor(){

		// we update players list, players status change, dealer change 

		@Override
		public boolean onPlayerSitOnTableNotification(PlayerSitOnTableNotification notif, ACLMessage aclMsg) {
		    try {
			playersContainer.addPlayer(notif.getNewPlayer());
		    } catch (PlayerAlreadyRegisteredException | NoPlaceAvailableException e) {
			System.out.println("[" + myAgent.getLocalName() + "] can't add player, environment inconsistency.");
		    }
		    return true;
		}

		@Override
		public boolean onDealerChangedNotification(DealerChangedNotification notif, ACLMessage aclMsg) {
		    Player dealer = playersContainer.getPlayerByAID(notif.getDealer());
		    try {
			playersContainer.setDealer(dealer);
		    } catch (NotRegisteredPlayerException e) {
			System.out.println("[" + myAgent.getLocalName() + "] the dealer '" + notif.getDealer().getLocalName() + "' is not a player, environment inconsistency.");
		    }
		    return true;
		}

		// TODO: player out, new hand ?

		// All other environment changes are discarded.
		@Override
		public boolean onEnvironmentChanged(Message notif, ACLMessage aclMsg) {	return true; }
	    });

	    if(!msgReceived)
		block();
	}

	private void subscribeToEnvironment(){

	    TransactionBhv envSubscriptionBhv = new TransactionBhv(dealerAgent, null, environment, ACLMessage.SUBSCRIBE);
	    envSubscriptionBhv.setResponseVisitor(new MessageVisitor(){

		@Override
		public boolean onSubscriptionOK(SubscriptionOKMessage msg, ACLMessage aclMsg) {
		    System.out.println("[" + dealerAgent.getLocalName() + "] subscription to environment succeded.");
		    // Initialization of the initial player container.
		    playersContainer = msg.getGame().getPlayersContainer();
		    return true;
		}

		@Override
		public boolean onFailureMessage(FailureMessage msg, ACLMessage aclMsg) {
		    System.out.println("[" + dealerAgent.getLocalName() + "] subscription to environment failed: " + msg.getMessage());
		    return true;
		}

	    });
	    dealerAgent.addBehaviour(envSubscriptionBhv);
	}
    }

    private class DealCardsBehaviour extends CyclicBehaviour{
	@Override
	public void action() {
	    if(!AgentHelper.receiveMessage(DealerAgent.this, ACLMessage.REQUEST, messageVisitor)){
		block();
	    }
	}
    }

    private class ConcludeDealPlayersCardsTransactionBehaviour extends OneShotBehaviour
    {
	private ACLMessage request;

	public ConcludeDealPlayersCardsTransactionBehaviour(ACLMessage request){
	    this.request = request;
	}

	@Override
	public void action() {

	    Message conclusionMessage = null;
	    int performative = ACLMessage.INFORM;

	    ArrayList<String> errors = getErrorsForDealTransaction(request.getConversationId());

	    if(getErrorsForDealTransaction(request.getConversationId()) != null){
		conclusionMessage = new FailureMessage();
		performative = ACLMessage.FAILURE;

		StringBuffer sb = new StringBuffer();
		for(String error : errors){
		    sb.append(error).append(System.lineSeparator());
		}

		((FailureMessage)conclusionMessage).setMessage(sb.toString());
	    }
	    else {
		conclusionMessage = new OKMessage();
	    }

	    AgentHelper.sendReply(DealerAgent.this, this.request, performative, conclusionMessage);
	    releaseDealTransaction(this.request.getConversationId());
	}
    }

    private class DealerMessageVisitor extends MessageVisitor{

	public boolean onPlayerSitOnTableNotification(PlayerSitOnTableNotification notification, ACLMessage aclMsg){
	    try{
		playersContainer.addPlayer(notification.getNewPlayer());
	    }catch(Exception ex){
		ex.printStackTrace();
	    }		
	    return true;
	}

	public boolean onDealRequest(DealRequest request, ACLMessage aclMsg){

	    AID EnvAID = DFServiceHelper.searchService(DealerAgent.this, "PokerEnvironment", "Environment");

	    registerDealTransaction(aclMsg.getConversationId());
	    SequentialBehaviour globalTransactionBehaviour = new SequentialBehaviour();

	    Round handStep = request.getHandStep();
	    if(handStep == Round.PLAYER_CARDS_DEAL)
	    {
		// we reset the game deck before dealing players cards.
		cardDeck = CardDeck.getNewRegularGameDeck();

		// we start dealing, starting from the first blind
		PlayerCircularIterator it = playersContainer.getCircularIterator(playersContainer.getSmallBlind());

		while(it.getLoopNumber() < 3){
		    final Player curPlayer = it.next();
		    DealCardToPlayerRequest dealRequest = new DealCardToPlayerRequest(curPlayer.getAID(), cardDeck.pickCard());
		    TransactionBhv dealSingleCardBhv = new TransactionBhv(DealerAgent.this, dealRequest, EnvAID);
		    dealSingleCardBhv.setResponseVisitor(new MessageVisitor(){
			Player player = curPlayer;
			@Override
			public boolean onOKMessage(OKMessage okMessage, ACLMessage aclMsg) {
			    System.out.println("[" + DealerAgent.this.getLocalName() + "] Card given to player " + player.getNickname() + ".");
			    return true;
			}
			@Override
			public boolean onFailureMessage(FailureMessage msg, ACLMessage aclMsg) {
			    addErrorForDealTransaction(aclMsg.getConversationId(), msg.getMessage());
			    return true;
			}
		    });
		    globalTransactionBehaviour.addSubBehaviour(dealSingleCardBhv);
		}
	    }
	    else if(handStep == Round.FLOP || handStep == Round.TURN || handStep == Round.RIVER)
	    {
		int nbDealtCards = 1;
		if(handStep == Round.FLOP){
		    nbDealtCards = 3;
		}

		for(int i = 0; i < nbDealtCards; i++){
		    AddCommunityCardRequest addCardRequest = new AddCommunityCardRequest(cardDeck.pickCard());

		    TransactionBhv dealSingleCardBhv = new TransactionBhv(DealerAgent.this, addCardRequest, EnvAID);
		    dealSingleCardBhv.setResponseVisitor(new MessageVisitor(){
			@Override
			public boolean onOKMessage(OKMessage okMessage, ACLMessage aclMsg) {
			    System.out.println("[" + DealerAgent.this.getLocalName() + "] Card added to community set.");
			    return true;
			}
			@Override
			public boolean onFailureMessage(FailureMessage msg, ACLMessage aclMsg) {
			    addErrorForDealTransaction(aclMsg.getConversationId(), msg.getMessage());
			    return true;
			}
		    });

		    globalTransactionBehaviour.addSubBehaviour(dealSingleCardBhv);
		}
	    }
	    else 
	    {
		AgentHelper.sendReply(DealerAgent.this, aclMsg, ACLMessage.INFORM, new OKMessage()); // No card dealt at that step
		return true;
	    }

	    globalTransactionBehaviour.addSubBehaviour(new ConcludeDealPlayersCardsTransactionBehaviour(aclMsg));
	    addBehaviour(globalTransactionBehaviour);

	    return true;	
	}
    }	
}
