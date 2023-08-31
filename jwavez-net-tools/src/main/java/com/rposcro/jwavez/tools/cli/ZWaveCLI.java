package com.rposcro.jwavez.tools.cli;

import com.rposcro.jwavez.tools.cli.controller.CommandController;

import java.io.InputStream;
import java.net.URL;
import java.util.jar.Manifest;

public class ZWaveCLI {

    private static final String APP_HEAD_LINE;

    static {
        String resource = "/" + ZWaveCLI.class.getName().replace(".", "/") + ".class";
        String fullPath = ZWaveCLI.class.getResource(resource).toString();
        String archivePath = fullPath.substring(0, fullPath.length() - resource.length());
        if (archivePath.endsWith("\\WEB-INF\\classes") || archivePath.endsWith("/WEB-INF/classes")) {
            archivePath = archivePath.substring(0, archivePath.length() - "/WEB-INF/classes".length()); // Required for wars
        }

        String version = "";
        try (InputStream input = new URL(archivePath + "/META-INF/MANIFEST.MF").openStream()) {
            Manifest manifest = new Manifest(input);
            version = manifest.getMainAttributes().getValue("Implementation-Version");
        } catch (Exception e) {
            System.err.println("Loading MANIFEST failed!");
            System.err.println(e.getMessage());
        }

        APP_HEAD_LINE = "JWaveZ Network Tool " + version;
    }

    public static void main(String... args) {
        System.out.println(APP_HEAD_LINE);
        CommandController controller = new CommandController();
        int exitCode = controller.executeCommand(args);
        System.exit(exitCode);
    }
}
