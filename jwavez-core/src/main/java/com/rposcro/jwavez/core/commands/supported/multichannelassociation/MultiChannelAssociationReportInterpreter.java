package com.rposcro.jwavez.core.commands.supported.multichannelassociation;

import com.rposcro.jwavez.core.model.EndPointAddress;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@AllArgsConstructor(access = AccessLevel.MODULE)
public class MultiChannelAssociationReportInterpreter {

    private MultiChannelAssociationReport report;

    public List<Integer> nodeIdList() {
        byte[] nodeIds = report.getNodeIds();
        return IntStream.range(0, nodeIds.length)
                .map(idx -> nodeIds[idx] & 0xff)
                .sorted()
                .boxed()
                .collect(Collectors.toList());
    }

    public List<EndPointAddress> endPointAddressList() {
        byte[][] endpoints = report.getEndPointIds();
        List<EndPointAddress> addresses = new LinkedList<>();

        for (byte[] row: endpoints) {
            if ((row[1] & 0x80) == 0) {
                addresses.add(new EndPointAddress(row[0], row[1]));
            } else {
                for (int eId = 1, mask = 1; eId <= 7; eId++, mask <<= 1) {
                    if ((row[1] & mask) != 0) {
                        addresses.add(new EndPointAddress(row[0], (byte) eId));
                    }
                }
            }
        }

        Collections.sort(addresses);
        return addresses;
    }
}
