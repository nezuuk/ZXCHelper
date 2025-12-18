package ru.emrass.zxchelper.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Team;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.emrass.zxchelper.ZXCHelper;
import ru.emrass.zxchelper.features.impl.GlowHighlightFeature;


@Mixin(Entity.class)
public abstract class EntityGlowMixin {

    @Inject(method = "isGlowing", at = @At("RETURN"), cancellable = true)
    private void zxchelper$forceGlow(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.world == null) return;

        ZXCHelper mod = ZXCHelper.getInstance();
        if (mod == null) return;

        GlowHighlightFeature feature = mod.getGlowHighlightFeature();
        if (feature == null || !feature.isEnabled()) return;

        Entity self = (Entity) (Object) this;
        if (!(self instanceof PlayerEntity player)) return;
        if (player == mc.player) return;
        if(!feature.isRealPlayer(player)) return;



        cir.setReturnValue(true);
    }
}