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

package uk.nhs.hdn.ckan.api;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.ckan.domain.*;
import uk.nhs.hdn.ckan.domain.dates.MicrosecondTimestamp;
import uk.nhs.hdn.ckan.domain.ids.DatasetId;
import uk.nhs.hdn.ckan.domain.ids.GroupId;
import uk.nhs.hdn.ckan.domain.ids.RevisionId;
import uk.nhs.hdn.ckan.domain.uniqueNames.*;
import uk.nhs.hdn.common.exceptions.ShouldNeverHappenException;
import uk.nhs.hdn.common.http.client.HttpClient;
import uk.nhs.hdn.common.http.client.JavaHttpClient;
import uk.nhs.hdn.common.parsers.json.JsonSchema;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.tuples.Pair;

import java.io.UnsupportedEncodingException;

import static java.net.URLEncoder.encode;
import static uk.nhs.hdn.ckan.api.RelationshipType.*;
import static uk.nhs.hdn.ckan.schema.DatasetIdsArrayJsonSchema.DatasetIdsSchemaInstance;
import static uk.nhs.hdn.ckan.schema.DatasetJsonSchema.DatasetSchemaInstance;
import static uk.nhs.hdn.ckan.schema.DatasetNamesArrayJsonSchema.DatasetNamesSchemaInstance;
import static uk.nhs.hdn.ckan.schema.GroupIdsArrayJsonSchema.GroupIdsSchemaInstance;
import static uk.nhs.hdn.ckan.schema.GroupJsonSchema.GroupSchemaInstance;
import static uk.nhs.hdn.ckan.schema.GroupNamesArrayJsonSchema.GroupNamesSchemaInstance;
import static uk.nhs.hdn.ckan.schema.LicencesArrayJsonSchema.LicencesSchemaInstance;
import static uk.nhs.hdn.ckan.schema.RevisionIdsArrayJsonSchema.RevisionIdsSchemaInstance;
import static uk.nhs.hdn.ckan.schema.RevisionJsonSchema.RevisionSchemaInstance;
import static uk.nhs.hdn.ckan.schema.RevisionsArrayJsonSchema.RevisionsSchemaInstance;
import static uk.nhs.hdn.ckan.schema.TagCountsArrayJsonSchema.TagCountsSchemaInstance;
import static uk.nhs.hdn.ckan.schema.TagsArrayJsonSchema.TagsSchemaInstance;
import static uk.nhs.hdn.common.VariableArgumentsHelper.of;
import static uk.nhs.hdn.common.http.UrlHelper.commonPortNumber;
import static uk.nhs.hdn.common.http.UrlHelper.toUrl;
import static uk.nhs.hdn.common.http.client.connectionConfigurations.ChunkedUploadsConnectionConfiguration.DoesNotSupportChunkedUploads;
import static uk.nhs.hdn.common.tuples.Pair.pair;

@SuppressWarnings({"ClassNamePrefixedWithPackageName", "ClassWithTooManyMethods"})
public final class Api extends AbstractToString
{
	private static final char Slash = '/';

	@NotNull
	public static final Api DataGovUk = new Api(false, "data.gov.uk", "");
	public static final char Ampersand = '&';
	public static final char QuestionMark = '?';
	public static final char Equals = '=';
	@SuppressWarnings("ConstantNamingConvention") public static final String api = "api";
	@SuppressWarnings("ConstantNamingConvention") public static final String rest = "rest";
	@SuppressWarnings("ConstantNamingConvention") public static final String revision = "revision";
	@SuppressWarnings("ConstantNamingConvention") public static final String dataset = "dataset";
	@SuppressWarnings("ConstantNamingConvention") public static final String group = "group";
	@SuppressWarnings("ConstantNamingConvention") public static final String tag = "tag";
	@SuppressWarnings("ConstantNamingConvention") public static final String search = "search";

	private final boolean useHttps;
	@NotNull @NonNls private final String domainName;
	private final char portNumber;
	@NotNull @NonNls private final String absoluteUrlPath;

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public Api(final boolean useHttps, @NonNls @NotNull final String domainName, @NonNls @NotNull final String absoluteUrlPath)
	{
		this(useHttps, domainName, commonPortNumber(useHttps), absoluteUrlPath);
	}

	public Api(final boolean useHttps, @NonNls @NotNull final String domainName, final char portNumber, @NotNull final String absoluteUrlPath)
	{
		this.useHttps = useHttps;
		this.domainName = domainName;
		this.portNumber = portNumber;
		this.absoluteUrlPath = absoluteUrlPath;
	}

