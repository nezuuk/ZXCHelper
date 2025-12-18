package ru.emrass.zxchelper.config;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class ZXCHelperConfig {
    private Set<String> friends = new HashSet<>();

}
