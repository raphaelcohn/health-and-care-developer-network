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

package uk.nhs.hcdn.common.parsers.json.parseModes.stringParseModes;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.parsers.charaterSets.CharacterSet;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.JsonParseEventHandler;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.schemaViolationInvalidJsonExceptions.SchemaViolationInvalidJsonException;
import uk.nhs.hcdn.common.parsers.json.parseModes.ParseMode;

import static uk.nhs.hcdn.common.parsers.json.CharacterHelper.*;

public final class ValueStringParseMode extends AbstractStringParseMode
{
	@NotNull
	public static final ParseMode RootValueStringParseModeInstance = new ValueStringParseMode(true, Whitespace);

	@NotNull
	public static final ParseMode ObjectValueStringParseModeInstance = new ValueStringParseMode(false, WhitespaceCommaCloseObject);

	@NotNull
	public static final ParseMode ArrayValueStringParseModeInstance = new ValueStringParseMode(false, WhitespaceCommaCloseArray);

	private ValueStringParseMode(final boolean isRootValue, @NotNull final CharacterSet validTerminatingCharacters)
	{
		super(isRootValue, validTerminatingCharacters);
	}

	@Override
	protected void useStringValue(@NotNull final JsonParseEventHandler jsonParseEventHandler, @NonNls @NotNull final String value) throws SchemaViolationInvalidJsonException
	{
		jsonParseEventHandler.stringValue(value);
	}
}
