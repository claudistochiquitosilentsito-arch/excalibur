package com.excalibur.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

import com.excalibur.ExcaliburMod;
import com.excalibur.client.anim.ExcaliburAnimations;

/**
 * Animación de primera persona.
 *
 * En 26.3 el método es:
 * submitArmWithItem(PlayerRenderState, FirstPersonHandsAndItemsRenderState, float partialTicks,
 *                   float pitch, InteractionHand hand, float attack, ItemStack item,
 *                   float inverseArmHeight, PoseStack, SubmitNodeCollector, int lightCoords)
 *
 * 1) Al entrar, si el objeto es Excalibur, guardamos el progreso del ataque y se lo
 *    quitamos a vanilla (attack = 0) para que no haga su golpe por defecto.
 * 2) Justo después del primer pushPose() aplicamos nuestra transformación, así el
 *    popPose() de vanilla la deshace al terminar.
 */
@Mixin(FirstPersonHandsAndItemsRenderer.class)
public abstract class FirstPersonSwingMixin {
	@Unique
	private static float excalibur$swing = -1f;

	@Unique
	private static HumanoidArm excalibur$arm = HumanoidArm.RIGHT;

	@ModifyVariable(method = "submitArmWithItem", at = @At("HEAD"), argsOnly = true, index = 6)
	private float excalibur$captureSwing(float attack,
			@Local(argsOnly = true) ItemStack item,
			@Local(argsOnly = true) InteractionHand hand) {
		excalibur$swing = -1f;
		if (hand == InteractionHand.MAIN_HAND && attack > 0f && item.is(ExcaliburMod.EXCALIBUR)) {
			Minecraft mc = Minecraft.getInstance();
			excalibur$arm = mc.player != null ? mc.player.getMainArm() : HumanoidArm.RIGHT;
			excalibur$swing = attack;
			return 0f;
		}
		return attack;
	}

	@Inject(method = "submitArmWithItem",
			at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V",
					shift = At.Shift.AFTER, ordinal = 0),
			require = 0)
	private void excalibur$applySwing(CallbackInfo ci, @Local(argsOnly = true) PoseStack poseStack) {
		if (excalibur$swing >= 0f) {
			ExcaliburAnimations.applyFirstPerson(poseStack, excalibur$arm, excalibur$swing);
			excalibur$swing = -1f;
		}
	}
}
