// Copyright (C) 2011 jOVAL.org.  All rights reserved.
// This software is licensed under the AGPL 3.0 license available at http://www.joval.org/agpl_v3.txt

package org.joval.intf.system;

import oval.schemas.common.FamilyEnumeration;
import oval.schemas.systemcharacteristics.core.SystemInfoType;

import org.joval.intf.io.IFilesystem;

/**
 * A representation of a session.
 *
 * @author David A. Solin
 * @version %I% %G%
 */
public interface ISession extends IBaseSession {
    public void setWorkingDir(String path);

    public IFilesystem getFilesystem();

    public IEnvironment getEnvironment();

    public SystemInfoType getSystemInfo();

    /**
     * Return the FamilyEnumeration member against which the host should be tested for FamilyTest applicability.
     */
    public FamilyEnumeration getFamily();
}
