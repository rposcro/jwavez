package com.rposcro.jwavez.commands.enums;

import com.rposcro.jwavez.enums.CommandClass;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandTypeEnum {

  CommandClass commandClass();
}
