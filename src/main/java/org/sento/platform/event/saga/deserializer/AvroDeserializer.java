package org.sento.platform.event.saga.deserializer;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecord;

public class AvroDeserializer {

    public static <T extends SpecificRecord> T fromBytes(
        byte[] data,
        Class<T> clazz
    ) {
        try {
            SpecificDatumReader<T> reader =
                new SpecificDatumReader<>(clazz);

            BinaryDecoder decoder =
                DecoderFactory.get().binaryDecoder(data, null);

            return reader.read(null, decoder);

        } catch (Exception e) {
            throw new RuntimeException("Avro deserialization failed", e);
        }
    }
}