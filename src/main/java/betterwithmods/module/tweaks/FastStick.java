package betterwithmods.module.tweaks;

import betterwithmods.module.Feature;

public class FastStick extends Feature{
    public FastStick() {
        recipeCondition = true;
    }

    @Override
    public String getFeatureDescription() {
        return "Allows crafting two stick from a single plank";
    }
}
