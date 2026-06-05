package com.rposcro.jwavez.tools.shell.formatters;

import com.rposcro.jwavez.tools.shell.models.NodeInformation;
import com.rposcro.jwavez.tools.shell.models.NodeProductInformation;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class NodeInformationFormatter {

    public String formatShortNodeInfo(NodeInformation nodeInformation) {
        return String.format("Node id: %s\nMemo: %s\n",
                nodeInformation.getNodeId(),
                nodeInformation.getNodeMemo());
    }

    public String formatVerboseNodeInfo(NodeInformation nodeInformation) {
        NodeProductInformation productInformation = nodeInformation.getProductInformation();
        return String.format("Node id: %s (0x%02X)\n"
                        + "Memo: %s\n"
                        + "\n"
                        + "Manufacturer id: %s (0x%04X)\n"
                        + "Product type id: %s (0x%04X)\n"
                        + "Product id: %s (0x%04X)\n"
                        + "\n"
                        + "ZWave library type: %s (0x%02X)\n"
                        + "ZWave protocol version: %s (0x%02X)\n"
                        + "ZWave protocol sub version: %s (0x%02X)\n"
                        + "Application version: %s (0x%02X)\n"
                        + "Application sub version: %s (0x%02X)\n"
                        + "\n"
                        + "Basic device class: %s\n"
                        + "Generic device class: %s\n"
                        + "Specific device class: %s\n"
                        + "\n"
                        + "Supported command classes: %s\n"
                , nodeInformation.getNodeId(), nodeInformation.getNodeId()
                , nodeInformation.getNodeMemo()
                , productInformation.getManufacturerId(), productInformation.getManufacturerId()
                , productInformation.getProductTypeId(), productInformation.getProductTypeId()
                , productInformation.getProductId(), productInformation.getProductId()
                , productInformation.getZWaveLibraryType(), productInformation.getZWaveLibraryType()
                , productInformation.getZWaveProtocolVersion(), productInformation.getZWaveProtocolVersion()
                , productInformation.getZWaveProtocolSubVersion(), productInformation.getZWaveProtocolSubVersion()
                , productInformation.getApplicationVersion(), productInformation.getApplicationVersion()
                , productInformation.getApplicationSubVersion(), productInformation.getApplicationSubVersion()
                , productInformation.getBasicDeviceClass()
                , productInformation.getGenericDeviceClass()
                , productInformation.getSpecificDeviceClass()
                , Stream.of(productInformation.getCommandClasses())
                        .map(cmdMeta ->
                                String.format("\n    %s(0x%02X) version %s", cmdMeta.getCommandClass(), cmdMeta.getCommandClass().getCode(), cmdMeta.getVersion())
                        )
                        .collect(Collectors.joining(", "))
        );
    }
}
