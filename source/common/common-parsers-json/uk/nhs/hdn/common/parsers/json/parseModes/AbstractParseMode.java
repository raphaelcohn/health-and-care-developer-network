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

package uk.nhs.hdn.common.parsers.json.parseModes;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.parsers.json.InvalidJsonException;
import uk.nhs.hdn.common.parsers.charaterSets.CharacterSet;
import uk.nhs.hdn.common.parsers.convenientReaders.EndOfFileException;
import uk.nhs.hdn.common.parsers.convenientReaders.PeekingConvenientReader;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;

public abstract class AbstractParseMode extends AbstractToString implements ParseMode
{
	protected final boolean isNotRootValue;

	@NotNull
	private final CharacterSet validTerminatingCharacters;

	protected AbstractParseMode(final boolean isRootValue, @NotNull final CharacterSet validTerminatingCharacters)
	{
		isNotRootValue = !isRootValue;
		this.validTerminatingCharacters = validTerminatingCharacters;
	}

	protected final void guardNextCharacter(@NotNull final PeekingConvenientReader peekingConvenientReader) throws InvalidJsonException
	{
		final char nextCharacter;
		try
		{
			nextCharacter = peekNextCharacter(peekingConvenientReader);
		}
		catch (EndOfFileException ignored)
		{
			return;
		}
		guardIsTerminatingCharacter(nextCharacter);
	}

	protected final char peekNextCharacter(@NotNull final PeekingConvenientReader peekingConvenientReader) throws InvalidJsonException, EndOfFileException
	{
		try
		{
			return peekingConvenientReader.peekCharacter();
		}
		catch (EndOfFileException e)
		{
			if (isNotRootValue)
			{
				throw new InvalidJsonException(e);
			}
			throw e;
		}
	}

	protected final void guardIsTerminatingCharacter(final char nextCharacter) throws InvalidJsonException
	{
		if (isNotTerminatingCharacter(nextCharacter))
		{
			throw new InvalidJsonException("literal value not correctly terminated");
		}
	}

	protected final boolean isNotTerminatingCharacter(final char nextCharacter)
	{
		return !isTerminatingCharacter(nextCharacter);
	}

	protected final boolean isTerminatingCharacter(final char nextCharacter)
	{
		return validTerminatingCharacters.contains(nextCharacter);
	}

	public static char readRequiredCharacter(@NotNull final PeekingConvenientReader peekingConvenientReader) throws InvalidJsonException
	{
		try
		{
			return peekingConvenientReader.readCharacter();
		}
		catch (EndOfFileException e)
		{
			throw new InvalidJsonException(e);
		}
	}
}
