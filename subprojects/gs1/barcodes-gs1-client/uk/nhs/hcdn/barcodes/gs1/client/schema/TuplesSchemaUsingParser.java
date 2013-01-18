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

package uk.nhs.hcdn.barcodes.gs1.client.schema;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.barcodes.gs1.companyPrefixes.Gs1CompanyPrefix;
import uk.nhs.hcdn.barcodes.gs1.companyPrefixes.remote.AdditionalInformation;
import uk.nhs.hcdn.barcodes.gs1.companyPrefixes.remote.Tuple;
import uk.nhs.hcdn.common.parsers.json.SchemaUsingParser;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.root.ArrayRootArrayConstructor;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.NonNullFieldExpectation;

import static uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.NonNullCollectToArrayObjectsOnlyForElementsArrayConstructor.nonNullArrayOfObjects;
import static uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.root.NonNullArrayRootArrayConstructor.rootIsArrayOf;
import static uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.JavaObjectConstructor.object;
import static uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.NonEmptyStringFieldExpectation.nonEmptyStringField;

public final class TuplesSchemaUsingParser extends SchemaUsingParser<Tuple>
{
	@NotNull
	public static final ArrayRootArrayConstructor<Tuple> TuplesSchema = rootIsArrayOf
	(
		TupleArrayCreator.TupleArray,
		nonNullArrayOfObjects
		(
			TupleArrayCreator.TupleArray,
			object
			(
				Tuple.class,
				NonNullFieldExpectation.nonNullField
				(
						"gs1CompanyPrefix",
						Gs1CompanyPrefix.class,
						Gs1CompanyPrefixArrayConstructor.Gs1CompanyPrefixArrayConstructorInstance
				),
				nonEmptyStringField("trust"),
				nonEmptyStringField("organisationName"),
				NonNullFieldExpectation.nonNullField
				(
						"additionalInformation",
						AdditionalInformation.class,
						AdditionalInformationObjectConstructor.AdditionalInformationObjectConstructorInstance
				)
			)
		)
	);

	@NotNull
	public static final SchemaUsingParser<Tuple> TuplesSchemaUsingParserInstance = new TuplesSchemaUsingParser();

	private TuplesSchemaUsingParser()
	{
		super(TuplesSchema);
	}
}
