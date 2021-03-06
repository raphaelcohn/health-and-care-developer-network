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

package uk.nhs.hdn.dts.domain;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.serialisers.CouldNotSerialiseValueException;
import uk.nhs.hdn.common.serialisers.CouldNotWriteValueException;
import uk.nhs.hdn.common.serialisers.ValueSerialisable;
import uk.nhs.hdn.common.serialisers.ValueSerialiser;
import uk.nhs.hdn.common.unknown.IsUnknown;
import uk.nhs.hdn.common.unknown.IsUnknownException;

import java.util.HashMap;
import java.util.Map;

public enum BooleanFlag implements IsUnknown, ValueSerialisable
{
	UnknownBooleanFlag,
	Y(true),
	N(false),
	;

	private final boolean isUnknown;
	private final boolean value;

	@SuppressWarnings("UtilityClassWithoutPrivateConstructor")
	private static final class CompilerWorkaround
	{
		private static final Map<String, BooleanFlag> Index = new HashMap<>(2);
	}

	@SuppressWarnings("ThisEscapedInObjectConstruction")
	BooleanFlag()
	{
		isUnknown = true;
		value = false;
		CompilerWorkaround.Index.put("", this);
	}

	@SuppressWarnings("ThisEscapedInObjectConstruction")
	BooleanFlag(final boolean value)
	{
		isUnknown = false;
		this.value = value;
		CompilerWorkaround.Index.put(name(), this);
	}

	@Override
	public boolean isUnknown()
	{
		return isUnknown;
	}

	@Override
	public boolean isKnown()
	{
		return !isUnknown;
	}

	@Override
	public boolean isSameKnownessAs(@NotNull final IsUnknown that)
	{
		if (isUnknown)
		{
			return that.isUnknown();
		}
		else
		{
			return that.isKnown();
		}
	}

	@Override
	public final boolean isDifferentKnownessAs(@NotNull final IsUnknown that)
	{
		return !isSameKnownessAs(that);
	}

	@SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
	@Nullable
	public Boolean valueIfPotentiallyUnknown()
	{
		if (isUnknown)
		{
			return null;
		}
		return value;
	}

	@SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
	public boolean value()
	{
		if (isUnknown)
		{
			throw new IsUnknownException();
		}
		return value;
	}

	@Override
	public void serialiseValue(@NotNull final ValueSerialiser valueSerialiser) throws CouldNotSerialiseValueException
	{
		try
		{
			valueSerialiser.writeValue(name());
		}
		catch (CouldNotWriteValueException e)
		{
			throw new CouldNotSerialiseValueException(this, e);
		}
	}

	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@Nullable
	public static BooleanFlag booleanFlag(@NotNull final CharSequence value)
	{
		return CompilerWorkaround.Index.get(value);
	}

	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@NotNull
	public static BooleanFlag booleanFlag(final boolean value)
	{
		return value ? Y : N;
	}
}
