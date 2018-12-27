package com.rposcro.zwave.samples;

import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.interceptors.ApplicationUpdateCatcher;
import com.rposcro.jwavez.serial.frame.requests.GetInitDataRequestFrame;
import com.rposcro.jwavez.serial.frame.requests.RequestNodeInfoRequestFrame;
import com.rposcro.jwavez.serial.frame.responses.GetInitDataResponseFrame;
import com.rposcro.jwavez.serial.frame.responses.RequestNodeInfoResponseFrame;
import com.rposcro.jwavez.serial.transactions.TransactionResult;
import com.rposcro.jwavez.serial.transactions.TransactionStatus;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * First it learns list of known nodes from the controller. Next, for each of found node id,
 * it requests node information callbacks. Finally awaits 30 seconds for callbacks.
 *
 * com.rposcro.jwavez.serial.debug.ApplicationUpdateCatcher is used to intercept node info
 * callback updates.
 */
@Slf4j
public class IncludedNodesInfoTest  extends AbstractExample {

  public IncludedNodesInfoTest() {
    super("/dev/cu.usbmodem1411");
    this.channel.addInboundFrameInterceptor(new ApplicationUpdateCatcher());
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
      nodesInfo.append(String.format("%02X, ", nodeId));
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

  public static void main(String[] args) throws Exception {
    IncludedNodesInfoTest test = new IncludedNodesInfoTest();
    for (NodeId nodeId : test.findIncludedNodes()) {
      test.requestNodeInfo(nodeId);
    }
    Thread.sleep(30000);
    System.exit(0);
  }
}
