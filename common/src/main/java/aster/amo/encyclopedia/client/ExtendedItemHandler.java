package aster.amo.encyclopedia.client;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class ExtendedItemHandler {
    private static Map<Predicate<ItemStack>, BiFunction<List<Component>, ItemStack, List<Component>>> EXTENDED_ITEM_HANDLERS = new HashMap<>();

    public static void register(Predicate<ItemStack> predicate, BiFunction<List<Component>, ItemStack, List<Component>> handler){
        EXTENDED_ITEM_HANDLERS.put(predicate, handler);
    }

    public static void addToTooltip(ItemStack stack, List<Component> lines){
        for(Map.Entry<Predicate<ItemStack>, BiFunction<List<Component>, ItemStack, List<Component>>> entry : EXTENDED_ITEM_HANDLERS.entrySet()){
            if(entry.getKey().test(stack)){
                lines = entry.getValue().apply(lines, stack);
            }
        }
    }

    public static void clear(){
        EXTENDED_ITEM_HANDLERS.clear();
    }
}
