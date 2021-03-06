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

package uk.nhs.hdn.ihe.xds.builders;

import org.jetbrains.annotations.NotNull;
import org.openhealthtools.ihe.bridge.type.GenericPnRSubmissionSet;
import org.openhealthtools.ihe.bridge.type.GenericProvideAndRegisterType;
import org.openhealthtools.ihe.bridge.type.XDSDocType;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.ihe.xds.builders.abstractions.Builder;

public final class GenericProvideAndRegisterBuilder extends AbstractToString
{
	@SafeVarargs
	@NotNull
	public static GenericProvideAndRegisterType genericProvideAndRegister(@NotNull final GenericPnRSubmissionSet submissionSet, @NotNull final Builder<XDSDocType>... xdsDocuments)
	{
		final int length = xdsDocuments.length;
		final XDSDocType[] xdsDocTypes = new XDSDocType[length];
		for (int index = 0; index < length; index++)
		{
			xdsDocTypes[index] = xdsDocuments[index].build();
		}
		return genericProvideAndRegister(submissionSet, xdsDocTypes);
	}

	@NotNull
	public static GenericProvideAndRegisterType genericProvideAndRegister(@NotNull final GenericPnRSubmissionSet submissionSet, @NotNull final XDSDocType... xdsDocuments)
	{
		final GenericProvideAndRegisterType done = new GenericProvideAndRegisterType();
		done.setSubmissionSet(submissionSet);
		done.setDocuments(xdsDocuments);
		return done;
	}
}
