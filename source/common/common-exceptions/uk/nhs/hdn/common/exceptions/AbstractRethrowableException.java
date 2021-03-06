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

package uk.nhs.hdn.common.exceptions;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractRethrowableException extends Exception
{
	@SuppressWarnings("NonFinalFieldOfException")
	private boolean stackTraceFilled;

	protected AbstractRethrowableException(@NonNls @NotNull final String message)
	{
		super(message);
		stackTraceFilled = false;
	}

	protected AbstractRethrowableException(@NonNls @NotNull final String message, @NotNull final Throwable cause)
	{
		super(message, cause);
		stackTraceFilled = false;
	}

	@SuppressWarnings("NonSynchronizedMethodOverridesSynchronizedMethod")
	@Override
	public Throwable fillInStackTrace()
	{
		if (stackTraceFilled)
		{
			return this;
		}
		stackTraceFilled = true;
		return super.fillInStackTrace();
	}
}
