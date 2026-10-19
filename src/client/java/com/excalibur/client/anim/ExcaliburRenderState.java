package com.excalibur.client.anim;

import net.minecraft.world.entity.HumanoidArm;

/** Datos extra que añadimos al estado de render de cada jugador (via mixin). */
public interface ExcaliburRenderState {
	boolean excalibur$isHolding();

	void excalibur$setHolding(boolean holding);

	HumanoidArm excalibur$getArm();

	void excalibur$setArm(HumanoidArm arm);
}
