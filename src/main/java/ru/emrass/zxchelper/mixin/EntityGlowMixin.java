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

/**
 * Перехватываем Entity.isGlowing():
 * если включена GLOW-фича и сущность — «наш» игрок,
 * форсим isGlowing = true (контур включён), независимо от сервера.
 */
@Mixin(Entity.class)
public abstract class EntityGlowMixin {

    @Inject(method = "isGlowing", at = @At("RETURN"), cancellable = true)
    private void zxchelper$forceGlow(CallbackInfoReturnable<Boolean> cir) {
        // если ваниль уже считает сущность светящейся (эффект / сервер) — не трогаем
        if (cir.getReturnValue()) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.world == null) return;

        ZXCHelper mod = ZXCHelper.getInstance();
        if (mod == null) return;

        GlowHighlightFeature feature = mod.getGlowHighlightFeature();
        if (feature == null || !feature.isEnabled()) return;

        Entity self = (Entity) (Object) this;
        if (!(self instanceof PlayerEntity player)) return;
        if (player == mc.player) return; // себя не подсвечиваем
        if(!feature.isRealPlayer(player)) return;


        // здесь фактически то же самое, что ты делал в своём тесте,
        // только с нужными условиями
        cir.setReturnValue(true);
    }
}