package com.excalibur.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;

import com.excalibur.ExcaliburMod;
import com.excalibur.client.anim.ExcaliburRenderState;

/** Copia al estado de render si el jugador lleva Excalibur en la mano principal. */
@Mixin(AvatarRenderer.class)
public class AvatarRendererMixin {
	@Inject(
			method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V",
			at = @At("RETURN"))
	private void excalibur$extract(Avatar avatar, AvatarRenderState state, float partialTick, CallbackInfo ci) {
		ExcaliburRenderState data = (ExcaliburRenderState) state;
		data.excalibur$setHolding(avatar.getMainHandItem().is(ExcaliburMod.EXCALIBUR));
		data.excalibur$setArm(avatar.getMainArm());
	}
}
