package com.eirmax.Items;

import com.eirmax.PopcornAttack;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;


public class ModItems {


    public static final Item POPCORN = registerItem("popcorn", new PopcornItem(new Item.Settings().maxDamage(20).maxCount(1)));
    public static final Item SODA = registerItem("soda", new SodaItem(new Item.Settings().maxDamage(20).maxCount(1)));


    public static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(PopcornAttack.MOD_ID, name), item);
    }

    public static void registerModItems() {
        PopcornAttack.LOGGER.info("Registering Mod Items for " + PopcornAttack.MOD_ID);
    }
}

