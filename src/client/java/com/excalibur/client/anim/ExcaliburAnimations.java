package com.excalibur.client.anim;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

/**
 * Animación de tajo de Excalibur.
 *
 * El golpe tiene tres fases, en función del progreso del ataque (0 → 1):
 *   1. Carga   (0.00 – 0.22): la espada sube y se echa hacia atrás sobre el hombro.
 *   2. Tajo    (0.22 – 0.55): corte diagonal rápido de arriba-derecha a abajo-izquierda.
 *   3. Vuelta  (0.55 – 1.00): breve pausa y regreso suave a la posición normal.
 *
 * Todos los números de las tablas KEYS se pueden retocar para cambiar el estilo.
 */
public final class ExcaliburAnimations {
	private ExcaliburAnimations() {
	}

	// =====================================================================
	// PRIMERA PERSONA
	// =====================================================================
	// Cada fila: { tiempo, despl.X, despl.Y, despl.Z, rotX°, rotY°, rotZ° }
	// Valores para la mano derecha; para la izquierda se reflejan X, rotY y rotZ.
	private static final float[][] FP_KEYS = {
			{0.00f,   0.00f,  0.00f,  0.00f,    0f,   0f,   0f},
			{0.22f,   0.06f,  0.20f,  0.10f,   35f, -10f,  25f}, // carga
			{0.55f,  -0.45f, -0.22f, -0.15f,  -75f,  35f, -55f}, // fin del tajo
			{0.75f,  -0.40f, -0.20f, -0.12f,  -70f,  30f, -50f}, // pausa
			{1.00f,   0.00f,  0.00f,  0.00f,    0f,   0f,   0f},
	};

	/** Punto (en espacio de cámara) alrededor del cual gira el brazo: la posición de reposo de la mano. */
	private static final float PIVOT_X = 0.56f, PIVOT_Y = -0.52f, PIVOT_Z = -0.72f;

	public static void applyFirstPerson(PoseStack poseStack, HumanoidArm arm, float progress) {
		float[] k = sample(FP_KEYS, progress);
		float side = arm == HumanoidArm.RIGHT ? 1f : -1f;

		float px = PIVOT_X * side;
		poseStack.translate(px + k[0] * side, PIVOT_Y + k[1], PIVOT_Z + k[2]);
		poseStack.rotate(Axis.YP, Mth.DEG_TO_RAD * k[4] * side);
		poseStack.rotate(Axis.ZP, Mth.DEG_TO_RAD * k[5] * side);
		poseStack.rotate(Axis.XP, Mth.DEG_TO_RAD * k[3]);
		poseStack.translate(-px, -PIVOT_Y, -PIVOT_Z);
	}

	// =====================================================================
	// TERCERA PERSONA
	// =====================================================================
	// Cada fila: { tiempo, brazo.xRot, brazo.yRot, brazo.zRot, cuerpo.yRot, peso }
	// (radianes, brazo derecho). "peso" mezcla la pose con la animación vanilla
	// (0 = vanilla, 1 = pose de Excalibur).
	private static final float[][] TP_KEYS = {
			{0.00f,  -0.10f,  0.00f,  0.00f,  0.00f, 0f},
			{0.22f,  -2.90f,  0.35f,  0.45f,  0.30f, 1f}, // espada sobre la cabeza
			{0.55f,  -0.35f, -0.95f, -0.25f, -0.50f, 1f}, // tajo cruzando el cuerpo
			{0.75f,  -0.45f, -0.85f, -0.20f, -0.45f, 1f}, // pausa
			{1.00f,  -0.10f,  0.00f,  0.00f,  0.00f, 0f},
	};

	/** El otro brazo acompaña el golpe como si se agarrase la espada a dos manos. */
	private static final float OFF_ARM_FOLLOW = 0.6f;

	public static void applyThirdPerson(HumanoidModel<?> model, HumanoidArm arm, float progress) {
		float[] k = sample(TP_KEYS, progress);
		float side = arm == HumanoidArm.RIGHT ? 1f : -1f;
		float w = k[4];

		ModelPart main = arm == HumanoidArm.RIGHT ? model.rightArm : model.leftArm;
		ModelPart off = arm == HumanoidArm.RIGHT ? model.leftArm : model.rightArm;

		// Torso girando con el tajo
		model.body.yRot = Mth.lerp(w, model.body.yRot, k[3] * side);

		// Recolocar hombros según el giro del torso (igual que hace vanilla)
		float bodyYaw = model.body.yRot;
		model.rightArm.z = Mth.sin(bodyYaw) * 5.0F;
		model.rightArm.x = -Mth.cos(bodyYaw) * 5.0F;
		model.leftArm.z = -Mth.sin(bodyYaw) * 5.0F;
		model.leftArm.x = Mth.cos(bodyYaw) * 5.0F;

		// Brazo que sujeta la espada
		main.xRot = Mth.lerp(w, main.xRot, k[0]);
		main.yRot = Mth.lerp(w, main.yRot, k[1] * side + bodyYaw);
		main.zRot = Mth.lerp(w, main.zRot, k[2] * side);

		// Brazo libre: sigue parcialmente al principal y se cierra hacia el centro
		float fw = w * OFF_ARM_FOLLOW;
		off.xRot = Mth.lerp(fw, off.xRot, k[0]);
		off.yRot = Mth.lerp(fw, off.yRot, (k[1] + 0.6f) * side + bodyYaw);
		off.zRot = Mth.lerp(fw, off.zRot, -0.2f * side);
	}

	// =====================================================================
	// Interpolación entre fotogramas clave con suavizado (ease in-out)
	// =====================================================================
	private static float[] sample(float[][] keys, float t) {
		t = Mth.clamp(t, 0f, 1f);
		int n = keys[0].length - 1;
		float[] out = new float[n];
		for (int i = 0; i < keys.length - 1; i++) {
			float[] a = keys[i];
			float[] b = keys[i + 1];
			if (t <= b[0]) {
				float span = b[0] - a[0];
				float local = span <= 0f ? 1f : (t - a[0]) / span;
				float e = local * local * (3f - 2f * local); // smoothstep
				for (int j = 0; j < n; j++) {
					out[j] = Mth.lerp(e, a[j + 1], b[j + 1]);
				}
				return out;
			}
		}
		float[] last = keys[keys.length - 1];
		System.arraycopy(last, 1, out, 0, n);
		return out;
	}
}
