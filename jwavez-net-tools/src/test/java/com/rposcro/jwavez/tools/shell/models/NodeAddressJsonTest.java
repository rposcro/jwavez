package com.rposcro.jwavez.tools.shell.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class NodeAddressJsonTest {

    @ParameterizedTest
    @ValueSource(shorts = {93, 197, 255})
    public void testSerialization(short testValue) throws Exception {
        NodeAddress addressByNumber = new NodeAddress((byte) testValue);
        NodeAddress addressByString = new NodeAddress("" + testValue);
        String expected = "\"" + testValue + "\"";

        ObjectMapper mapper = new ObjectMapper();
        String jsonByNumber = mapper.writer().writeValueAsString(addressByNumber);
        String jsonByString = mapper.writer().writeValueAsString(addressByString);

        Assertions.assertEquals(expected, jsonByNumber);
        Assertions.assertEquals(expected, jsonByString);
    }

    @ParameterizedTest
    @ValueSource(shorts = {93, 237})
    public void testDeserialization(short testValue) throws Exception {

        String json = String.format("\"%s\"", "" + testValue);
        NodeAddress address = new ObjectMapper().readValue(json, NodeAddress.class);

        Assertions.assertEquals("" + testValue, address.getAddress());
        Assertions.assertEquals(testValue, ((short) address.getNodeId()) & 0xff);
    }
}
