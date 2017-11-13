package betterwithmods.common.blocks.miniblocks;

import net.minecraftforge.common.model.IModelState;

import java.util.Optional;

public class Orientation {
    public IModelState toTransformation() {
        return part -> Optional.empty();
    }
    //TODO
}
