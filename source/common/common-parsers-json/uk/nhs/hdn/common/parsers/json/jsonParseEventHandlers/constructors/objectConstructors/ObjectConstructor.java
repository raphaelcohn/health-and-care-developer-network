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

package uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.Constructor;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.ArrayConstructor;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.schemaViolationInvalidJsonExceptions.SchemaViolationInvalidJsonException;

import java.math.BigDecimal;

public interface ObjectConstructor<A> extends Constructor<A>
{
	void putObjectValue(@NotNull final A objectCollector, @NotNull final String key, @Nullable final Object value) throws SchemaViolationInvalidJsonException;

	void putArrayValue(@NotNull final A objectCollector, @NotNull final String key, @Nullable final Object value) throws SchemaViolationInvalidJsonException;

	void putLiteralBooleanValue(@NotNull final A objectCollector, @NotNull final String key, final boolean value) throws SchemaViolationInvalidJsonException;

	void putLiteralNullValue(@NotNull final A objectCollector, @NotNull final String key) throws SchemaViolationInvalidJsonException;

	void putConstantStringValue(@NotNull final A objectCollector, @NotNull final String key, @NotNull final String value) throws SchemaViolationInvalidJsonException;

	void putConstantNumberValue(@NotNull final A objectCollector, @NotNull final String key, final long value) throws SchemaViolationInvalidJsonException;

	void putConstantNumberValue(@NotNull final A objectCollector, @NotNull final String key, @NotNull final BigDecimal value) throws SchemaViolationInvalidJsonException;

	@NotNull
	ArrayConstructor<?> arrayConstructor(@NotNull final String key) throws SchemaViolationInvalidJsonException;

	@NotNull
	ObjectConstructor<?> objectConstructor(@NotNull final String key) throws SchemaViolationInvalidJsonException;
}
