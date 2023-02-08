package com.rposcro.jwavez.tools.cli.utils;

import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.exceptions.SerialPortException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProcedureUtil {

    public static boolean executeProcedure(SerialProcedure procedure) {
        try {
            procedure.execute();
            return true;
        } catch (SerialPortException e) {
            log.info("Failed to connect to port", e);
            System.out.println("Failed to connect to port ...");
        } catch (SerialException e) {
            System.out.println("Command flow failed due to: " + e.getMessage());
            log.debug("Failure exception: ", e);
        }
        return false;
    }
}
