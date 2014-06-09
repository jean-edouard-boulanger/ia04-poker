package sma.message;

import jade.core.AID;

import java.util.ArrayList;
import java.util.HashMap;

import sma.message.environment.request.AddPlayerTableRequest;

public class NotificationSubscriber {

	private HashMap<SubscribableNotifications, ArrayList<AID>> subscriptions;
	
	public NotificationSubscriber(){}
	
	public NotificationSubscriber addSubscriptionList(SubscribableNotifications subscriptionListName){
		this.subscriptions.put(subscriptionListName, new ArrayList<AID>());
		return this;
	}
	
	public NotificationSubscriber dropSubscriptionList(SubscribableNotifications subscriptionListName){
		this.subscriptions.remove(subscriptionListName);
		return this;
	}
	
	public boolean hasSubscriptionList(SubscribableNotifications subscriptionListName){
		return this.subscriptions.containsKey(subscriptionListName);
	}
	
	public NotificationSubscriber addSubscriberToList(SubscribableNotifications subscriptionListName, AID subscriberAID){
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

	public NotificationSubscriber dropSubscriberFromList(SubscribableNotifications subscriptionListName, AID subscriberAID){
		if(this.hasSubscriptionList(subscriptionListName)){
			this.subscriptions.get(subscriptionListName).remove(subscriberAID);
		}
		return this;
	}
	
	public ArrayList<AID> getListSubscribers(SubscribableNotifications subscriptionListName){
		if(!this.hasSubscriptionList(subscriptionListName)){
			return new ArrayList<AID>();
		}
		return this.subscriptions.get(subscriptionListName);
	}
}
