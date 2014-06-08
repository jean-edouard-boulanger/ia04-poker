package sma.message;

import jade.core.AID;

import java.util.ArrayList;
import java.util.HashMap;

import sma.message.environment.request.AddPlayerTableRequest;

public class NotificationSubscriber {

	private HashMap<String, ArrayList<AID>> subscriptions;
	
	public NotificationSubscriber(){}
	
	public NotificationSubscriber addSubscriptionList(String subscriptionListName){
		this.subscriptions.put(subscriptionListName, new ArrayList<AID>());
		return this;
	}
	
	public NotificationSubscriber dropSubscriptionList(String subscriptionListName){
		this.subscriptions.remove(subscriptionListName);
		return this;
	}
	
	public boolean hasSubscriptionList(String subscriptionListName){
		return this.subscriptions.containsKey(subscriptionListName);
	}
	
	public NotificationSubscriber addSubscriberToList(String subscriptionListName, AID subscriberAID){
		if(!this.hasSubscriptionList(subscriptionListName)){
			ArrayList<AID> newSubscribersList = new ArrayList<AID>();
			newSubscribersList.add(subscriberAID);
			this.subscriptions.put(subscriptionListName, newSubscribersList);
		}
		else {
			this.subscriptions.get(subscriptionListName).add(subscriberAID);
		}
		return this;
	}

	public NotificationSubscriber dropSubscriberFromList(String subscriptionListName, AID subscriberAID){
		if(this.hasSubscriptionList(subscriptionListName)){
			this.subscriptions.get(subscriptionListName).remove(subscriberAID);
		}
		return this;
	}
	
	public ArrayList<AID> getListSubscribers(String subscriptionListName){
		if(!this.hasSubscriptionList(subscriptionListName)){
			return new ArrayList<AID>();
		}
		return this.subscriptions.get(subscriptionListName);
	}
}
