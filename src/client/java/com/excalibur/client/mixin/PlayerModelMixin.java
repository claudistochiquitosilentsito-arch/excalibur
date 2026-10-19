package com.excalibur.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;

import com.excalibur.client.anim.ExcaliburAnimations;
import com.excalibur.client.anim.ExcaliburRenderState;

/** Animación de tercera persona: sobreescribe la pose de brazos y torso durante el golpe. */
@Mixin(PlayerModel.class)
public abstract class PlayerModelMixin {
	@Inject(method = "setupAnim", at = @At("RETURN"))
	private void excalibur$setupAnim(AvatarRenderState state, CallbackInfo ci) {
		if (!(state instanceof ExcaliburRenderState data) || !data.excalibur$isHolding()) {
			return;
		}
		float progress = state.swingAnimation;
		if (progress <= 0f) {
			return;
		}
		HumanoidModel<?> model = (HumanoidModel<?>) (Object) this;
		ExcaliburAnimations.applyThirdPerson(model, data.excalibur$getArm(), progress);
	}
}
