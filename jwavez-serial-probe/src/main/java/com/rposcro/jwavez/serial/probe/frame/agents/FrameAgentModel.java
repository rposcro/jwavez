package com.rposcro.jwavez.serial.probe.frame.agents;

import com.rposcro.jwavez.serial.probe.frame.constants.SerialCommand;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FrameAgentModel {

  SerialCommand command();
}
