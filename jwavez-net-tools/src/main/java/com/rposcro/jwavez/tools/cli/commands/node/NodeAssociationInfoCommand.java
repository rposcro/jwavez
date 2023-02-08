package com.rposcro.jwavez.tools.cli.commands.node;

import static com.rposcro.jwavez.core.commands.types.AssociationCommandType.ASSOCIATION_GROUPINGS_REPORT;

import com.rposcro.jwavez.core.commands.controlled.builders.association.AssociationCommandBuilder;
import com.rposcro.jwavez.core.commands.supported.association.AssociationGroupingsReport;
import com.rposcro.jwavez.core.commands.supported.association.AssociationReport;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.frames.requests.SendDataRequest;
import com.rposcro.jwavez.tools.cli.ZWaveCLI;
import com.rposcro.jwavez.tools.cli.exceptions.CommandOptionsException;
import com.rposcro.jwavez.tools.cli.options.node.DefaultNodeBasedOptions;
import com.rposcro.jwavez.tools.cli.utils.ProcedureUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NodeAssociationInfoCommand extends AbstractNodeAssociationCommand {

  private DefaultNodeBasedOptions options;
  private AssociationCommandBuilder commandBuilder;

  @Override
  public void configure(String[] args) throws CommandOptionsException {
    options = new DefaultNodeBasedOptions(args);
    commandBuilder = new AssociationCommandBuilder();
  }

  @Override
  public void execute() {
    System.out.println("Requesting node association information...");
    ProcedureUtil.executeProcedure(this::runAssociationFetch);
    System.out.println("Node association fetch finished");
  }

  private void runAssociationFetch() throws SerialException {
    connect(options);
    List<AssociationReport> reports = collectReports();
    printReport(reports);
  }

  private void printReport(List<AssociationReport> reports) {
    System.out.println();
    reports.stream().forEachOrdered(this::printAssociationReport);
  }

  private List<AssociationReport> collectReports() {
    int groupsCount;
    try {
      groupsCount = readGroupingsCount();
    } catch(SerialException e) {
      System.out.printf("Failed to read groupings count: %s\n", e.getMessage());
      return Collections.emptyList();
    }

    List<AssociationReport> reports = new ArrayList<>(groupsCount);
    for (int groupIdx = 1; groupIdx <= groupsCount; groupIdx++) {
      try {
        System.out.printf("Checking association group %s...\n", groupIdx);
        reports.add(readGroupAssociations(options.getNodeId(), groupIdx, options.getTimeout()));
      } catch(SerialException e) {
        System.out.printf("Failed to read group %s: %s\n", groupIdx, e.getMessage());
      }
    }
    return reports;
  }

  private int readGroupingsCount() throws SerialException {
    System.out.println("Checking association groups availabilities...");
    AssociationGroupingsReport report = requestApplicationCommand(
        SendDataRequest.createSendDataRequest(
            options.getNodeId(),
            commandBuilder.v1().buildGetSupportedGroupingsCommand(),
            nextFlowId()),
        ASSOCIATION_GROUPINGS_REPORT,
        options.getTimeout());
    System.out.println("Available groups count: " + report.getGroupsCount());
    return report.getGroupsCount();
  }

  public static void main(String... args) throws Exception {
    ZWaveCLI.main("node", "association", "info", "-d", "/dev/tty.usbmodem1421", "-n", "3");
  }
}
