/*
 * © Crown Copyright 2013
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.nhs.hdn.dts.domain.schema.xmlConstructors;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors.TextXmlConstructor;
import uk.nhs.hdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors.XmlConstructor;
import uk.nhs.hdn.common.xml.XmlSchemaViolationException;
import uk.nhs.hdn.dts.domain.statusRecords.dateTime.KnownDateTime;

import static uk.nhs.hdn.dts.domain.statusRecords.dateTime.KnownDateTime.parseDateTime;

public final class DateTimeTextXmlConstructor extends TextXmlConstructor<KnownDateTime>
{
	@NotNull
	public static final XmlConstructor<?, ?> DateTimeTextXmlConstructorInstance = new DateTimeTextXmlConstructor();

	private DateTimeTextXmlConstructor()
	{
		super(null, KnownDateTime.class);
	}

	@NotNull
	@Override
	protected KnownDateTime convert(@NotNull final String text) throws XmlSchemaViolationException
	{
		try
		{
			return parseDateTime(text);
		}
		catch (RuntimeException e)
		{
			throw new XmlSchemaViolationException(e);
		}
	}
}
