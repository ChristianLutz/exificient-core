/*
 * Copyright (c) 2007-2016 Siemens AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

package com.siemens.ct.exi;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.core.container.DocType;
import com.siemens.ct.exi.core.container.NamespaceDeclaration;
import com.siemens.ct.exi.core.container.ProcessingInstruction;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammars.event.EventType;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.values.Value;

/**
 * Internal EXI Decoder interface to transform an EXI stream back to XML Infoset
 * entities.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.7-SNAPSHOT
 */

public interface EXIBodyDecoder {

	/**
	 * Sets the input stream and resets all internal states
	 * 
	 * @see #updateInputStream(InputStream)
	 * @param is input stream
	 * @throws EXIException EXI exception 
	 * @throws IOException IO exception
	 */
	public void setInputStream(InputStream is) throws EXIException, IOException;

	/**
	 * Sets input channel and resets all internal states
	 * 
	 * @see #updateInputChannel(DecoderChannel)
	 * @param channel decoder channel 
	 * @throws EXIException EXI exception 
	 * @throws IOException IO exception
	 */
	public void setInputChannel(DecoderChannel channel) throws EXIException,
			IOException;
	
	/**
	 * Updates input stream and does not reset internal states.
	 * 
	 * @see #setInputStream(InputStream)
	 * @param is input stream
	 * @throws EXIException EXI exception 
	 * @throws IOException IO exception
	 */
	public void updateInputStream(InputStream is) throws EXIException, IOException;
	
	/**
	 * Updates input channel and and does not reset internal states.
	 * 
	 * @see #setInputChannel(DecoderChannel)
	 * @param channel decoder channel
	 * @throws EXIException EXI exception 
	 * @throws IOException IO exception
	 */
	public void updateInputChannel(DecoderChannel channel) throws EXIException,
			IOException;
	

	/**
	 * Reports the next available EXI event-type or <code>null</code> if no more
	 * EXI event is available.
	 * 
	 * @return <code>EventType</code> for next EXI event
	 * @throws EXIException EXI exception
	 * @throws IOException IO exception
	 */
	public EventType next() throws EXIException, IOException;

	/**
	 * Indicates the beginning of a set of XML events
	 * 
	 * @throws EXIException EXI exception
	 * @throws IOException IO exception
	 */
	public void decodeStartDocument() throws EXIException, IOException;

	/**
	 * Indicates the end of a set of XML events
	 * 
	 * @throws EXIException EXI exception
	 * @throws IOException IO exception
	 */
	public void decodeEndDocument() throws EXIException, IOException;

	/**
	 * Reads EXI start element and returns qualified name.
	 * 
	 * <p>
	 * Start element appearing as expected event.
	 * </p>
	 * 
	 * @return <code>QNameContext</code> for qualified name
	 * 
	 * @throws EXIException EXI exception
	 * @throws IOException IO exception
	 */
	public QNameContext decodeStartElement() throws EXIException, IOException;

	/**
	 * Returns element prefix for current element as String.
	 * 
	 * <p>
	 * Note: IF Preserve.Prefixes set to TRUE ONLY callable after all NS events
	 * </p>
	 * 
	 * @return <code>String</code> for prefix
	 */
	public String getElementPrefix();

	/**
	 * Returns qualified name for element name as String
	 * 
	 * <p>
	 * QName ::= PrefixedName | UnprefixedName <br>
	 * PrefixedName ::= Prefix ':' LocalPart <br>
	 * UnprefixedName ::= LocalPart
	 * </p>
	 * 
	 * @return <code>String</code> for qname
	 */
	public String getElementQNameAsString();

	/**
	 * Reads EXI a self-contained start element.
	 * 
	 * @throws EXIException EXI exception
	 * @throws IOException IO exception
	 */
	public void decodeStartSelfContainedFragment() throws EXIException,
			IOException;

	/**
	 * Reads EXI end element and returns qualified name.
	 * 
	 * @return <code>QNameContext</code> for qualified name
	 * 
	 * @throws EXIException EXI exception
	 * @throws IOException IO exception
	 */
	public QNameContext decodeEndElement() throws EXIException, IOException;

	/**
	 * Parses xsi:nil attribute
	 * 
	 * @return xsi:nil qname
	 * 
	 * @throws EXIException EXI exception
	 * @throws IOException IO exception
	 */
	public QNameContext decodeAttributeXsiNil() throws EXIException, IOException;

	/**
	 * Parses xsi:type attribute
	 * 
	 * @return <code>QNameContext</code> for qualified name
	 * 
	 * @throws EXIException EXI exception
	 * @throws IOException IO exception
	 */
	public QNameContext decodeAttributeXsiType() throws EXIException, IOException;

	/**
	 * Parses attribute and returns qualified name.
	 * 
	 * @return <code>QNameContext</code> for qname
	 * 
	 * @throws EXIException EXI exception
	 * @throws IOException IO exception
	 */
	public QNameContext decodeAttribute() throws EXIException, IOException;

	/**
	 * Returns attribute prefix for (last) attribute as String
	 * 
	 * @return <code>String</code> for prefix
	 */
	public String getAttributePrefix();

	/**
	 * Returns qualified name for (last) attribute as String
	 * 
	 * <p>
	 * QName ::= PrefixedName | UnprefixedName <br>
	 * PrefixedName ::= Prefix ':' LocalPart <br>
	 * UnprefixedName ::= LocalPart
	 * </p>
	 * 
	 * @return <code>String</code> for qname
	 */
	public String getAttributeQNameAsString();

	/**
	 * Provides attribute value
	 * 
	 * @return <code>Value</code> for attribute value
	 */
	public Value getAttributeValue();

	/**
	 * Parses namespace declaration retrieving associated URI and prefix.
	 * 
	 * @return <code>NamespaceDeclaration</code> ns declaration
	 * 
	 * @throws EXIException EXI exception
	 * @throws IOException IO exception
	 */
	public NamespaceDeclaration decodeNamespaceDeclaration()
			throws EXIException, IOException;

	/**
	 * Prefix declarations for current context (element)
	 * 
	 * @return list or null if no mappings are available
	 */
	public List<NamespaceDeclaration> getDeclaredPrefixDeclarations();

	/**
	 * Decodes characters and reports them.
	 * 
	 * @return <code>Value</code> for XML characters item
	 * 
	 * @throws EXIException EXI exception
	 * @throws IOException IO exception
	 */
	public Value decodeCharacters() throws EXIException, IOException;

	/**
	 * Parses DOCTYPE with information items (name, publicID, systemID, text).
	 * 
	 * @return <code>DocType</code> for DOCTYPE information items
	 * 
	 * @throws EXIException EXI exception
	 * @throws IOException IO exception
	 */
	public DocType decodeDocType() throws EXIException, IOException;

	/**
	 * Parses EntityReference and returns ER name.
	 * 
	 * @return <code>String</code> for ER name
	 * 
	 * @throws EXIException EXI exception
	 * @throws IOException IO exception
	 */
	public char[] decodeEntityReference() throws EXIException, IOException;

	/**
	 * Parses comment with associated characters and provides comment text.
	 * 
	 * @return <code>String</code> for comment text
	 * 
	 * @throws EXIException EXI exception
	 * @throws IOException IO exception
	 */
	public char[] decodeComment() throws EXIException, IOException;

	/**
	 * Parses processing instruction with associated target and data.
	 * 
	 * @return <code>String</code> for PI target and data
	 * 
	 * @throws EXIException EXI exception
	 * @throws IOException IO exception
	 */
	public ProcessingInstruction decodeProcessingInstruction()
			throws EXIException, IOException;

}
