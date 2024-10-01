package org.crychicteam.scout.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public class ScoutConfig {
	public static class Common {
		public final ForgeConfigSpec.BooleanValue allowShulkers;
		public final ForgeConfigSpec.BooleanValue useArrows;

		Common(ForgeConfigSpec.Builder builder) {
			builder.comment("Scout Mod Configuration");
			builder.push("features");

			allowShulkers = builder
					.comment("Allow shulker boxes to be placed in bags. Bags are already blacklisted from shulker boxes with no toggle.")
					.define("allow_shulkers", true);

			useArrows = builder
					.comment("Allow bags to act as a quiver and pull arrows.")
					.define("use_arrows", true);

			builder.pop();
		}
	}

	public static final ForgeConfigSpec COMMON_SPEC;
	public static final Common COMMON;

	static {
		final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
		COMMON_SPEC = specPair.getRight();
		COMMON = specPair.getLeft();
	}

	public static void register(ModLoadingContext context) {
		context.registerConfig(ModConfig.Type.COMMON, COMMON_SPEC);
	}

	public static boolean allowShulkers() {
		return COMMON.allowShulkers.get();
	}

	public static boolean useArrows() {
		return COMMON.useArrows.get();
	}
}