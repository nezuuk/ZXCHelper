package ru.emrass.zxchelper.features;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import ru.emrass.zxchelper.ZXCHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FeatureRegistry {

    private static final String KEY_CATEGORY_NAME = "ZXCHelper";

    @Getter
    private static final List<BaseFeature> features = new ArrayList<>();
    private static boolean initialized = false;

    public static void init() {
        if (initialized) return;
        initialized = true;

        registerKeyBindingsForExistingFeatures();
        callOnRegisteredForExisting();

        ClientTickEvents.END_CLIENT_TICK.register(FeatureRegistry::onClientTick);
    }

    public static void registerFeatures(BaseFeature... feats) {
        init();
        features.addAll(Arrays.asList(feats));

        if (initialized) {
            for (BaseFeature f : feats) {
                registerKeyBinding(f);
                f.onRegistered();
            }
        }
    }

    private static void registerKeyBindingsForExistingFeatures() {
        for (BaseFeature feature : features) {
            registerKeyBinding(feature);
        }
    }

    private static void registerKeyBinding(BaseFeature feature) {
        if (!feature.isKeybound()) return;

        feature.createKeyBinding(KEY_CATEGORY_NAME);
        KeyBindingHelper.registerKeyBinding(feature.getKeyBinding());

        log.info("[{}] Registered keybind for feature {}: {} (default={})",
                ZXCHelper.MOD_NAME,
                feature.getId(),
                feature.getDisplayName(),
                feature.getKeyBinding().getDefaultKey().getCode());
    }

    private static void callOnRegisteredForExisting() {
        for (BaseFeature feature : features) {
            feature.onRegistered();
        }
    }

    private static void onClientTick(MinecraftClient client) {
        if (client == null) return;

        for (BaseFeature feature : features) {
            if (feature.isKeybound() && feature.getKeyBinding() != null) {
                while (feature.getKeyBinding().wasPressed()) {
                    feature.onKeyPressed(client);
                }
            }

            feature.onClientTick(client);
        }
    }
}