package net.gudenau.minecraft.inan.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.gudenau.minecraft.inan.INAN;
import net.minecraft.client.tutorial.TutorialManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(TutorialManager.class)
public abstract class TutorialManagerMixin {
    @Inject(
        method = "createHandler",
        at = @At("HEAD"),
        cancellable = true
    )
    private void createHandler(CallbackInfo ci){
        if(INAN.CONFIG_DISABLE_TUTORIAL) {
            ci.cancel();
        }
    }
}
