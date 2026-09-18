package mca_patch.flood.mixin;

import mca_patch.flood.PathStats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Pathfinding counters for MCA 7.7.36+ (Minecraft 1.21.1).
 *
 * MCA 1.21.1 replaced fabric.net.mca...VillagerLandPathNodeMaker with
 * net.conczin.mca...MCAWalkNodeEvaluator, which extends vanilla LandPathNodeMaker and only
 * overrides part of it. Mapping from the 1.20.1 hooks:
 *   init (search started)            -> method_21 / getStart: runs once per path search
 *   getExtendedNodeType (candidate)  -> method_17 / getDefaultNodeType(PathContext, x, y, z)
 *   checkBoxCollision (collision)    -> hasExactBlockClearance(Box): MCA's own clearance test
 *   getLandNodeType, getNodeTypeFromNeighbors -> no MCA override any more (vanilla statics), not counted.
 *
 * Names are runtime names because the target is matched by string (@Pseudo, remap = false):
 * intermediary for methods overriding vanilla, MCA's own names for MCA's methods.
 * require = 0: these are diagnostics and must never crash the game if MCA changes again.
 */
@Pseudo
@Mixin(targets = "net.conczin.mca.entity.ai.navigation.MCAWalkNodeEvaluator")
public abstract class MCAWalkNodeEvaluatorMixin {
    @Inject(method = "method_21", at = @At("HEAD"), remap = false, require = 0)
    private void mcaTickPatch$countPathSearch(CallbackInfoReturnable<?> callback) {
        if (PathStats.enabled()) {
            PathStats.pathSearchStarted();
        }
    }

    @Inject(method = "method_17", at = @At("HEAD"), remap = false, require = 0)
    private void mcaTickPatch$countCandidate(CallbackInfoReturnable<?> callback) {
        if (PathStats.enabled()) {
            PathStats.candidateClassified();
        }
    }

    @Inject(method = "hasExactBlockClearance", at = @At("HEAD"), remap = false, require = 0)
    private void mcaTickPatch$countCollision(CallbackInfoReturnable<Boolean> callback) {
        if (PathStats.enabled()) {
            PathStats.collisionChecked();
        }
    }
}
