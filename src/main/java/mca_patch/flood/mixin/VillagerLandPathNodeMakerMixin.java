package mca_patch.flood.mixin;

import mca_patch.flood.PathStats;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.chunk.ChunkCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "fabric.net.mca.entity.ai.pathfinder.VillagerLandPathNodeMaker")
public abstract class VillagerLandPathNodeMakerMixin {
    @Inject(method = {"init", "method_12"}, at = @At("HEAD"), remap = false)
    private void mcaTickPatch$countPathSearch(ChunkCache world, MobEntity entity, CallbackInfo callback) {
        if (PathStats.enabled()) {
            PathStats.pathSearchStarted();
        }
    }

    @Inject(method = "getExtendedNodeType", at = @At("HEAD"), remap = false)
    private void mcaTickPatch$countCandidate(CallbackInfoReturnable<?> callback) {
        if (PathStats.enabled()) {
            PathStats.candidateClassified();
        }
    }

    @Inject(method = "getLandNodeType", at = @At("HEAD"), remap = false)
    private static void mcaTickPatch$countLandNode(CallbackInfoReturnable<?> callback) {
        if (PathStats.enabled()) {
            PathStats.landNodeClassified();
        }
    }

    @Inject(method = "getNodeTypeFromNeighbors", at = @At("HEAD"), remap = false)
    private static void mcaTickPatch$countNeighborCheck(CallbackInfoReturnable<?> callback) {
        if (PathStats.enabled()) {
            PathStats.neighborHazardsChecked();
        }
    }

    @Inject(method = "checkBoxCollision", at = @At("HEAD"), remap = false)
    private void mcaTickPatch$countCollision(CallbackInfoReturnable<Boolean> callback) {
        if (PathStats.enabled()) {
            PathStats.collisionChecked();
        }
    }
}
