package com.rposcro.jwavez.serial.frames;

import com.rposcro.jwavez.serial.enums.SerialCommand;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CallbackFrameModel {

    SerialCommand function();
}
