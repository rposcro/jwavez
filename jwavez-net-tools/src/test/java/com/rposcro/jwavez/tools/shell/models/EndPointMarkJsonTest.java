package com.rposcro.jwavez.tools.shell.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class EndPointMarkJsonTest {

    @ParameterizedTest
    @ValueSource(strings = {"93-150", "197-255"})
    public void testSerialization(String testValue) throws Exception {
        EndPointMark address = new EndPointMark(testValue);
        String expected = String.format("\"%s\"", testValue);
        String json = new ObjectMapper().writer().writeValueAsString(address);

        Assertions.assertEquals(expected, json);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1-13", "240-50", "93-150", "197-255"})
    public void testDeserialization(String testValue) throws Exception {
        String json = String.format("\"%s\"", testValue);
        int expectedNode = Integer.parseInt(testValue.substring(0, testValue.indexOf('-')));
        int expectedEndPoint = Integer.parseInt(testValue.substring(testValue.indexOf('-') + 1));
        EndPointMark address = new ObjectMapper().readValue(json, EndPointMark.class);

        Assertions.assertEquals(testValue, address.getMark());
        Assertions.assertEquals(expectedNode, address.getNodeId());
        Assertions.assertEquals(expectedEndPoint, address.getEndPointId());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1-13", "240-50", "93-150", "197-255"})
    public void testNumericConstruction(String testValue) throws Exception {
        byte expectedNode = (byte) Integer.parseInt(testValue.substring(0, testValue.indexOf('-')));
        byte expectedEndPoint = (byte) Integer.parseInt(testValue.substring(testValue.indexOf('-') + 1));
        EndPointMark address = new EndPointMark(expectedNode, expectedEndPoint);

        Assertions.assertEquals(testValue, address.getMark());
        Assertions.assertEquals(expectedNode, (byte) address.getNodeId());
        Assertions.assertEquals(expectedEndPoint, (byte) address.getEndPointId());
    }
}
