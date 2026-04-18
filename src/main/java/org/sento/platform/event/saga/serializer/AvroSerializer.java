package org.sento.platform.event.saga.serializer;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecord;

import java.io.ByteArrayOutputStream;

public class AvroSerializer {

    public static <T extends SpecificRecord> byte[] toBytes(T record) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            SpecificDatumWriter<T> writer =
                new SpecificDatumWriter<>(record.getSchema());

            BinaryEncoder encoder =
                EncoderFactory.get().binaryEncoder(out, null);

            writer.write(record, encoder);
            encoder.flush();
            out.close();

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Avro serialization failed", e);
        }
    }
}
