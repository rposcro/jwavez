package com.rposcro.jwavez.core.commands.enums;

import com.rposcro.jwavez.core.enums.CommandClass;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandTypeEnum {

  CommandClass commandClass();
}
