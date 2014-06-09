package sma.message.blind.notification;

import jade.lang.acl.ACLMessage;
import sma.message.Message;
import sma.message.MessageVisitor;

public class TimeBeforeIncreasingBlindChangedNotification extends Message {
	
	private int time;

	public TimeBeforeIncreasingBlindChangedNotification() {
		
	}
	
	public TimeBeforeIncreasingBlindChangedNotification(int time) {
		this.time = time;
	}
	
	@Override
	public boolean accept(MessageVisitor visitor, ACLMessage aclMsg) {
		return visitor.onTimeBeforeIncreasingBlindChangedNotification(this, aclMsg);
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}
}
