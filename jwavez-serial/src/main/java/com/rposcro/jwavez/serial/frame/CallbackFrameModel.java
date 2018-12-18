package com.rposcro.jwavez.serial.frame;

import com.rposcro.jwavez.serial.frame.contants.SerialCommand;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CallbackFrameModel {

  SerialCommand function();
}
