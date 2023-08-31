package com.rposcro.jwavez.serial;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class TestUtils {

    public static List<Integer> dataFromBuffer(ImmutableBuffer frameBuffer) {
        List<Integer> data = new ArrayList<>(frameBuffer.available());
        while (frameBuffer.hasNext()) {
            data.add(frameBuffer.nextByte() & 0xff);
        }
        return data;
    }

    public static ByteBuffer byteBufferFromData(List<Integer> data) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(data.size());
        data.forEach(value -> byteBuffer.put(value.byteValue()));
        byteBuffer.position(0);
        return byteBuffer;
    }

    public static byte[] asByteArray(List<Integer> bytesList) {
        byte[] bytes = new byte[bytesList.size()];
        int idx = 0;
        for (Integer val : bytesList) {
            bytes[idx++] = val.byteValue();
        }
        return bytes;
    }
}
