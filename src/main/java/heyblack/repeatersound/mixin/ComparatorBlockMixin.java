package heyblack.repeatersound.mixin;

import heyblack.repeatersound.RepeaterSound;
import heyblack.repeatersound.config.Config;
import heyblack.repeatersound.config.ConfigManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.ComparatorBlock;
import net.minecraft.block.enums.ComparatorMode;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import static net.minecraft.block.ComparatorBlock.MODE;

@Environment(value= EnvType.CLIENT)
@Mixin(ComparatorBlock.class)
public class ComparatorBlockMixin
{
    BlockState state;
    @Inject(method = "onUse", at = @At(value = "HEAD"))
    public void getState(BlockState s, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir)
    {
        state = s;
    }

    @ModifyArgs(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"))
    public void pitch(Args args)
    {
        Config config = RepeaterSound.getConfig();
        float basePitch = config.getBasePitch();
        float pitch = config.getRandomPitch() ?
                (float) (basePitch + (Math.random() - 0.5) * 0.25) :
                (state = state.cycle(MODE)).get(MODE) == ComparatorMode.SUBTRACT ? basePitch + 0.05f : basePitch;
        float volume = config.getVolume();
        args.set(5, pitch);
        args.set(4, volume);
    }
}
