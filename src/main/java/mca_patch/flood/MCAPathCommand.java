package mca_patch.flood;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public final class MCAPathCommand {
    private MCAPathCommand() {
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, net.minecraft.command.CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("mcapath")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("stats").executes(context -> stats(context.getSource())))
                .then(CommandManager.literal("reset").executes(context -> reset(context.getSource())))
                .then(CommandManager.literal("reload").executes(context -> reload(context.getSource()))));
    }

    private static int stats(ServerCommandSource source) {
        PathStats.Snapshot stats = PathStats.snapshot();
        PatchConfig.Values config = PatchConfig.current();
        source.sendFeedback(() -> Text.literal("MCA path patch: mode=" + config.mode + ", instrumentation=" + config.instrumentation), false);
        source.sendFeedback(() -> Text.literal("searches=" + stats.pathSearches()
                + ", candidates=" + stats.candidateClassifications()
                + ", landNodes=" + stats.landNodeClassifications()
                + ", neighborChecks=" + stats.neighborHazardChecks()
                + ", collisions=" + stats.collisionChecks()), false);
        source.sendFeedback(() -> Text.literal("measuredTicks=" + stats.measuredTicks()
                + ", maxSearches/tick=" + stats.maxTickPathSearches()
                + ", maxCandidates/tick=" + stats.maxTickCandidateClassifications()), false);
        return 1;
    }

    private static int reset(ServerCommandSource source) {
        PathStats.reset();
        source.sendFeedback(() -> Text.literal("MCA path statistics reset."), false);
        return 1;
    }

    private static int reload(ServerCommandSource source) {
        PatchConfig.load();
        source.sendFeedback(() -> Text.literal("MCA path patch config reloaded. New navigation mode applies to newly created MCA villagers."), false);
        return 1;
    }
}
