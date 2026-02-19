package com.rposcro.jwavez.tools.shell.models;

import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.classes.GenericDeviceClass;
import com.rposcro.jwavez.core.classes.SpecificDeviceClass;
import com.rposcro.jwavez.core.commands.supported.multichannel.MultiChannelCapabilityReport;
import com.rposcro.jwavez.core.commands.supported.multichannel.MultiChannelEndPointReport;
import lombok.Builder;

@Builder
public class NodeMultiChannelInformation {

    private MultiChannelEndPointReport endPointReport;
    private MultiChannelCapabilityReport[] capabilityReports;

    public int getEndPointsCount() {
        return endPointReport.getEndPointsCount() & 0xff;
    }

    public int getAggregatedEndPointsCount() {
        return endPointReport.getAggregatedEndPointsCount() & 0xff;
    }

    public boolean isEndPointsCountDynamic() {
        return endPointReport.isEndPointsCountDynamic();
    }

    public boolean isEndPointsCapabilitiesIdentical() {
        return endPointReport.isEndPointsCapabilitiesIdentical();
    }

    public boolean isEndPointDynamic(int endPointNumber) {
        return capabilityReports[endPointNumber].isEndPointDynamic();
    }

    public GenericDeviceClass getEndPointGenericClass(int endPointNumber) {
        return capabilityReports[endPointNumber].getDecodedGenericDeviceClass();
    }

    public SpecificDeviceClass getEndPointSpecificClass(int endPointNumber) {
        return capabilityReports[endPointNumber].getDecodedSpecificDeviceClass();
    }

    public CommandClass[] getEndPointCommandClasses(int endPointNumber) {
        return capabilityReports[endPointNumber].getDecodedCommandClasses();
    }
}
