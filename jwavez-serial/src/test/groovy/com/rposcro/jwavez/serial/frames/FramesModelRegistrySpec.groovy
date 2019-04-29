package com.rposcro.jwavez.serial.frames

import com.rposcro.jwavez.serial.frames.callbacks.AddNodeToNetworkCallback
import com.rposcro.jwavez.serial.frames.callbacks.ApplicationCommandHandlerCallback
import com.rposcro.jwavez.serial.frames.callbacks.ApplicationUpdateCallback
import com.rposcro.jwavez.serial.frames.callbacks.RemoveNodeFromNetworkCallback
import com.rposcro.jwavez.serial.frames.callbacks.SendDataCallback
import com.rposcro.jwavez.serial.frames.responses.GetCapabilitiesResponse
import com.rposcro.jwavez.serial.frames.responses.GetVersionResponse
import com.rposcro.jwavez.serial.frames.responses.SendDataResponse
import spock.lang.Specification
import spock.lang.Unroll

class FramesModelRegistrySpec extends Specification {

    @Unroll
    def "Callbacks are registered (#functionId, #expectedClass)"() {
        given:
        FramesModelRegistry registry = FramesModelRegistry.defaultRegistry();

        when:
        def optionalClass = registry.callbackClass((byte) functionId);

        then:
        optionalClass.isPresent();
        optionalClass.get() == expectedClass;

        where:
        functionId  | expectedClass
        0x4a        | AddNodeToNetworkCallback.class
        0x04        | ApplicationCommandHandlerCallback.class
        0x49        | ApplicationUpdateCallback.class
        0x4b        | RemoveNodeFromNetworkCallback.class
        0x13        | SendDataCallback.class
    }

    @Unroll
    def "Responses are registered (#functionId, #expectedClass)"() {
        given:
        FramesModelRegistry registry = FramesModelRegistry.defaultRegistry();

        when:
        def optionalClass = registry.responseClass((byte) functionId);

        then:
        optionalClass.isPresent();
        optionalClass.get() == expectedClass;

        where:
        functionId  | expectedClass
        0x07        | GetCapabilitiesResponse.class
        0x15        | GetVersionResponse.class
        0x13        | SendDataResponse.class
    }
}
