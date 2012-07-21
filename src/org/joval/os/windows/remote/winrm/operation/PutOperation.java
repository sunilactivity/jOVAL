// Copyright (C) 2012 jOVAL.org.  All rights reserved.
// This software is licensed under the AGPL 3.0 license available at http://www.joval.org/agpl_v3.txt

package org.joval.os.windows.remote.winrm.operation;

import java.util.Collections;
import java.util.List;

import org.joval.os.windows.remote.winrm.IMessage;
import org.joval.os.windows.remote.winrm.IOperation;
import org.joval.os.windows.remote.winrm.message.AnyXmlMessage;
import org.joval.os.windows.remote.winrm.message.OptionalXmlMessage;

public class PutOperation implements IOperation<OptionalXmlMessage> {
    private AnyXmlMessage input;
    private OptionalXmlMessage output;

    public PutOperation(AnyXmlMessage input) {
	this.input = input;
    }

    // Implement IOperation

    public IMessage getInput() {
	return input;
    }

    public void setOutput(OptionalXmlMessage output) {
	this.output = output;
    }

    public OptionalXmlMessage getOutput() {
	return output;
    }

    public String getSOAPAction() {
	return "http://schemas.xmlsoap.org/ws/2004/09/transfer/Put";
    }

    public List<Object> getHeaders() {
	@SuppressWarnings("unchecked")
	List<Object> empty = (List<Object>)Collections.EMPTY_LIST;
	return empty;
    }
}