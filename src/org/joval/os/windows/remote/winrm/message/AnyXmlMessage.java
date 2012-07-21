// Copyright (C) 2012 jOVAL.org.  All rights reserved.
// This software is licensed under the AGPL 3.0 license available at http://www.joval.org/agpl_v3.txt

package org.joval.os.windows.remote.winrm.message;

import org.xmlsoap.ws.transfer.AnyXmlType;

import org.joval.os.windows.remote.winrm.IMessage;

public class AnyXmlMessage implements IMessage<Object> {
    private AnyXmlType body;

    public AnyXmlMessage(AnyXmlType body) {
	this.body = body;
    }

    // Implement IMessage

    public Object getBody() {
	return body.getAny();
    }
}