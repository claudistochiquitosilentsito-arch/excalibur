package com.excalibur.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * Efecto "Temblor". No hace nada en el servidor: el cliente del jugador afectado
 * lo detecta y hace temblar su cámara (ver CameraShake en el lado cliente).
 */
public class TremorEffect extends MobEffect {
	public TremorEffect() {
		super(MobEffectCategory.HARMFUL, 0xE8C547); // dorado
	}
}
