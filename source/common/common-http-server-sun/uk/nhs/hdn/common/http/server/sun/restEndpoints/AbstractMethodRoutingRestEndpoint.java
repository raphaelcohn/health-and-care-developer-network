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

package uk.nhs.hdn.common.http.server.sun.restEndpoints;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.http.ProtocolAndVersion;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.clientError4xxs.BadRequestException;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.methodEndpoints.BadRequestMethodEndpoint;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.methodEndpoints.MethodEndpoint;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.ResourceStateSnapshot;

import java.io.IOException;
import java.net.Authenticator;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import static uk.nhs.hdn.common.http.ProtocolAndVersion.HTTP10;
import static uk.nhs.hdn.common.http.ProtocolAndVersion.HTTP11;
import static uk.nhs.hdn.common.http.server.sun.helpers.RequestHeadersHelper.validateConnectionRequestHeader;
import static uk.nhs.hdn.common.http.server.sun.helpers.RequestHeadersHelper.validateTransferEncodingRequestHeader;
import static uk.nhs.hdn.common.http.server.sun.restEndpoints.methodEndpoints.UnsupportedProtocolMethodEndpoint.unsupportedProtocolMethodEndpoint;

public abstract class AbstractMethodRoutingRestEndpoint<R extends ResourceStateSnapshot> extends AbstractRestEndpoint
{
	@NotNull @ProtocolAndVersion
	private static final Set<String> ValidProtocolAndVersions = new HashSet<String>(2)
	{{
		add(HTTP10);
		add(HTTP11);
	}};

	private static final int Slash = (int) '/';
	private final int length;
	private final boolean lastCharacterIsSlash;

	protected AbstractMethodRoutingRestEndpoint(@NonNls @NotNull final String absoluteRawPath, @Nullable final Authenticator authenticator)
	{
		super(absoluteRawPath, authenticator);
		length = absoluteRawPath.length();
		lastCharacterIsSlash = (int) absoluteRawPath.charAt(length - 1) == Slash;
	}

	@NotNull
	protected abstract MethodEndpoint<R> methodEndpoint(@NotNull final String method);

	@NotNull
	protected abstract R snapshotOfStateThatIsInvariantForRequest();

	@Override
	public final void handle(@NotNull final HttpExchange httpExchange) throws IOException
	{
		final URI requestURI = httpExchange.getRequestURI();
		@Nullable final String rawQueryString = requestURI.getRawQuery();
		final String rawPath = requestURI.getRawPath();
		final String rawRelativePath = rawPath.substring(length);

		final R resourceStateSnapshot = snapshotOfStateThatIsInvariantForRequest();
		try
		{
			final MethodEndpoint<R> methodEndpoint;
			if (ValidProtocolAndVersions.contains(httpExchange.getProtocol()))
			{
				if (isInvalidRelativePath(rawRelativePath))
				{
					methodEndpoint = new BadRequestMethodEndpoint<>("Invalid relative path");
				}
				else
				{
					methodEndpoint = methodEndpoint(httpExchange);
				}
			}
			else
			{
				methodEndpoint = unsupportedProtocolMethodEndpoint();
			}

			methodEndpoint.handle(rawRelativePath, rawQueryString, httpExchange, resourceStateSnapshot);
		}
		catch (BadRequestException e)
		{
			new BadRequestMethodEndpoint<>(e).handle(rawRelativePath, rawQueryString, httpExchange, resourceStateSnapshot);
		}
	}

	private MethodEndpoint<R> methodEndpoint(final HttpExchange httpExchange) throws BadRequestException
	{
		final Headers requestHeaders = httpExchange.getRequestHeaders();
		validateConnectionRequestHeader(requestHeaders);
		validateTransferEncodingRequestHeader(requestHeaders);

		return methodEndpoint(httpExchange.getRequestMethod());
	}

	private boolean isInvalidRelativePath(final String rawRelativePath)
	{
		if (lastCharacterIsSlash)
		{
			if (rawRelativePath.startsWith("/"))
			{
				return true;
			}
		}
		else
		{
			if (!rawRelativePath.startsWith("/"))
			{
				return true;
			}
		}
		return false;
	}

}
