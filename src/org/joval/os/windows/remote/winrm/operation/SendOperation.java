// Copyright (C) 2012 jOVAL.org.  All rights reserved.
// This software is licensed under the AGPL 3.0 license available at http://www.joval.org/agpl_v3.txt

package org.joval.os.windows.remote.winrm.operation;

import java.util.Collections;
import java.util.List;

import org.joval.os.windows.remote.winrm.IMessage;
import org.joval.os.windows.remote.winrm.IOperation;
import org.joval.os.windows.remote.winrm.message.SendMessage;
import org.joval.os.windows.remote.winrm.message.SendResponseMessage;

public class SendOperation implements IOperation<SendResponseMessage> {
    private SendMessage input;
    private SendResponseMessage output;

    public SendOperation(SendMessage input) {
	this.input = input;
    }

    // Implement IOperation

    public IMessage getInput() {
	return input;
    }

    public void setOutput(SendResponseMessage output) {
	this.output = output;
    }

    public SendResponseMessage getOutput() {
	return output;
    }

    publis String getSOAPAction() {
	return "http://schemas.microsoft.com/wbem/wsman/1/windows/shell/Send";
    }

    public List<Object> getHeaders() {
	@SuppressWarnings("unchecked")
	List<Object> empty = (List<Object>)Collections.EMPTY_LIST;
	return empty;
    }
}