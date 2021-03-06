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

package com.siemens.ct.exi.datatype.strings;

import java.io.IOException;
import java.util.List;

import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.datatype.EnumDatatype;
import com.siemens.ct.exi.datatype.strings.StringEncoderImpl.ValueContainer;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.util.MethodsBag;
import com.siemens.ct.exi.values.StringValue;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.7-SNAPSHOT
 */

public class ExtendedStringEncoderImpl implements StringEncoder {

	final StringEncoderImpl stringEncoder;
	
	EnumDatatype grammarStrings;
	
	public ExtendedStringEncoderImpl(StringEncoderImpl stringEncoder) {
		this.stringEncoder = (StringEncoderImpl) stringEncoder;
	}
	
	public void setGrammarStrings(EnumDatatype grammarStrings) {
		this.grammarStrings = grammarStrings;
	}
	
	
	public void writeValue(QNameContext context,
			EncoderChannel valueChannel, String value) throws IOException {

		ValueContainer vc = stringEncoder.stringValues.get(value);

		if (vc != null) {
			// hit
			if (stringEncoder.localValuePartitions && context.equals(vc.context)) {
				/*
				 * local value hit ==> is represented as zero (0) encoded as an
				 * Unsigned Integer followed by the compact identifier of the
				 * string value in the "local" value partition
				 */
				valueChannel.encodeUnsignedInteger(0);
				int numberBitsLocal = MethodsBag.getCodingLength(getNumberOfStringValues(context));
				valueChannel.encodeNBitUnsignedInteger(vc.localValueID, numberBitsLocal);
			} else {
				/*
				 * global value hit ==> value is represented as one (1) encoded
				 * as an Unsigned Integer followed by the compact identifier of
				 * the String value in the global value partition.
				 */
				valueChannel.encodeUnsignedInteger(1);
				// global value size
				
				int numberBitsGlobal = MethodsBag.getCodingLength(stringEncoder.stringValues.size());
				valueChannel.encodeNBitUnsignedInteger(vc.globalValueID, numberBitsGlobal);
			}
		} else {
			/*
			 * miss [not found in local nor in global value partition] ==>
			 * string literal is encoded as a String with the length incremented
			 * by 6.
			 */
			
			// --> check grammar strings
			if(this.grammarStrings != null && this.grammarStrings.isValid(new StringValue(value))) {
				valueChannel.encodeUnsignedInteger(2); // grammar string
				
				this.grammarStrings.writeValue(context, valueChannel, stringEncoder);
			} else {
				// TODO (3)shared string, (4)split string, (5)undefined
				
				final int L = value.codePointCount(0, value.length());
				valueChannel.encodeUnsignedInteger(L + 6);
				/*
				 * If length L is greater than zero the string S is added
				 */
				if (L > 0) {
					valueChannel.encodeStringOnly(value);
					// After encoding the string value, it is added to both the
					// associated "local" value string table partition and the
					// global value string table partition.
					addValue(context, value);
				}
			}
		}

	}

	// Restricted char set
	public boolean isStringHit(String value) throws IOException {
		return this.stringEncoder.isStringHit(value);
	}
	

	public void addValue(QNameContext qnc, String value) {
		this.stringEncoder.addValue(qnc, value);

	}

	public void clear() {
		this.stringEncoder.clear();
	}
	
	public void setSharedStrings(List<String> sharedStrings) {
		this.stringEncoder.setSharedStrings(sharedStrings);
	}

	@Override
	public int getNumberOfStringValues(QNameContext qnc) {
		return this.stringEncoder.getNumberOfStringValues(qnc);
	}

}
