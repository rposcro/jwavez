package com.rposcro.jwavez.serial.controllers.helpers;

import com.rposcro.jwavez.serial.controllers.inclusion.AddNodeToNetworkFlowState;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletionException;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TransactionKeeperTest {

    private StateCaptor stateCaptor;
    private TransactionKeeper<AddNodeToNetworkFlowState> keeper;

    @BeforeEach
    public void setup() {
        this.stateCaptor = new StateCaptor();
        this.keeper = new TransactionKeeper<>();
        this.keeper.setStateChangeListener(stateCaptor);
    }

    @Test
    public void initialStateIsCorrect() {
        assertNull(keeper.getState());
        assertTrue(keeper.getTransitRequest().isEmpty());
        assertStates(false, false, false, false);
    }

    @Test
    public void stateAfterResetIsCorrect() {
        keeper.cancel();
        keeper.reset();

        assertNull(keeper.getState());
        assertTrue(keeper.getTransitRequest().isEmpty());
        assertStates(false, false, false, false);
    }

    @Test
    public void isCancelled() {
        keeper.cancel();
        assertStates(false, true, false, true);
    }

    @Test
    public void isSuccessful() {
        keeper.complete();
        assertStates(true, false, false, true);
    }

    @Test
    public void isFailed() {
        keeper.fail();
        assertStates(false, false, true, true);
    }

    @Test
    public void stateIsTransitedWithoutNextRequest() {
        keeper.transit(AddNodeToNetworkFlowState.WAITING_FOR_NODE);

        assertStates(false, false, false, false);
        assertEquals(AddNodeToNetworkFlowState.WAITING_FOR_NODE, keeper.getState());
        assertEquals(AddNodeToNetworkFlowState.WAITING_FOR_NODE, stateCaptor.lastCapturedState);
        assertTrue(keeper.getTransitRequest().isEmpty());
    }

    @Test
    public void stateIsTransitedWithNextRequest() {
        final SerialRequest expectedNextRequest = SerialRequest.builder().build();
        keeper.transitAndSchedule(AddNodeToNetworkFlowState.WAITING_FOR_NODE, expectedNextRequest);

        assertStates(false, false, false, false);
        assertEquals(AddNodeToNetworkFlowState.WAITING_FOR_NODE, keeper.getState());
        assertEquals(AddNodeToNetworkFlowState.WAITING_FOR_NODE, stateCaptor.lastCapturedState);
        assertEquals(expectedNextRequest, keeper.getTransitRequest().get());
    }

    @Test
    public void exceptionThrownWhenTransitionNotAllowed() {
        keeper.transit(AddNodeToNetworkFlowState.WAITING_FOR_NODE);
        keeper.fail();
        keeper.transit(AddNodeToNetworkFlowState.NODE_FOUND);

        assertStates(false, false, true, true);
        assertEquals(AddNodeToNetworkFlowState.WAITING_FOR_NODE, keeper.getState());
        assertEquals(AddNodeToNetworkFlowState.WAITING_FOR_NODE, stateCaptor.lastCapturedState);
        assertThrows(CompletionException.class, () -> keeper.getTransitRequest());
    }

    private void assertStates(boolean successful, boolean cancelled, boolean failed, boolean stopped) {
        assertEquals(successful, keeper.isSuccessful());
        assertEquals(cancelled, keeper.isCancelled());
        assertEquals(failed, keeper.isFailed());
        assertEquals(stopped, keeper.isStopped());
    }

    private class StateCaptor implements Consumer<AddNodeToNetworkFlowState> {

        private AddNodeToNetworkFlowState lastCapturedState;

        @Override
        public void accept(AddNodeToNetworkFlowState addNodeToNetworkFlowState) {
            this.lastCapturedState = addNodeToNetworkFlowState;
        }
    }
}
