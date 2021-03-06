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

package uk.nhs.hdn.barcodes.gs1.client.application;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.barcodes.gs1.client.ClientApplication;
import uk.nhs.hdn.common.commandLine.AbstractConsoleEntryPoint;
import uk.nhs.hdn.common.http.client.exceptions.CorruptResponseException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotConnectHttpException;
import uk.nhs.hdn.common.http.client.exceptions.UnacceptableResponseException;

import static uk.nhs.hdn.barcodes.gs1.client.ClientApplication.DefaultDomainName;
import static uk.nhs.hdn.barcodes.gs1.client.ClientApplication.DefaultPort;
import static uk.nhs.hdn.barcodes.gs1.client.application.Demonstration.demonstrateClientApplication;

public final class HdnGs1ClientConsoleEntryPoint extends AbstractConsoleEntryPoint
{
	private static final String DomainNameOption = "domain-name";
	private static final String PortOption = "port";
	private static final String UseHttpsOption = "use-https";
	private static final String GtinOption = "gtin";
	private static final String CacheOption = "cache";

	private static final String DefaultImaginaryBarcodeButRealCompanyPrefix = "5055220798768";

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void main(@NotNull final String... commandLineArguments)
	{
		execute(HdnGs1ClientConsoleEntryPoint.class, commandLineArguments);
	}

	@Override
	protected boolean options(@NotNull final OptionParser options)
	{
		options.accepts(DomainNameOption).withRequiredArg().ofType(String.class).defaultsTo(DefaultDomainName).describedAs("domain name to connect to");
		options.accepts(PortOption).withRequiredArg().ofType(Integer.class).defaultsTo((int) DefaultPort).describedAs("port to connect to HTTP(S) on");
		options.accepts(UseHttpsOption);
		options.accepts(CacheOption);
		options.accepts(GtinOption).requiredIf(CacheOption).withRequiredArg().ofType(String.class).defaultsTo(DefaultImaginaryBarcodeButRealCompanyPrefix).describedAs("A sequence of 12, 13 or 14 barcode digits 0 to 9 inclusive representing a GTIN-12, GTIN-13 or GTIN-14 (Global Trade Item Number)");
		return true;
	}

	@Override
	protected void execute(@NotNull final OptionSet optionSet) throws CouldNotConnectHttpException, CorruptResponseException, UnacceptableResponseException
	{
		@NotNull final String domainName = defaulted(optionSet, DomainNameOption);

		final char httpPort = portNumber(optionSet, PortOption);

		final boolean useHttps = optionSet.has(UseHttpsOption);

		final boolean cache = optionSet.has(CacheOption);

		@Nullable final CharSequence gtin = optionSet.has(GtinOption) ? (CharSequence) optionSet.valueOf(GtinOption) : null;

		//noinspection NumericCastThatLosesPrecision
		final ClientApplication clientApplication = new ClientApplication(useHttps, domainName, httpPort);
		demonstrateClientApplication(cache, gtin, clientApplication);
	}
}
