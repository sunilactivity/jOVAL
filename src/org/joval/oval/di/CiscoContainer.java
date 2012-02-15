// Copyright (C) 2012 jOVAL.org.  All rights reserved.
// This software is licensed under the AGPL 3.0 license available at http://www.joval.org/agpl_v3.txt

package org.joval.oval.di;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Properties;
import java.util.PropertyResourceBundle;

import org.joval.intf.cisco.system.ITechSupport;
import org.joval.os.cisco.system.TechSupport;
import org.joval.os.juniper.system.SupportInformation;
import org.joval.intf.plugin.IPlugin;
import org.joval.plugin.CiscoPlugin;
import org.joval.util.JOVALMsg;
import org.joval.util.JOVALSystem;

/**
 * Jovaldi continer for the CiscoPlugin.
 *
 * @author David A. Solin
 * @version %I% %G%
 */
public class CiscoContainer implements IPluginContainer {
    private static PropertyResourceBundle resources;
    static {
	try {
	    ClassLoader cl = CiscoContainer.class.getClassLoader();
	    Locale locale = Locale.getDefault();
	    URL url = cl.getResource("plugin.resources_" + locale.toString() + ".properties");
	    if (url == null) {
		url = cl.getResource("plugin.resources_" + locale.getLanguage() + ".properties");
	    }
	    if (url == null) {
		url = cl.getResource("plugin.resources.properties");
	    }
	    resources = new PropertyResourceBundle(url.openStream());
	} catch (IOException e) {
	    JOVALSystem.getLogger().warn(JOVALSystem.getMessage(JOVALMsg.ERROR_EXCEPTION), e);
	}
    }

    /**
     * Retrieve a message using its key.
     */
    static String getMessage(String key, Object... arguments) {
	return MessageFormat.format(resources.getString(key), arguments);
    }

    private IPlugin plugin;

    public CiscoContainer() {
    }

    // Implement IPluginContainer

    public void setDataDirectory(File dir) {}

    public void configure(Properties props) throws Exception {
	if (props == null) {
	    throw new Exception(getMessage("err.configMissing", DEFAULT_FILE));
	}
	String str = props.getProperty("tech.url");
	if (str == null) {
	    throw new Exception(getMessage("err.configPropMissing", "tech.url"));
	}
	URL url = CiscoPlugin.toURL(props.getProperty("tech.url"));
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	InputStream in = url.openStream();
	byte[] buff = new byte[1024];
	int len = 0;
	while((len = in.read(buff)) > 0) {
	    out.write(buff, 0, len);
	}

	ITechSupport tech = new TechSupport(new ByteArrayInputStream(out.toByteArray()));
	if (tech.getHeadings().size() == 0) {
	    tech = new SupportInformation(new ByteArrayInputStream(out.toByteArray()));
	}

	plugin = new CiscoPlugin(tech);
    }

    public String getProperty(String key) {
	return resources.getString(key);
    }

    public IPlugin getPlugin() {
	return plugin;
    }
}