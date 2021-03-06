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

package uk.nhs.hdn.common.http.client.xml;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xml.sax.SAXException;
import uk.nhs.hdn.common.MillisecondsSince1970;
import uk.nhs.hdn.common.http.ResponseCode;
import uk.nhs.hdn.common.http.ResponseCodeRange;
import uk.nhs.hdn.common.http.client.exceptions.UnacceptableResponseException;
import uk.nhs.hdn.common.http.client.getHttpResponseUsers.GetHttpResponseUser;
import uk.nhs.hdn.common.parsers.xml.SchemaParser;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;
import static uk.nhs.hdn.common.CharsetHelper.Utf8;
import static uk.nhs.hdn.common.http.ContentTypeWithCharacterSet.LegacyXmlContentTypeUtf8;
import static uk.nhs.hdn.common.http.ResponseCode.NoContentResponseCode;
import static uk.nhs.hdn.common.http.ResponseCode.OkResponseCode;
import static uk.nhs.hdn.common.http.ResponseCodeRange.Successful2xx;

public final class LegacyXmlGetHttpResponseUser<V> extends AbstractToString implements GetHttpResponseUser<V>
{
	@NotNull @NonNls
	private static final String Identity = "identity";

	@NotNull
	private final SchemaParser<V> schemaUsingParser;

	public LegacyXmlGetHttpResponseUser(@NotNull final SchemaParser<V> schemaUsingParser)
	{
		this.schemaUsingParser = schemaUsingParser;
	}

	@Override
	@NotNull
	public V response(@ResponseCode final int responseCode, @NotNull final ResponseCodeRange responseCodeRange, @MillisecondsSince1970 final long date, @MillisecondsSince1970 final long expires, final long contentLengthOrMinusOneIfNoneSupplied, @Nullable final String contentType, @NonNls @Nullable final String contentEncoding, @NotNull final InputStream inputStream) throws UnacceptableResponseException
	{
		guardResponseIsValid(responseCode, responseCodeRange, contentLengthOrMinusOneIfNoneSupplied, contentType, contentEncoding);
		try
		{
			return schemaUsingParser.parse(inputStream);
		}
		catch (SAXException e)
		{
			throw new UnacceptableResponseException("Invalid XML", e, responseCode);
		}
		catch (IOException e)
		{
			throw new UnacceptableResponseException("IOException", e, responseCode);
		}
	}

	@SuppressWarnings("MethodWithMoreThanThreeNegations")
	private static Charset guardResponseIsValid(final int responseCode, @NotNull final ResponseCodeRange responseCodeRange, final long contentLengthOrMinusOneIfNoneSupplied, final String contentType, final String contentEncoding) throws UnacceptableResponseException
	{
		if (responseCodeRange != Successful2xx)
		{
			throw new UnacceptableResponseException(format(ENGLISH, "was not a successful 2xx response code, but %1$s", responseCode), responseCode);
		}
		if (responseCode == NoContentResponseCode || contentLengthOrMinusOneIfNoneSupplied == 0L)
		{
			throw new UnacceptableResponseException("no content", responseCode);
		}

		if (responseCode != OkResponseCode)
		{
			throw new UnacceptableResponseException("unimplemented response code", responseCode);
		}

		if (contentEncoding != null)
		{
			if (!contentEncoding.isEmpty() && !Identity.equalsIgnoreCase(contentEncoding))
			{
				throw new UnacceptableResponseException("compressed content encodings are not supported yet", responseCode);
			}
		}

		if (contentType == null)
		{
			throw new UnacceptableResponseException("no Content-Type supplied", responseCode);
		}
		// Hideous. But until we have to parse anything other than JSON UTF-8, there's no point writing a content-type parser...
		if (!contentType.replace(" ", "").equalsIgnoreCase(LegacyXmlContentTypeUtf8))
		{
			throw new UnacceptableResponseException("content is not " + LegacyXmlContentTypeUtf8, responseCode);
		}

		return Utf8;
	}
}
