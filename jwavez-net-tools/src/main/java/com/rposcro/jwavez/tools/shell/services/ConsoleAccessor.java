package com.rposcro.jwavez.tools.shell.services;

import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConsoleAccessor {

    @Autowired
    private LineReader lineReader;

    @Autowired
    private Terminal terminal;

    public String readLine(String prompt) {
        return lineReader.readLine(prompt);
    }

    public void flushLine(String line) {
        terminal.writer().println(line);
        terminal.writer().flush();
    }
}
