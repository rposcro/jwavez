package com.rposcro.jwavez.serial.probe.frame

import com.rposcro.jwavez.serial.probe.frame.callbacks.AddNodeToNetworkCallbackFrame
import com.rposcro.jwavez.serial.probe.frame.callbacks.ApplicationCommandHandlerCallbackFrame
import com.rposcro.jwavez.serial.probe.frame.callbacks.ApplicationUpdateCallbackFrame
import com.rposcro.jwavez.serial.probe.frame.callbacks.RemoveNodeFromNetworkCallbackFrame
import com.rposcro.jwavez.serial.probe.frame.callbacks.SendDataCallbackFrame
import com.rposcro.jwavez.serial.probe.frame.requests.GetCapabilitiesRequestFrame
import com.rposcro.jwavez.serial.probe.frame.requests.GetVersionRequestFrame
import com.rposcro.jwavez.serial.probe.frame.requests.SendDataRequestFrame
import com.rposcro.jwavez.serial.probe.frame.responses.GetCapabilitiesResponseFrame
import com.rposcro.jwavez.serial.probe.frame.responses.GetVersionResponseFrame
import com.rposcro.jwavez.serial.probe.frame.responses.SendDataResponseFrame
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class SOFFrameRegistrySpec extends Specification {

    def "Callbacks are registered (#functionId, #expectedClass)"() {
        given:
        SOFFrameRegistry registry = SOFFrameRegistry.defaultRegistry();

        when:
        def optionalClass = registry.callbackClass((byte) functionId);

        then:
        optionalClass.isPresent();
        optionalClass.get() == expectedClass;

        where:
        functionId  | expectedClass
        0x4a        | AddNodeToNetworkCallbackFrame.class
        0x04        | ApplicationCommandHandlerCallbackFrame.class
        0x49        | ApplicationUpdateCallbackFrame.class
        0x4b        | RemoveNodeFromNetworkCallbackFrame.class
        0x13        | SendDataCallbackFrame.class
    }

    def "Responses are registered (#functionId, #expectedClass)"() {
        given:
        SOFFrameRegistry registry = SOFFrameRegistry.defaultRegistry();

        when:
        def optionalClass = registry.responseClass((byte) functionId);

        then:
        optionalClass.isPresent();
        optionalClass.get() == expectedClass;

        where:
        functionId  | expectedClass
        0x07        | GetCapabilitiesResponseFrame.class
        0x15        | GetVersionResponseFrame.class
        0x13        | SendDataResponseFrame.class
    }

    def "Requests are registered (#functionId, #expectedClass)"() {
        given:
        SOFFrameRegistry registry = SOFFrameRegistry.defaultRegistry();

        when:
        def optionalClass = registry.requestClass((byte) functionId);

        then:
        optionalClass.isPresent();
        optionalClass.get() == expectedClass;

        where:
        functionId  | expectedClass
        0x07        | GetCapabilitiesRequestFrame.class
        0x15        | GetVersionRequestFrame.class
        0x13        | SendDataRequestFrame.class
    }
}
