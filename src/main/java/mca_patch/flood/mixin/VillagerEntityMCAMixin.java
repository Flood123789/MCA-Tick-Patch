package mca_patch.flood.mixin;

import mca_patch.flood.PatchConfig;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "fabric.net.mca.entity.VillagerEntityMCA")
public abstract class VillagerEntityMCAMixin {
    @Inject(method = {"createNavigation", "method_5965"}, at = @At("HEAD"), cancellable = true, remap = false)
    private void mcaTickPatch$useVanillaNavigation(World world, CallbackInfoReturnable<EntityNavigation> callback) {
        if (PatchConfig.current().mode == PatchConfig.NavigationMode.VANILLA) {
            callback.setReturnValue(new MobNavigation((MobEntity) (Object) this, world));
        }
    }
}
