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

package uk.nhs.hdn.ckan.schema.arrayCreators;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.ckan.domain.Revision;
import uk.nhs.hdn.common.arrayCreators.AbstractArrayCreator;
import uk.nhs.hdn.common.arrayCreators.ArrayCreator;

public final class RevisionArrayCreator extends AbstractArrayCreator<Revision>
{
	@NotNull
	public static final ArrayCreator<Revision> RevisionArray = new RevisionArrayCreator();

	private RevisionArrayCreator()
	{
		super(Revision.class, Revision[].class);
	}

	@NotNull
	@Override
	public Revision[] newInstance1(final int size)
	{
		return new Revision[size];
	}

	@NotNull
	@Override
	public Revision[][] newInstance2(final int size)
	{
		return new Revision[size][];
	}
}
