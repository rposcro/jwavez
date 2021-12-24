package com.rposcro.jwavez.core.commands.supported.binaryswitch;

import com.rposcro.jwavez.core.commands.types.SwitchBinaryCommandType;
import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.classes.CommandClassVersion;
import com.rposcro.jwavez.core.exceptions.CommandNotSupportedException;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.core.utils.ImmutableBuffer;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class BinarySwitchReport extends ZWaveSupportedCommand<SwitchBinaryCommandType> {

  private CommandClassVersion version;
  private short value;
  private short targetValue;
  private short duration;

  public BinarySwitchReport(ImmutableBuffer payload, NodeId sourceNodeId) {
    super(SwitchBinaryCommandType.BINARY_SWITCH_REPORT, sourceNodeId);
    version = recognizeVersion(payload);
    payload.skip(2);
    value = payload.nextUnsignedByte();
    if (CommandClassVersion.V2 == version) {
      targetValue = payload.nextUnsignedByte();
      duration = payload.nextUnsignedByte();
    }
  }

  private CommandClassVersion recognizeVersion(ImmutableBuffer payload) {
    int length = payload.getLength();
    if (length == 3) {
      return CommandClassVersion.V1;
    } else if (length == 5) {
      return CommandClassVersion.V2;
    } else {
      throw new CommandNotSupportedException(
              "Unrecognized BINARY_SWITCH_REPORT command length: " + length,
              CommandClass.CMD_CLASS_SWITCH_BINARY,
              SwitchBinaryCommandType.BINARY_SWITCH_REPORT
      );
    }
  }
}
