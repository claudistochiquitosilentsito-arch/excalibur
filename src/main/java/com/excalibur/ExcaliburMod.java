package com.excalibur;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ToolMaterial;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excalibur.effect.TremorEffect;

public class ExcaliburMod implements ModInitializer {
	public static final String MOD_ID = "excalibur";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	/** Duración del temblor al recibir un golpe (en ticks, 20 = 1 segundo). */
	public static final int TREMOR_DURATION_TICKS = 60;

	// ---------- Efecto "Temblor" ----------
	public static final Holder<MobEffect> TREMOR = Registry.registerForHolder(
			BuiltInRegistries.MOB_EFFECT, id("temblor"), new TremorEffect());

	// ---------- Espada Excalibur ----------
	public static final ResourceKey<Item> EXCALIBUR_KEY = ResourceKey.create(Registries.ITEM, id("excalibur"));

	public static final Item EXCALIBUR = Registry.register(BuiltInRegistries.ITEM, EXCALIBUR_KEY,
			new Item(new Item.Properties()
					.setId(EXCALIBUR_KEY)
					// material netherita, +5 de daño extra, velocidad de ataque como una espada normal
					.sword(ToolMaterial.NETHERITE, 5.0F, -2.4F)
					.fireResistant()
					.rarity(Rarity.EPIC)));

	@Override
	public void onInitialize() {
		// Añadir la espada a la pestaña de combate del modo creativo
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT).register(output -> output.accept(EXCALIBUR));

		// Cuando alguien recibe daño cuerpo a cuerpo de Excalibur, se le aplica "Temblor"
		ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamage, damageTaken, blocked) -> {
			if (blocked) {
				return;
			}
			if (source.getEntity() instanceof LivingEntity attacker
					&& source.getDirectEntity() == attacker
					&& attacker.getMainHandItem().is(EXCALIBUR)) {
				entity.addEffect(new MobEffectInstance(TREMOR, TREMOR_DURATION_TICKS, 0));
			}
		});

		LOGGER.info("Excalibur ha sido sacada de la piedra.");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
