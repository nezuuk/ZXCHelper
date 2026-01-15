package ru.emrass.zxchelper.config;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class ZXCHelperConfig {
    private Set<String> friends = new HashSet<>();
    private String lastInstalledVersion;
    private boolean isActivePrefixChat = true;
    private int pingColor = 0x00FF00;
    private String licenseKey = "";
    private boolean showStatus = false;
    private List<String> chatWheelMessages = new ArrayList<>(List.of(
            "Test message 1",
            "Test message 2",
            "Sexy drill",
            "Zed pidor",
            "Отсоси",
            "Назад",
            "HAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
    ));
    private List<String> soundWheelSounds = new ArrayList<>();
    private String selectedArrowSkin = "default";
    private String selectedEnemySkin = "default";
}
