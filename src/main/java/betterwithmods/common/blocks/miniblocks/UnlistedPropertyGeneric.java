package betterwithmods.common.blocks.miniblocks;

import net.minecraftforge.common.property.IUnlistedProperty;

public class UnlistedPropertyGeneric<T> implements IUnlistedProperty<T> {
    private final String name;
    private final Class<T> klazz;

    public UnlistedPropertyGeneric(String name, Class<T> klazz) {
        this.name = name;
        this.klazz = klazz;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isValid(T value) {
        return true;
    }

    @Override
    public Class<T> getType() {
        return klazz;
    }

    @Override
    public String valueToString(T value) {
        return value != null ? value.toString() : "null";
    }
}