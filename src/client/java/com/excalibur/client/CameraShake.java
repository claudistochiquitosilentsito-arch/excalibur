package com.excalibur.client;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;

import com.excalibur.ExcaliburMod;

/**
 * Temblor de cámara para el jugador que tiene el efecto "Temblor".
 *
 * Cada tick se deshace el desplazamiento del tick anterior y se aplica uno nuevo
 * aleatorio, así la vista tiembla pero no se desvía de donde apuntaba el jugador.
 * La intensidad baja suavemente durante el último segundo del efecto.
 */
public final class CameraShake {
	/** Grados máximos de sacudida con el efecto a nivel I. */
	private static final float BASE_STRENGTH = 2.5F;

	private static final Random RANDOM = new Random();
	private static float appliedYaw;
	private static float appliedPitch;

	private CameraShake() {
	}

	public static void tick(Minecraft mc) {
		LocalPlayer player = mc.player;
		if (player == null) {
			appliedYaw = 0;
			appliedPitch = 0;
			return;
		}

		// 1) Quitar la sacudida del tick anterior
		if (appliedYaw != 0 || appliedPitch != 0) {
			player.setYRot(player.getYRot() - appliedYaw);
			player.setXRot(player.getXRot() - appliedPitch);
			appliedYaw = 0;
			appliedPitch = 0;
		}

		if (mc.isPaused()) {
			return;
		}

		MobEffectInstance effect = player.getEffect(ExcaliburMod.TREMOR);
		if (effect == null) {
			return;
		}

		// 2) Calcular intensidad (más fuerte con más nivel, se apaga al final)
		float fade = Mth.clamp(effect.getDuration() / 20.0F, 0.0F, 1.0F);
		float strength = BASE_STRENGTH * (effect.getAmplifier() + 1) * fade;
		if (strength <= 0.01F) {
			return;
		}

		float yaw = (RANDOM.nextFloat() * 2.0F - 1.0F) * strength;
		float pitch = (RANDOM.nextFloat() * 2.0F - 1.0F) * strength * 0.8F;

		// 3) Aplicar la nueva sacudida (respetando el límite vertical de ±90°)
		float oldPitch = player.getXRot();
		float newPitch = Mth.clamp(oldPitch + pitch, -90.0F, 90.0F);
		player.setYRot(player.getYRot() + yaw);
		player.setXRot(newPitch);
		appliedYaw = yaw;
		appliedPitch = newPitch - oldPitch;
	}
}
