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

package uk.nhs.hdn.dbs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.serialisers.CouldNotSerialiseValueException;
import uk.nhs.hdn.common.serialisers.CouldNotWriteValueException;
import uk.nhs.hdn.common.serialisers.ValueSerialisable;
import uk.nhs.hdn.common.serialisers.ValueSerialiser;

public enum Gender implements ValueSerialisable
{
	NotKnown(0),
	Male(1),
	Female(2),
	NotSpecified(9), // indeterminable
	;

	private static final class CompilerWorkaround
	{
		private static final Gender[] Index = new Gender[10];
	}

	public final int value;

	Gender(final int value)
	{
		this.value = value;
		CompilerWorkaround.Index[value] = this;
	}

	@Override
	public void serialiseValue(@NotNull final ValueSerialiser valueSerialiser) throws CouldNotSerialiseValueException
	{
		try
		{
			valueSerialiser.writeValue(value);
		}
		catch (CouldNotWriteValueException e)
		{
			throw new CouldNotSerialiseValueException(this, e);
		}
	}

	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@Nullable
	public static Gender gender(final int value)
	{
		if (value < 0 || value > 9)
		{
			return null;
		}
		return CompilerWorkaround.Index[value];
	}
}
