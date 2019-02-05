package com.rposcro.jwavez.serial.buffers.dispatchers

import spock.lang.Specification
import spock.lang.Unroll

import java.nio.BufferOverflowException
import java.nio.ByteBuffer

class SingletonBufferDispatcherSpec extends Specification {

    @Unroll
    def "provides buffer of enough size"() {
        given:
        def dispatcher = new SingletonBufferDispatcher();
        def buffer = dispatcher.allocateBuffer(data.size());

        when:
        data.stream().forEach({ bt -> buffer.put((byte) bt)});
        def byteBuffer = buffer.asByteBuffer();

        then:
        byteBuffer.remaining() == data.size();
        data == listFromBuffer(byteBuffer);

        where:
        data                | _
        []                  | _
        [0x44]              | _
        [0x22, 0x44]        | _
        [0x22, 0x44, 0x66]  | _
    }

    @Unroll
    def "throws exception when putting too many bytes"() {
        given:
        def dispatcher = new SingletonBufferDispatcher();
        def buffer = dispatcher.allocateBuffer(data.size());

        when:
        data.stream().forEach({ bt -> buffer.put((byte) bt)});
        buffer.put((byte) 0x01);

        then:
        thrown BufferOverflowException;

        where:
        data                | _
        []                  | _
        [0x44]              | _
        [0x22, 0x44]        | _
        [0x22, 0x44, 0x66]  | _
    }

    def "releases buffer by calling dispatcher's recycle"() {
        given:
        def dispatcher = Spy(SingletonBufferDispatcher);
        def buffer = dispatcher.allocateBuffer(12);

        when:
        buffer.release();

        then:
        1 * dispatcher.recycleBuffer(_);
    }


    def listFromBuffer(ByteBuffer byteBuffer) {
        def list = [];
        while (byteBuffer.hasRemaining()) {
            list.add((0xff) & ((int) byteBuffer.get()));
        }
        return list;
    }
}
