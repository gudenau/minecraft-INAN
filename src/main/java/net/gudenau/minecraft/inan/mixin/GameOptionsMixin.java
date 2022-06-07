package net.gudenau.minecraft.inan.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.gudenau.minecraft.inan.INAN;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Environment(EnvType.CLIENT)
@Mixin(GameOptions.class)
public abstract class GameOptionsMixin {
	@Shadow public boolean skipMultiplayerWarning;
	@Shadow @Final private SimpleOption<Boolean> autoJump;
	@Shadow public boolean joinedFirstServer;
	
	@Inject(
		method = "<init>",
		at = @At("TAIL")
	)
	private void init(MinecraftClient client, File optionsFile, CallbackInfo ci){
		if(INAN.CONFIG_DISABLE_AUTO_AUTO_JUMP) {
			autoJump.setValue(false);
		}
		if(INAN.CONFIG_DISABLE_MP_NAG) {
			skipMultiplayerWarning = true;
		}
		if(INAN.CONFIG_DISABLE_INTERACTION_NAG) {
			joinedFirstServer = true;
		}
	}
}
