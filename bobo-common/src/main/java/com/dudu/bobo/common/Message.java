package com.dudu.bobo.common;

import java.io.Serializable;

/**
 *
 * @author liangy43
 *
 */
public class Message implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private long messageId;

    private int messageType;

    private Object messageBody;

    private transient Object attachment;

    public Message() {

    }

    public Message(Long id, Object body) {
        messageId = id;
        messageBody = body;
    }

    public Message(Long id, Object body, Object attachment) {
        messageId = id;
        messageBody = body;
        this.attachment = attachment;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    public Object getMessageBody() {
        return this.messageBody;
    }

    public void setMessageBody(Object messageBody) {
        this.messageBody = messageBody;
    }

    public Object getAttachment() {
        return this.attachment;
    }

    public void setAttachment(Object attachment) {
        this.attachment = attachment;
    }
}
