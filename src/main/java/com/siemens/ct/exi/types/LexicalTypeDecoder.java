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

package com.siemens.ct.exi.types;

import java.io.IOException;
import java.util.Map;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.datatype.RestrictedCharacterSetDatatype;
import com.siemens.ct.exi.datatype.charset.XSDBase64CharacterSet;
import com.siemens.ct.exi.datatype.charset.XSDBooleanCharacterSet;
import com.siemens.ct.exi.datatype.charset.XSDDateTimeCharacterSet;
import com.siemens.ct.exi.datatype.charset.XSDDecimalCharacterSet;
import com.siemens.ct.exi.datatype.charset.XSDDoubleCharacterSet;
import com.siemens.ct.exi.datatype.charset.XSDHexBinaryCharacterSet;
import com.siemens.ct.exi.datatype.charset.XSDIntegerCharacterSet;
import com.siemens.ct.exi.datatype.charset.XSDStringCharacterSet;
import com.siemens.ct.exi.datatype.strings.StringDecoder;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.values.Value;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.7-SNAPSHOT
 */

public class LexicalTypeDecoder extends AbstractTypeDecoder {

	protected RestrictedCharacterSetDatatype rcsBase64Binary = new RestrictedCharacterSetDatatype(
			new XSDBase64CharacterSet(), null);
	protected RestrictedCharacterSetDatatype rcsHexBinary = new RestrictedCharacterSetDatatype(
			new XSDHexBinaryCharacterSet(), null);
	protected RestrictedCharacterSetDatatype rcsBoolean = new RestrictedCharacterSetDatatype(
			new XSDBooleanCharacterSet(), null);
	protected RestrictedCharacterSetDatatype rcsDateTime = new RestrictedCharacterSetDatatype(
			new XSDDateTimeCharacterSet(), null);
	protected RestrictedCharacterSetDatatype rcsDecimal = new RestrictedCharacterSetDatatype(
			new XSDDecimalCharacterSet(), null);
	protected RestrictedCharacterSetDatatype rcsDouble = new RestrictedCharacterSetDatatype(
			new XSDDoubleCharacterSet(), null);
	protected RestrictedCharacterSetDatatype rcsInteger = new RestrictedCharacterSetDatatype(
			new XSDIntegerCharacterSet(), null);
	protected RestrictedCharacterSetDatatype rcsString = new RestrictedCharacterSetDatatype(
			new XSDStringCharacterSet(), null);

	public LexicalTypeDecoder() throws EXIException {
		this(null, null, null);
	}

	public LexicalTypeDecoder(QName[] dtrMapTypes, QName[] dtrMapRepresentations, Map<QName, Datatype> dtrMapRepresentationsDatatype)
			throws EXIException {
		super(dtrMapTypes, dtrMapRepresentations, dtrMapRepresentationsDatatype);
	}

	public Value readValue(Datatype datatype, QNameContext qnContext,
			DecoderChannel valueChannel, StringDecoder stringDecoder)
			throws IOException {
		if (this.dtrMapInUse) {
			datatype = this.getDtrDatatype(datatype);
		}

		Value val;
		switch (datatype.getDatatypeID()) {
		case exi_base64Binary:
			val = rcsBase64Binary.readValue(qnContext, valueChannel,
					stringDecoder);
			break;
		case exi_hexBinary:
			val = rcsHexBinary
					.readValue(qnContext, valueChannel, stringDecoder);
			break;
		case exi_boolean:
			val = rcsBoolean.readValue(qnContext, valueChannel, stringDecoder);
			break;
		case exi_dateTime:
		case exi_time:
		case exi_date:
		case exi_gYearMonth:
		case exi_gYear:
		case exi_gMonthDay:
		case exi_gDay:
		case exi_gMonth:
			val = rcsDateTime.readValue(qnContext, valueChannel, stringDecoder);
			break;
		case exi_decimal:
			val = rcsDecimal.readValue(qnContext, valueChannel, stringDecoder);
			break;
		case exi_double:
			val = rcsDouble.readValue(qnContext, valueChannel, stringDecoder);
			break;
		case exi_integer:
			val = rcsInteger.readValue(qnContext, valueChannel, stringDecoder);
			break;
		case exi_string:
			// exi:string no restricted character set
			val = stringDecoder.readValue(qnContext, valueChannel);
			// val = rcsString.readValue(qnContext, valueChannel,
			// stringDecoder);
			break;
		default:
			throw new UnsupportedOperationException();
		}

		return val;
	}
}
