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

package uk.nhs.hdn.common.reflection.toString.toStringGenerators;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public final class PrimitiveBooleanArrayToStringGenerator extends AbstractToStringGenerator<boolean[]>
{
	@NotNull
	public static final ToStringGenerator<?> PrimitiveBooleanArrayToStringGeneratorInstance = new PrimitiveBooleanArrayToStringGenerator();

	private PrimitiveBooleanArrayToStringGenerator()
	{
		super(boolean[].class);
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	@NotNull
	@Override
	public String toString(@NotNull final boolean[] value)
	{
		return Arrays.toString(value);
	}
}
