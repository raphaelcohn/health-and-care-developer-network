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

package uk.nhs.hdn.ckan.domain.ids;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.ckan.domain.uniqueNames.DatasetKey;
import uk.nhs.hdn.common.serialisers.FieldTokenName;
import uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser;
import uk.nhs.hdn.common.serialisers.separatedValues.matchers.Matcher;

import java.util.UUID;

import static java.util.UUID.fromString;
import static uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser.commaSeparatedValueSerialiser;
import static uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser.tabSeparatedValueSerialiser;
import static uk.nhs.hdn.common.serialisers.separatedValues.matchers.LeafMatcher.leaf;

public final class DatasetId extends AbstractId implements DatasetKey
{
	@FieldTokenName
	@NonNls
	@NotNull
	private static final String Field = "datasetId";

	@NotNull
	private static final Matcher SeparatedValuesSchema = leaf(Field, 0);

	@NotNull
	private static final String[] SeparatedValuesHeadings = {Field};

	@NotNull
	public static SeparatedValueSerialiser csvSerialiserForDatasetIds(final boolean writeHeaderLine)
	{
		return commaSeparatedValueSerialiser(SeparatedValuesSchema, writeHeaderLine, SeparatedValuesHeadings);
	}

	@NotNull
	public static SeparatedValueSerialiser tsvSerialiserForDatasetIds()
	{
		return tabSeparatedValueSerialiser(SeparatedValuesSchema, true, SeparatedValuesHeadings);
	}

	public static DatasetId valueOf(@NonNls @NotNull final String value)
	{
		return new DatasetId(fromString(value));
	}

	public DatasetId(@NonNls @NotNull final UUID value)
	{
		super(value);
	}
}
