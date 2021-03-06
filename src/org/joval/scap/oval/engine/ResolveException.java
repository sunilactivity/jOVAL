// Copyright (C) 2011 jOVAL.org.  All rights reserved.
// This software is licensed under the AGPL 3.0 license available at http://www.joval.org/agpl_v3.txt

package org.joval.scap.oval.engine;

/**
 * An exception class for variable resolution, used to propagate messages upstream to the originator so that an error
 * can be logged. Examples of cases where a ResolveException can be thrown include:
 * (1) A var_ref indicates a field that is not defined in a referenced item
 * (2) A substring component contains an out-of-bounds index
 * (3) A regex_capture pattern contains no groups
 *
 * @author David A. Solin
 * @version %I% %G%
 */
class ResolveException extends Exception {
    ResolveException(String message) {
	super(message);
    }

    ResolveException(Exception e) {
	super(e);
    }
}
