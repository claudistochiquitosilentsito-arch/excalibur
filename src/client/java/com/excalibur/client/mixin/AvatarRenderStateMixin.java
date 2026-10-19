package com.excalibur.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.HumanoidArm;

import com.excalibur.client.anim.ExcaliburRenderState;

@Mixin(AvatarRenderState.class)
public class AvatarRenderStateMixin implements ExcaliburRenderState {
	@Unique
	private boolean excalibur$holding;

	@Unique
	private HumanoidArm excalibur$arm = HumanoidArm.RIGHT;

	@Override
	public boolean excalibur$isHolding() {
		return excalibur$holding;
	}

	@Override
	public void excalibur$setHolding(boolean holding) {
		this.excalibur$holding = holding;
	}

	@Override
	public HumanoidArm excalibur$getArm() {
		return excalibur$arm;
	}

	@Override
	public void excalibur$setArm(HumanoidArm arm) {
		this.excalibur$arm = arm;
	}
}
