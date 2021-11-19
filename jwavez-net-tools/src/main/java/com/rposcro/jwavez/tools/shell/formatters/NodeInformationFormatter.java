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
        return String.format("Node id: %s\n"
                        + "Memo: %s\n"
                        + "\n"
                        + "Manufacturer id: 0x%04X\n"
                        + "Product type id: 0x%04X\n"
                        + "Product id: 0x%04X\n"
                        + "\n"
                        + "ZWave library type: 0x%02X\n"
                        + "ZWave protocol version: 0x%02X\n"
                        + "ZWave protocol sub version: 0x%02X\n"
                        + "Application version: 0x%02X\n"
                        + "Application sub version: 0x%02X\n"
                        + "\n"
                        + "Basic device class: %s\n"
                        + "Generic device class: %s\n"
                        + "Specific device class: %s\n"
                        + "\n"
                        + "Supported command classes: %s\n"
                , nodeInformation.getNodeId()
                , nodeInformation.getNodeMemo()
                , productInformation.getManufacturerId()
                , productInformation.getProductTypeId()
                , productInformation.getProductId()
                , productInformation.getZWaveLibraryType()
                , productInformation.getZWaveProtocolVersion()
                , productInformation.getZWaveProtocolSubVersion()
                , productInformation.getApplicationVersion()
                , productInformation.getApplicationSubVersion()
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
