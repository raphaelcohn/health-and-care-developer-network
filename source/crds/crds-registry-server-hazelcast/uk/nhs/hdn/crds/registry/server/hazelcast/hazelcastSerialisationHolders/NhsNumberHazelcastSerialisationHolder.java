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

package uk.nhs.hdn.crds.registry.server.hazelcast.hazelcastSerialisationHolders;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.hazelcast.hazelcastDataWriters.DigitsHazelcastDataObjectWriter;
import uk.nhs.hdn.common.hazelcast.hazelcastSerialisationHolders.AbstractHazelcastSerialisationHolder;
import uk.nhs.hdn.number.NhsNumber;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static uk.nhs.hdn.common.hazelcast.hazelcastDataReaders.NhsNumberHazelcastDataReader.NhsNumberHazelcastDataReaderInstance;

@SuppressWarnings({"SerializableHasSerializationMethods", "serial"})
public final class NhsNumberHazelcastSerialisationHolder extends AbstractHazelcastSerialisationHolder<NhsNumber>
{
	public NhsNumberHazelcastSerialisationHolder()
	{
	}

	@SuppressWarnings("NullableProblems")
	public NhsNumberHazelcastSerialisationHolder(@NotNull final NhsNumber nhsNumber)
	{
		super(nhsNumber);
	}

	@Override
	public void writeData(@NotNull final DataOutput out) throws IOException
	{
		assert heldValue != null;
		DigitsHazelcastDataObjectWriter.writeData(out, heldValue);
	}

	@Override
	public void readData(@NotNull final DataInput in) throws IOException
	{
		assert heldValue == null;
		heldValue = NhsNumberHazelcastDataReaderInstance.readData(in);
	}
}
