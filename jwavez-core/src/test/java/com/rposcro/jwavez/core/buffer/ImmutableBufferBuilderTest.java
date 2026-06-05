package com.rposcro.jwavez.core.buffer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ImmutableBufferBuilderTest {

    @Mock
    private ByteBufferManager byteBufferManager;

    private ImmutableBufferBuilder immutableBufferBuilder;

    @BeforeEach
    void setUp() {
        when(byteBufferManager.obtainBuffer(255)).thenReturn(new ByteBuffer(255, byteBufferManager));
        this.immutableBufferBuilder = new ImmutableBufferBuilder(byteBufferManager, 255);
    }

    @Test
    void addByte_shouldFillTheBuffer() {
        ImmutableBuffer buffer = immutableBufferBuilder
            .add((byte) 10)
            .add((byte) 20)
            .build();
        assertEquals(10, buffer.getByte(0));
        assertEquals(20, buffer.getByte(1));
    }

    @Test
    void addByteSupplier_shouldFillTheBuffer() {
        ImmutableBuffer buffer = immutableBufferBuilder
            .add(() -> (byte) 30)
            .build();
        assertEquals(30, buffer.getByte(0));
    }

    @Test
    void addWord_shouldFillTheBuffer() {
        ImmutableBuffer buffer = immutableBufferBuilder
            .addWord((short) 0x1E2D)
            .build();
        assertEquals(0x1e, buffer.getByte(0));
        assertEquals(0x2d, buffer.getByte(1));
    }
}
