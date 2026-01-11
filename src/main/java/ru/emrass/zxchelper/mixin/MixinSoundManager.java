package ru.emrass.zxchelper.mixin;

import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundManager.class)
public class MixinSoundManager {

    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    public void getExternalSound(Identifier id, CallbackInfoReturnable<WeightedSoundSet> cir) {
        if (id.getNamespace().equals("zxchelper") && id.getPath().startsWith("external/")) {

            Sound sound = new Sound(
                    id.toString(),
                    ConstantFloatProvider.create(1.0f),
                    ConstantFloatProvider.create(1.0f),
                    1,
                    Sound.RegistrationType.FILE,
                    true,
                    false,
                    16
            );

            WeightedSoundSet soundSet = new WeightedSoundSet(id, null);
            soundSet.add(sound);

            cir.setReturnValue(soundSet);
        }
    }
}