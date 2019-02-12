package com.rposcro.jwavez.samples;

import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.probe.frame.requests.GetInitDataRequestFrame;
import com.rposcro.jwavez.serial.probe.frame.requests.MemoryGetIdRequestFrame;
import com.rposcro.jwavez.serial.probe.frame.requests.RequestNodeInfoRequestFrame;
import com.rposcro.jwavez.serial.probe.frame.responses.GetInitDataResponseFrame;
import com.rposcro.jwavez.serial.probe.frame.responses.MemoryGetIdResponseFrame;
import com.rposcro.jwavez.serial.probe.frame.responses.RequestNodeInfoResponseFrame;
import com.rposcro.jwavez.serial.probe.interceptors.ApplicationUpdateLogger;
import com.rposcro.jwavez.serial.probe.transactions.TransactionResult;
import com.rposcro.jwavez.serial.probe.transactions.TransactionStatus;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * First it learns list of known nodes from the controller. Next, for each of found node id,
 * it requests node information callbacks. Finally awaits 30 seconds for callbacks.
 *
 * com.rposcro.jwavez.serial.debug.ApplicationUpdateLogger is used to intercept node info
 * callback updates.
 */
@Slf4j
public class IncludedNodesInfoTest  extends AbstractExample {

  public IncludedNodesInfoTest() {
    super("/dev/cu.usbmodem14211", new ApplicationUpdateLogger());
  }

  private List<NodeId> findIncludedNodes() throws Exception {
    log.info("Learning included nodes ...");
    TransactionResult<GetInitDataResponseFrame> result = channel.sendFrameWithResponseAndWait(new GetInitDataRequestFrame());

    if (result.getStatus() != TransactionStatus.Completed) {
        log.error("Failed to obtain nodes list!");
        return null;
    }

    StringBuffer nodesInfo = new StringBuffer("Found node ids: ");
    List<NodeId> nodes = result.getResult().getNodeList();
    nodes.stream().forEach(nodeId -> {
      nodesInfo.append(String.format("%02x, ", nodeId.getId()));
    });
    log.info(nodesInfo.toString());

    return nodes;
  }

  private void requestNodeInfo(NodeId nodeId) throws Exception {
    log.info("Learning about node {} ...", nodeId);
    TransactionResult<RequestNodeInfoResponseFrame> result = channel.sendFrameWithResponseAndWait(new RequestNodeInfoRequestFrame(nodeId));
    log.info("Transaction status: {}", result.getStatus());

    if (result.getStatus() == TransactionStatus.Completed) {
      log.info("Request accepted, awaiting callback");
    }
  }

  private NodeId findOutControllerNodeId() throws Exception {
    TransactionResult<MemoryGetIdResponseFrame> result = channel.sendFrameWithResponseAndWait(new MemoryGetIdRequestFrame());
    if (result.getStatus() != TransactionStatus.Completed) {
      throw new RuntimeException("Failed to obtain controller's id!");
    }
    return result.getResult().getNodeId();
  }

  public static void main(String[] args) throws Exception {
    try {
      IncludedNodesInfoTest test = new IncludedNodesInfoTest();
      NodeId controllerId = test.findOutControllerNodeId();
      for (NodeId nodeId : test.findIncludedNodes()) {
        if (!nodeId.equals(controllerId)) {
          test.requestNodeInfo(nodeId);
        }
      }
      Thread.sleep(30000);
    } catch(Exception e) {
      e.printStackTrace();
    } finally {
      System.exit(0);
    }
  }
}
