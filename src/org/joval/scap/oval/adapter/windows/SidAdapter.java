// Copyright (C) 2011 jOVAL.org.  All rights reserved.
// This software is licensed under the AGPL 3.0 license available at http://www.joval.org/agpl_v3.txt

package org.joval.scap.oval.adapter.windows;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import jsaf.identity.IdentityException;
import jsaf.intf.system.ISession;
import jsaf.intf.windows.identity.IDirectory;
import jsaf.intf.windows.identity.IPrincipal;
import jsaf.intf.windows.system.IWindowsSession;
import jsaf.util.StringTools;

import scap.oval.common.MessageType;
import scap.oval.common.MessageLevelEnumeration;
import scap.oval.common.OperationEnumeration;
import scap.oval.common.SimpleDatatypeEnumeration;
import scap.oval.definitions.core.ObjectType;
import scap.oval.definitions.windows.SidObject;
import scap.oval.systemcharacteristics.core.EntityItemStringType;
import scap.oval.systemcharacteristics.core.FlagEnumeration;
import scap.oval.systemcharacteristics.core.ItemType;
import scap.oval.systemcharacteristics.core.StatusEnumeration;
import scap.oval.systemcharacteristics.windows.SidItem;

import org.joval.intf.plugin.IAdapter;
import org.joval.scap.oval.CollectException;
import org.joval.scap.oval.Factories;
import org.joval.util.JOVALMsg;

/**
 * Collects items for the sid_sid_object.
 *
 * @author David A. Solin
 * @version %I% %G%
 */
public class SidAdapter implements IAdapter {
    private IWindowsSession session;
    private IDirectory directory;

    // Implement IAdapter

    @Override
    public Collection<Class> init(ISession session, Collection<Class> notapplicable) {
	Collection<Class> classes = new ArrayList<Class>();
	if (session instanceof IWindowsSession) {
	    this.session = (IWindowsSession)session;
	    classes.add(SidObject.class);
	} else {
	    notapplicable.add(SidObject.class);
	}
	return classes;
    }

    @Override
    public Collection<SidItem> getItems(ObjectType obj, IRequestContext rc) throws CollectException {
	directory = session.getDirectory();
	Collection<SidItem> items = new ArrayList<SidItem>();
	SidObject sObj = (SidObject)obj;
	OperationEnumeration op = sObj.getTrusteeName().getOperation();

	try {
	    switch(op) {
	      case EQUALS: {
		String netbiosName = directory.getQualifiedNetbiosName((String)sObj.getTrusteeName().getValue());
		items.add(makeItem(directory.queryPrincipal(netbiosName)));
		break;
	      }

	      case NOT_EQUAL: {
		String netbiosName = directory.getQualifiedNetbiosName((String)sObj.getTrusteeName().getValue());
		for (IPrincipal p : directory.queryAllPrincipals()) {
		    if (!p.getNetbiosName().equals(netbiosName)) {
			items.add(makeItem(p));
		    }
		}
		break;
	      }

	      case PATTERN_MATCH:
		try {
		    Pattern p = StringTools.pattern((String)sObj.getTrusteeName().getValue());
		    for (IPrincipal principal : directory.queryAllPrincipals()) {
			if (p.matcher(principal.getNetbiosName()).find()) {
			    items.add(makeItem(principal));
			}
		    }
		} catch (PatternSyntaxException e) {
		    MessageType msg = Factories.common.createMessageType();
		    msg.setLevel(MessageLevelEnumeration.ERROR);
		    msg.setValue(JOVALMsg.getMessage(JOVALMsg.ERROR_PATTERN, e.getMessage()));
		    rc.addMessage(msg);
		    session.getLogger().warn(JOVALMsg.getMessage(JOVALMsg.ERROR_EXCEPTION), e);
		}
		break;
    
	      default:
		String msg = JOVALMsg.getMessage(JOVALMsg.ERROR_UNSUPPORTED_OPERATION, op);
		throw new CollectException(msg, FlagEnumeration.NOT_COLLECTED);
	    }
	} catch (NoSuchElementException e) {
	    // No match.
	} catch (IdentityException e) {
	    MessageType msg = Factories.common.createMessageType();
	    msg.setLevel(MessageLevelEnumeration.ERROR);
	    msg.setValue(JOVALMsg.getMessage(JOVALMsg.ERROR_WIN_IDENTITY, obj.getId(), e.getMessage()));
	    rc.addMessage(msg);
	}
	return items;
    }

    // Private

    private SidItem makeItem(IPrincipal principal) {
	SidItem item = Factories.sc.windows.createSidItem();
	EntityItemStringType trusteeSidType = Factories.sc.core.createEntityItemStringType();
	trusteeSidType.setValue(principal.getSid());
	trusteeSidType.setDatatype(SimpleDatatypeEnumeration.STRING.value());
	item.setTrusteeSid(trusteeSidType);

	EntityItemStringType trusteeNameType = Factories.sc.core.createEntityItemStringType();
	if (principal.isBuiltin()) {
	    trusteeNameType.setValue(principal.getName());
	} else {
	    trusteeNameType.setValue(principal.getNetbiosName());
	}
	trusteeNameType.setDatatype(SimpleDatatypeEnumeration.STRING.value());
	item.setTrusteeName(trusteeNameType);

	EntityItemStringType trusteeDomainType = Factories.sc.core.createEntityItemStringType();
	trusteeDomainType.setValue(principal.getDomain());
	trusteeDomainType.setDatatype(SimpleDatatypeEnumeration.STRING.value());
	item.setTrusteeDomain(trusteeDomainType);
	return item;
    }
}
