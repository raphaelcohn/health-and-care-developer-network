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

package uk.nhs.hcdn.common.parsers.json.parseModes;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.parsers.json.InvalidJsonException;
import uk.nhs.hcdn.common.parsers.charaterSets.CharacterSet;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.JsonParseEventHandler;
import uk.nhs.hcdn.common.parsers.json.jsonReaders.JsonReader;

import static uk.nhs.hcdn.common.parsers.json.CharacterHelper.*;
import static uk.nhs.hcdn.common.parsers.json.parseModes.NumberParseMode.ArrayNumberParseModeInstance;
import static uk.nhs.hcdn.common.parsers.json.parseModes.ObjectParseMode.ArrayObjectParseModeInstance;
import static uk.nhs.hcdn.common.parsers.json.parseModes.ObjectParseMode.soakUpWhitespace;
import static uk.nhs.hcdn.common.parsers.json.parseModes.literalParseModes.BooleanLiteralParseMode.ArrayFalseBooleanConstantParseMode;
import static uk.nhs.hcdn.common.parsers.json.parseModes.literalParseModes.BooleanLiteralParseMode.ArrayTrueBooleanConstantParseMode;
import static uk.nhs.hcdn.common.parsers.json.parseModes.literalParseModes.NullLiteralParseMode.ArrayNullConstantParseMode;
import static uk.nhs.hcdn.common.parsers.json.parseModes.stringParseModes.ValueStringParseMode.ArrayValueStringParseModeInstance;

public final class ArrayParseMode extends AbstractParseMode
{
	@NotNull
	public static final ParseMode RootArrayParseModeInstance = new ArrayParseMode(true, Whitespace);

	@NotNull
	public static final ParseMode ObjectArrayParseModeInstance = new ArrayParseMode(false, WhitespaceCommaCloseObject);

	@NotNull
	public static final ParseMode ArrayArrayParseModeInstance = new ArrayParseMode(false, WhitespaceCommaCloseArray);

	public ArrayParseMode(final boolean isRootValue, @NotNull final CharacterSet validTerminatingCharacters)
	{
		super(isRootValue, validTerminatingCharacters);
	}

	@Override
	public void parse(@NotNull final JsonParseEventHandler jsonParseEventHandler, @NotNull final JsonReader jsonReader) throws InvalidJsonException
	{
		if (isNot(OpenArray, jsonReader.readRequiredCharacter()))
		{
			throw new InvalidJsonException("Does not start with open array");
		}

		jsonParseEventHandler.startArray();

		parseMembers(jsonParseEventHandler, jsonReader);

		jsonParseEventHandler.endArray();
	}

	@SuppressWarnings("FeatureEnvy")
	private static void parseMembers(final JsonParseEventHandler jsonParseEventHandler, final JsonReader jsonReader) throws InvalidJsonException
	{
		if (is(soakUpWhitespace(jsonReader), CloseArray))
		{
			return;
		}
		jsonReader.pushBackLastCharacter();

		do
		{
			readValue(jsonParseEventHandler, jsonReader);

			final char peekUpToCommaStart = soakUpWhitespace(jsonReader);

			if (is(peekUpToCommaStart, CloseArray))
			{
				return;
			}

			if (isNot(peekUpToCommaStart, Comma))
			{
				throw new InvalidJsonException("Expected a trailing comma in array");
			}
		} while(true);
	}

	private static void readValue(final JsonParseEventHandler jsonParseEventHandler, final JsonReader jsonReader) throws InvalidJsonException
	{
		final char peekUpToValueStart = soakUpWhitespace(jsonReader);

		jsonReader.pushBackLastCharacter();

		final ParseMode valueParseMode = valueParseMode(peekUpToValueStart);
		valueParseMode.parse(jsonParseEventHandler, jsonReader);
	}

	@SuppressWarnings("SwitchStatementWithTooManyBranches")
	@NotNull
	public static ParseMode valueParseMode(final char peekUpToValueStart) throws InvalidJsonException
	{
		switch (peekUpToValueStart)
		{
			case OpenObject:
				return ArrayObjectParseModeInstance;

			case OpenArray:
				return ArrayArrayParseModeInstance;

			case TrueLiteralStart:
				return ArrayTrueBooleanConstantParseMode;

			case FalseLiteralStart:
				return ArrayFalseBooleanConstantParseMode;

			case NullLiteralStart:
				return ArrayNullConstantParseMode;

			case QuotationMark:
				return ArrayValueStringParseModeInstance;

			case ZeroDigit:
			case OneDigit:
			case TwoDigit:
			case ThreeDigit:
			case FourDigit:
			case FiveDigit:
			case SixDigit:
			case SevenDigit:
			case EightDigit:
			case NineDigit:
				return ArrayNumberParseModeInstance;

			default:
				throw new InvalidJsonException("unexpected character in array");
		}
	}
}
