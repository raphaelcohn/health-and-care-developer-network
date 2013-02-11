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

package uk.nhs.hdn.common.parsers.fixedWidth.fixedWidthFields;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.parsers.CouldNotParseException;

import java.io.BufferedReader;
import java.io.IOException;

public final class RightPaddedFixedWidthField implements FixedWidthField
{
	private final int padding;
	private final int width;

	public RightPaddedFixedWidthField(final char padding, final int width)
	{
		this.padding = (int) padding;
		this.width = width;
	}

	@NotNull
	@Override
	public String parse(@NotNull final BufferedReader bufferedReader) throws IOException, CouldNotParseException
	{
		final char[] paddedCharacters = new char[width];
		int toRead = width;
		do
		{
			final int read = bufferedReader.read(paddedCharacters);
			if (read == -1)
			{
				throw new CouldNotParseException(0, "End of input within right padded fixed width field");
			}
			toRead -= read;
		} while(toRead != 0);

		for(int characterIndex = width - 1; characterIndex != -1; characterIndex--)
		{
			if (paddedCharacters[characterIndex] == padding)
			{
				continue;
			}
			return new String(paddedCharacters, 0, characterIndex + 1);
		}
		return "";
	}
}
