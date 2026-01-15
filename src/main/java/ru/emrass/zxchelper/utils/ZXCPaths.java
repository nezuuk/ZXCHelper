package ru.emrass.zxchelper.utils;

import net.fabricmc.loader.api.FabricLoader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ZXCPaths {

    public static final Path ROOT = FabricLoader.getInstance().getGameDir().resolve("zxc");

    public static final Path TOOLS = ROOT.resolve("tools");
    public static final Path SOUNDS = ROOT.resolve("sounds");
    public static final Path SCACHE = ROOT.resolve("scache");
    public static final Path SKINS = ROOT.resolve("skins");

    public static void init() {
        try {
            if (!Files.exists(ROOT)) Files.createDirectories(ROOT);
            if (!Files.exists(TOOLS)) Files.createDirectories(TOOLS);
            if (!Files.exists(SOUNDS)) Files.createDirectories(SOUNDS);
            if (!Files.exists(SCACHE)) Files.createDirectories(SCACHE);
            if (!Files.exists(SKINS)) Files.createDirectories(SKINS);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}