package com.dudu.bobo.common;

import java.io.Serializable;

public class Message implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	long messageId;
		
	Object messageBody;

	public Message(Long id, Object obj) {
		messageId = id;
		messageBody = obj;
	}

	public Long getMessageId() {
		return messageId;
	}
		
	public void setMessageId(Long messageId) {
		this.messageId = messageId;
	}
		
	public Object getMessageBody() {
		return this.messageBody;
	}
	
	public void setMessageBody(Object messageBody) {
		this.messageBody = messageBody;
	}
}