	@NotNull
	public ApiMethod<DatasetName[]> allDatasetNames()
	{
		return newApi(DatasetNamesSchemaInstance, api, "1", rest, dataset);
	}

	@NotNull
	public ApiMethod<DatasetId[]> allDatasetIds()
	{
		return newApi(DatasetIdsSchemaInstance, api, "2", rest, dataset);
	}

	@NotNull
	public ApiMethod<Dataset> dataset(@SuppressWarnings("TypeMayBeWeakened") @NotNull final DatasetKey datasetKey)
	{
		return newApi(DatasetSchemaInstance, api, "2", rest, dataset, datasetKey.value());
	}

	@NotNull
	public ApiMethod<Revision[]> datasetRevisions(@SuppressWarnings("TypeMayBeWeakened") @NotNull final DatasetKey datasetKey)
	{
		return newApi(RevisionsSchemaInstance, api, "2", rest, dataset, datasetKey.value(), "revisions");
	}

	@NotNull
	public ApiMethod<DatasetId[]> datasetRelationshipDependsOn(@NotNull final DatasetKey datasetKey)
	{
		return datasetRelationships(datasetKey, depends_on);
	}

	@NotNull
	public ApiMethod<DatasetId[]> datasetRelationshipDependencyOn(@NotNull final DatasetKey datasetKey)
	{
		return datasetRelationships(datasetKey, dependency_on);
	}

	@NotNull
	public ApiMethod<DatasetId[]> datasetRelationshipDerivesFrom(@NotNull final DatasetKey datasetKey)
	{
		return datasetRelationships(datasetKey, derives_from);
	}

	@NotNull
	public ApiMethod<DatasetId[]> datasetRelationshipHasDerivation(@NotNull final DatasetKey datasetKey)
	{
		return datasetRelationships(datasetKey, has_derivation);
	}

	@NotNull
	public ApiMethod<DatasetId[]> datasetRelationshipChildOf(@NotNull final DatasetKey datasetKey)
	{
		return datasetRelationships(datasetKey, child_of);
	}

	@NotNull
	public ApiMethod<DatasetId[]> datasetRelationshipParentOf(@NotNull final DatasetKey datasetKey)
	{
		return datasetRelationships(datasetKey, parent_of);
	}

	@NotNull
	public ApiMethod<DatasetId[]> datasetRelationshipLinksTo(@NotNull final DatasetKey datasetKey)
	{
		return datasetRelationships(datasetKey, links_to);
	}

	@NotNull
	public ApiMethod<DatasetId[]> datasetRelationshipLinkedFrom(@NotNull final DatasetKey datasetKey)
	{
		return datasetRelationships(datasetKey, linked_from);
	}

	@NotNull
	public ApiMethod<DatasetId[]> datasetRelationships(@SuppressWarnings("TypeMayBeWeakened") @NotNull final DatasetKey datasetKey, @NotNull final RelationshipType relationshipType)
	{
		return newApi(DatasetIdsSchemaInstance, api, "2", rest, dataset, datasetKey.value(), relationshipType.name());
	}

	@NotNull
	public ApiMethod<GroupName[]> allGroupNames()
	{
		return newApi(GroupNamesSchemaInstance, api, "1", rest, group);
	}

	@NotNull
	public ApiMethod<GroupId[]> allGroupIds()
	{
		return newApi(GroupIdsSchemaInstance, api, "2", rest, group);
	}

	@NotNull
	public ApiMethod<Group> group(@SuppressWarnings("TypeMayBeWeakened") @NotNull final GroupKey groupKey)
	{
		return newApi(GroupSchemaInstance, api, "2", rest, group, groupKey.value());
	}

	@NotNull
	public ApiMethod<TagName[]> allTags()
	{
		return newApi(TagsSchemaInstance, api, "2", rest, tag);
	}

	@NotNull
	public ApiMethod<DatasetId[]> datasetIdsWithTag(@NotNull final TagName tagName)
	{
		return newApi(DatasetIdsSchemaInstance, api, "2", rest, tag, tagName.value());
	}

	@NotNull
	public ApiMethod<Licence[]> allLicences()
	{
		return newApi(LicencesSchemaInstance, api, "2", rest, "licenses");
	}

	@NotNull
	public ApiMethod<Revision> revision(@SuppressWarnings("TypeMayBeWeakened") @NotNull final RevisionId revisionId)
	{
		return newApi(RevisionSchemaInstance, api, "2", rest, revision, revisionId.value());
	}

