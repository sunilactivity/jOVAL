// Copyright (C) 2012 jOVAL.org.  All rights reserved.
// This software is licensed under the AGPL 3.0 license available at http://www.joval.org/agpl_v3.txt

package org.joval.intf.scap.xccdf;

import java.util.List;
import java.util.NoSuchElementException;

import org.joval.intf.scap.IScapContext;
import org.joval.intf.scap.arf.IReport;
import org.joval.intf.scap.ocil.IChecklist;
import org.joval.intf.scap.ocil.IVariables;
import org.joval.intf.scap.oval.IResults;
import org.joval.intf.scap.sce.IScriptResult;
import org.joval.intf.scap.xccdf.SystemEnumeration;
import org.joval.intf.util.IProducer;
import org.joval.scap.ScapException;
import org.joval.scap.arf.ArfException;
import org.joval.util.Version;

/**
 * Engine that evaluates a host against an XCCDF benchmark.
 *
 * @author David A. Solin
 * @version %I% %G%
 */
public interface IXccdfEngine extends Runnable {
    /**
     * The version of the SCAP schema supported by the engine.
     */
    Version SCHEMA_VERSION = new Version("1.2");

    enum Message {
	/**
	 * Message indicating that the engine has begun probing for platform applicability.
	 */
	PLATFORM_PHASE_START,

	/**
	 * Message indicating that the engine is probing for platform applicability. Argument is the String CPE ID of
	 * the platform that is about to be tested.
	 */
	PLATFORM_CPE,

	/**
	 * Message indicating that the engine has finished probing for object items. Argument is Boolean.TRUE if the
	 * target is applicable, or Boolean.FALSE if not.
	 */
	PLATFORM_PHASE_END,

	/**
	 * Message indicating that the engine is beginning to evaluate selected XCCDF rules.
	 */
	RULES_PHASE_START,

	/**
	 * Message indicating that the engine has finished evaluating selected XCCDF rules.
	 */
	RULES_PHASE_END,

	/**
	 * Message indicating that the engine has created an OVAL engine instance and is about to run it. The argument
	 * is the OVAL IOvalEngine instance.
	 *
	 * @see org.joval.intf.scap.oval.IOvalEngine
	 */
	OVAL_ENGINE,

	/**
	 * Message indicating that the engine is missing information about an OCIL checklist result. The argument is
	 * an OcilMessageArgument.
	 *
	 * @see org.joval.intf.scap.ocil.IChecklist
	 */
	OCIL_MISSING,

	/**
	 * Message indicating that the engine is about to run an SCE script. The argument is the script href (String).
	 */
	SCE_SCRIPT;
    }

    /**
     * Specification for the argument accompanying a MESSAGE_OCIL notification message.
     */
    interface OcilMessageArgument {
	/**
	 * Get the href pertaining to this argument.
	 */
	String getHref();

	/**
	 * Get the OCIL checklist corresponding to the href.
	 */
	IChecklist getChecklist();

	/**
	 * Get the IDs of the questionnaires in the checklist for which results are required. Results are unique, but
         * listed in the order in which they are first referenced by the XCCDF document.
	 */
	List<String> getQuestionnaireIds();

	/**
	 * Get the variable value exports for the checklist.
	 */
	IVariables getVariables();
    }

    enum Result {
	/**
	 * Specifies a nominal result
	 */
	OK,

	/**
	 * Specifies that an exception has been raised
	 */
	ERR;
    }

    /**
     * Stop the engine's processing and close all open resources.  This will leave the engine in an error state.
     */
    void destroy();

    /**
     * Set the SCAP context (e.g., the selected profile of a benchmark in a datastream collection) that will be processed
     * by the engine.
     *
     * @throws IllegalStateException if the engine has already started.
     */
    void setContext(IScapContext ctx) throws IllegalStateException, ScapException;

    /**
     * Assert the applicability of a platform to the engine. This will prevent auto-discovery of the referenced platform.
     */
    void assertPlatform(String idref, boolean applicable) throws IllegalStateException;

    /**
     * Add a pre-existing OCIL checklist (containing result information) to the engine as input.
     *
     * @throws IllegalStateException if the engine has already started.
     */
    void addChecklist(String href, IChecklist checklist) throws IllegalStateException;

    /**
     * Add a pre-existing OVAL result to the engine as input.
     *
     * @throws IllegalStateException if the engine has already started.
     */
    void addOvalResult(String href, IResults results) throws IllegalStateException;

    /**
     * Add a pre-existing SCE result to the engine as input.
     *
     * @throws IllegalStateException if the engine has already started.
     */
    void addScriptResult(String href, IScriptResult result) throws IllegalStateException;

    /**
     * Get an IProducer associated with the IXccdfEngine.  This IProducer can be observed for MESSAGE_ notifications while
     * the engine is running.
     */
    IProducer<Message> getNotificationProducer();

    /**
     * Returns Result.OK or Result.ERR
     *
     * @throws IllegalThreadStateException if the engine hasn't run, or is running.
     */
    Result getResult() throws IllegalThreadStateException;

    /**
     * Generate an ARF report. Only valid after the run() method has finished (if getResult returned Result.OK).
     * The XCCDF report is always included (even when called without arguments). Additional subreports can be
     * specified by check system using the SystemEnumeration.
     *
     * @throws IllegalThreadStateException if the engine hasn't run, or is running.
     */
    IReport getReport(SystemEnumeration... systems) throws IllegalThreadStateException, ArfException;

    /**
     * Return the error (valid if getResult returned Result.ERR).  Only valid after the run() method has finished.
     *
     * @throws IllegalThreadStateException if the engine hasn't run, or is running.
     */
    Exception getError() throws IllegalThreadStateException;
}
