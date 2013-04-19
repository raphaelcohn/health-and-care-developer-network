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

package uk.nhs.hdn.crds.store.client;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.http.client.ApiMethod;
import uk.nhs.hdn.common.http.client.exceptions.CorruptResponseException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotConnectHttpException;
import uk.nhs.hdn.common.http.client.exceptions.UnacceptableResponseException;
import uk.nhs.hdn.common.http.client.json.JsonGenericGetApi;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.crds.store.domain.SimplePatientRecord;
import uk.nhs.hdn.crds.store.domain.identifiers.ProviderIdentifier;
import uk.nhs.hdn.crds.store.domain.identifiers.RepositoryIdentifier;
import uk.nhs.hdn.crds.store.domain.metadata.ProviderMetadataRecord;
import uk.nhs.hdn.crds.store.domain.metadata.RepositoryMetadataRecord;
import uk.nhs.hdn.number.NhsNumber;

import static uk.nhs.hdn.crds.store.client.jsonSchemas.ProviderMetadataRecordArrayJsonSchema.ProviderMetadataRecordsSchemaUsingParserInstance;
import static uk.nhs.hdn.crds.store.client.jsonSchemas.RepositoryMetadataRecordArrayJsonSchema.RepositoryMetadataRecordsSchemaUsingParserInstance;
import static uk.nhs.hdn.crds.store.client.jsonSchemas.SimplePatientRecordArrayJsonSchema.SimplePatientRecordsSchemaUsingParserInstance;
import static uk.nhs.hdn.crds.store.domain.metadata.IdentifierConstructor.Provider;
import static uk.nhs.hdn.crds.store.domain.metadata.IdentifierConstructor.Repository;

public final class ConcreteCrdsRestApi extends AbstractToString implements CrdsRestApi
{
	@SuppressWarnings("ConstantNamingConvention") @NonNls @NotNull private static final String crds = "crds";
	@SuppressWarnings("ConstantNamingConvention") @NonNls @NotNull private static final String store = "store";
	@SuppressWarnings("ConstantNamingConvention") @NonNls @NotNull private static final String patientRecordStore = "patient-record-store";
	@SuppressWarnings("ConstantNamingConvention") @NonNls @NotNull private static final String metadata = "metadata";

	@NotNull
	public static CrdsRestApi crdsStore(@NotNull final String domainName)
	{
		return new ConcreteCrdsRestApi(new JsonGenericGetApi(false, domainName, ""));
	}

	@NotNull private final JsonGenericGetApi jsonGenericGetApi;

	public ConcreteCrdsRestApi(@NotNull final JsonGenericGetApi jsonGenericGetApi)
	{
		this.jsonGenericGetApi = jsonGenericGetApi;
	}

	@Override
	@NotNull
	public ApiMethod<SimplePatientRecord> simplePatientRecord(@NotNull final NhsNumber nhsNumber)
	{
		return new ApiMethod<SimplePatientRecord>()
		{
			private final ApiMethod<SimplePatientRecord[]> internal = jsonGenericGetApi.newApiMethod(SimplePatientRecordsSchemaUsingParserInstance, crds, store, patientRecordStore, nhsNumber.normalised());

			@NotNull
			@Override
			public SimplePatientRecord execute() throws UnacceptableResponseException, CorruptResponseException, CouldNotConnectHttpException
			{
				return internal.execute()[0];
			}
		};
	}

	@Override
	@NotNull
	public ApiMethod<ProviderMetadataRecord> providerMetadataRecord(@NotNull final ProviderIdentifier providerIdentifier)
	{
		return new ApiMethod<ProviderMetadataRecord>()
		{
			private final ApiMethod<ProviderMetadataRecord[]> internal = jsonGenericGetApi.newApiMethod(ProviderMetadataRecordsSchemaUsingParserInstance, crds, store, metadata, Provider.restName(), providerIdentifier.toUuidString());

			@NotNull
			@Override
			public ProviderMetadataRecord execute() throws UnacceptableResponseException, CorruptResponseException, CouldNotConnectHttpException
			{
				return internal.execute()[0];
			}
		};
	}

	@Override
	@NotNull
	public ApiMethod<RepositoryMetadataRecord> repositoryMetadataRecord(@NotNull final RepositoryIdentifier repositoryIdentifier)
	{
		return new ApiMethod<RepositoryMetadataRecord>()
		{
			private final ApiMethod<RepositoryMetadataRecord[]> internal = jsonGenericGetApi.newApiMethod(RepositoryMetadataRecordsSchemaUsingParserInstance, crds, store, metadata, Repository.restName(), repositoryIdentifier.toUuidString());

			@NotNull
			@Override
			public RepositoryMetadataRecord execute() throws UnacceptableResponseException, CorruptResponseException, CouldNotConnectHttpException
			{
				return internal.execute()[0];
			}
		};
	}
}