	@NotNull
	public ApiMethod<TagCount[]> tagCounts()
	{
		return newApi(TagCountsSchemaInstance, api, "2", "tag_counts");
	}

	@SuppressWarnings("unchecked")
	@NotNull
	public ApiMethod<RevisionId[]> revisions(@SuppressWarnings("TypeMayBeWeakened") @NotNull final RevisionId since)
	{
		return newApi(RevisionIdsSchemaInstance, of(api, "2", search, revision), pair("since_id", since.value()));
	}

	@SuppressWarnings("unchecked")
	@NotNull
	public ApiMethod<RevisionId[]> revisions(@NotNull final MicrosecondTimestamp since)
	{
		return newApi(RevisionIdsSchemaInstance, of(api, "2", search, revision), pair("since_time", since.toString()));
	}

	@NotNull
	private <V> ApiMethod<V> newApi(@NotNull final JsonSchema<V> jsonSchema, @NotNull @NonNls final String... urlPieces)
	{
		return newApiUsingAbsoluteUrlPath(jsonSchema, urlPath(urlPieces));
	}

	@SuppressWarnings("FinalMethodInFinalClass")
	@SafeVarargs
	@NotNull
	private final <V> ApiMethod<V> newApi(@NotNull final JsonSchema<V> jsonSchema, @NotNull @NonNls final String[] urlPieces, final Pair<String, String>... unencodedQueryStringParameters)
	{
		return newApiUsingAbsoluteUrlPath(jsonSchema, urlPath(urlPieces, unencodedQueryStringParameters));
	}

	@NotNull
	private <V> ApiMethod<V> newApiUsingAbsoluteUrlPath(@NotNull final JsonSchema<V> jsonSchema, @NotNull @NonNls final String absoluteUrlPath)
	{
		final HttpClient httpClient = new JavaHttpClient(toUrl(useHttps, domainName, portNumber, absoluteUrlPath), DoesNotSupportChunkedUploads);
		return new ApiMethod<>(httpClient, jsonSchema);
	}

	private String urlPath(final String... urlPieces)
	{
		final StringBuilder stringBuilder = new StringBuilder(absoluteUrlPath);
		for (final String urlPiece : urlPieces)
		{
			stringBuilder.append(Slash).append(urlPiece);
		}
		return stringBuilder.toString();
	}

	@SuppressWarnings("FinalMethodInFinalClass") // @SafeVarargs causes this problem
	@SafeVarargs
	private final String urlPath(final String[] urlPieces, final Pair<String, String>... unencodedQueryStringParameters)
	{
		final String urlPath = urlPath(urlPieces);
		final StringBuilder stringBuilder = new StringBuilder(urlPath);
		boolean afterFirst = false;
		for (final Pair<String, String> unencodedQueryStringParameter : unencodedQueryStringParameters)
		{
			if (afterFirst)
			{
				stringBuilder.append(Ampersand);
			}
			else
			{
				stringBuilder.append(QuestionMark);
				afterFirst = true;
			}
			stringBuilder.append(encodeUsingUtf8ButBeAwareThatTheEncodingUsesPlusSignsAndIsNotValidForLatterRfcSpecifications(unencodedQueryStringParameter.a));
			stringBuilder.append(Equals);
			stringBuilder.append(encodeUsingUtf8ButBeAwareThatTheEncodingUsesPlusSignsAndIsNotValidForLatterRfcSpecifications(unencodedQueryStringParameter.b));
		}
		return stringBuilder.toString();
	}

	@NotNull
	private static String encodeUsingUtf8ButBeAwareThatTheEncodingUsesPlusSignsAndIsNotValidForLatterRfcSpecifications(@NotNull final String value)
	{
		try
		{
			return encode(value, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			throw new ShouldNeverHappenException(e);
		}
	}

	@Override
	public boolean equals(@Nullable final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null || getClass() != obj.getClass())
		{
			return false;
		}

		final Api that = (Api) obj;

		if ((int) portNumber != (int) that.portNumber)
		{
			return false;
		}
		if (useHttps != that.useHttps)
		{
			return false;
		}
		if (!absoluteUrlPath.equals(that.absoluteUrlPath))
		{
			return false;
		}
		if (!domainName.equals(that.domainName))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = useHttps ? 1 : 0;
		result = 31 * result + domainName.hashCode();
		result = 31 * result + (int) portNumber;
		result = 31 * result + absoluteUrlPath.hashCode();
		return result;
	}
}
