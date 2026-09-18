package mca_patch.flood

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.loader.api.FabricLoader
import org.slf4j.LoggerFactory

object MCATickUsagePatch : ModInitializer {
    const val MOD_ID = "mca-tick-usage-patch"
    val logger = LoggerFactory.getLogger(MOD_ID)

	override fun onInitialize() {
		PatchConfig.load()
		CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, environment ->
			MCAPathCommand.register(dispatcher, registryAccess, environment)
		}
		ServerTickEvents.END_SERVER_TICK.register { PathStats.endServerTick() }
		verifyMCATargets()
		logger.info(
			"MCA Tick Usage Patch loaded: mode={}, instrumentation={}",
			PatchConfig.current().mode,
			PatchConfig.current().instrumentation
		)
	}

	private fun verifyMCATargets() {
		if (!FabricLoader.getInstance().isModLoaded("mca")) {
			logger.warn("MCA is not loaded; MCA pathfinding Mixins are inactive.")
			return
		}

		try {
			Class.forName("net.conczin.mca.entity.VillagerEntityMCA")
			Class.forName("net.conczin.mca.entity.ai.navigation.MCAWalkNodeEvaluator")
			logger.info("Found MCA 7.7.36+ (1.21.1) navigation classes; check earlier log lines for Mixin application warnings.")
		} catch (throwable: Throwable) {
			logger.error("Could not verify MCA pathfinding targets. This MCA version may be incompatible.", throwable)
		}
	}
}
