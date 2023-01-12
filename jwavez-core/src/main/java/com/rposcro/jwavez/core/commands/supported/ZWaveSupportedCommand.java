package com.rposcro.jwavez.core.commands.supported;

import com.rposcro.jwavez.core.classes.CommandClass;
import com.rposcro.jwavez.core.commands.types.CommandType;
import com.rposcro.jwavez.core.model.NodeId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ZWaveSupportedCommand<C extends CommandType> {

  protected static final int OFFSET_COMMAND_CLASS = 0;
  protected static final int OFFSET_COMMAND = 1;

  private C commandType;
  private NodeId sourceNodeId;

  public CommandClass getCommandClass() {
    return this.commandType.getCommandClass();
  }

  public String asNiceString() {
    return String.format("%s(%02x) srcNode(%02x)",
           commandType.name(), commandType.getCode(), sourceNodeId.getId()
    );
  }
}
