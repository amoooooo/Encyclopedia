package aster.amo.encyclopedia.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.ArrayList;
import java.util.List;

public class HoverHandler {
    public static final KeyMapping INFO_KEY = new KeyMapping("key.encyclopedia.extended", InputConstants.Type.SCANCODE, InputConstants.KEY_I, "key.categories.misc");
    static ItemStack hoveredStack = ItemStack.EMPTY;
    static ItemStack trackingStack = ItemStack.EMPTY;
    static boolean isExtended = false;
    static int page = 0;
    static int maxPage = 1;

    public static void tick(int key) {
        Minecraft instance = Minecraft.getInstance();
        long window = instance.getWindow().getWindow();
        if (InputConstants.isKeyDown(window, key)) {
            if (!isExtended) {
                isExtended = true;
                return;
            }
        } else {
            isExtended = false;
        }
        hoveredStack = ItemStack.EMPTY;
    }

    public static void addToTooltip(ItemStack stack, List<Component> lines) {
        if (!isExtended) {
            ResourceLocation stackName = BuiltInRegistries.ITEM.getKey(stack.getItem());
            String transString = "encyclopedia." + stackName.toString().replace(":", ".").replace("/", ".") + ".desc." + page;
            if (I18n.exists(transString)) {
//                MutableComponent component = lines.get(0).copy();
//                component.append(;
//                lines.set(0, component);
                lines.add(Component.literal("[").withStyle(ChatFormatting.DARK_GRAY)
                        .append(Component.literal(INFO_KEY.getTranslatedKeyMessage().getString().toLowerCase()).withStyle(ChatFormatting.GRAY))
                        .append("]").withStyle(ChatFormatting.DARK_GRAY));
            }
            return;
        }
        updateHovered(stack);


        if (hoveredStack.isEmpty())
            return;
        ResourceLocation stackName = BuiltInRegistries.ITEM.getKey(stack.getItem());
        String transString = "encyclopedia." + stackName.toString().replace(":", ".") + ".desc." + page;
        MutableComponent component = Component.translatable(transString);
        if (I18n.exists(transString)) {
            String translated = I18n.get(transString);
            // if string contains newline \n split it into multiple lines
            String[] split = translated.split("\n");
            List<Component> transLines = new ArrayList<>();
            Style prevStyle = component.getStyle();
            for (String s : split) {
                MutableComponent comp = Component.literal(s);
                transLines.add(Component.literal(s));
            }
            if (lines.size() < 2) {
                lines.addAll(transLines);
            } else {
                lines.addAll(1, transLines);
            }
            if(stack.getItem() instanceof PotionItem) {
                Potion potion = PotionUtils.getPotion(stack);
                List<String> potionTypes = new ArrayList<>();
                potion.getEffects().forEach(effectInstance -> {
                    potionTypes.add(BuiltInRegistries.MOB_EFFECT.getKey(effectInstance.getEffect()).toString().replace(":", ".").replace("/", "."));
                });
                formatRepetition(lines, potionTypes);
            }
            if(stack.getItem() instanceof EnchantedBookItem || stack.isEnchanted()) {
                List<String> enchantments = new ArrayList<>();
                EnchantmentHelper.getEnchantments(stack).forEach((enchantmentInstance, level) -> {
                    enchantments.add(BuiltInRegistries.ENCHANTMENT.getKey(enchantmentInstance).toString().replace(":", ".").replace("/", "."));
                });
                formatRepetition(lines, enchantments);
            }
            int maxPage = 0;
            while (I18n.exists("encyclopedia." + stackName.toString().replace(":", ".") + ".desc." + (maxPage))) {
                maxPage++;
            }
            if (page >= maxPage) {
                page = maxPage - 1;
            } else if (page < 0) {
                page = 0;

            }
            lines.add(Component.literal(" < ").withStyle(ChatFormatting.GRAY)
                    .append(String.valueOf(page + 1)).withStyle(ChatFormatting.GRAY)
                    .append("/").withStyle(ChatFormatting.GRAY)
                    .append(String.valueOf(maxPage)).withStyle(ChatFormatting.GRAY)
                    .append(" >").withStyle(ChatFormatting.GRAY));
        }

    }

    private static void formatRepetition(List<Component> lines, List<String> enchantments) {
        for (String enchantment : enchantments) {
            String enchantmentTransString = "encyclopedia." + enchantment + ".desc." + page;
            if (I18n.exists(enchantmentTransString)) {
                String enchantmentTranslated = I18n.get(enchantmentTransString);
                String[] enchantmentSplit = enchantmentTranslated.split("\n");
                List<Component> enchantmentTransLines = new ArrayList<>();
                // find the line in the existing tooltip that contains the enchantment name
                int index = lines.size();
                String searchLine = enchantment.toLowerCase().split("\\.")[1];
                for (int i = 0; i < lines.size(); i++) {
                    Component line = lines.get(i);
                    if (line.getString().toLowerCase().contains(searchLine)) {
                        index = i+1;
                        break;
                    }
                }
                for (String s : enchantmentSplit) {
                    enchantmentTransLines.add(Component.literal(s));
                }
                lines.addAll(index, enchantmentTransLines);
            }
        }
    }

    private static void updateHovered(ItemStack stack) {
        Minecraft instance = Minecraft.getInstance();
        Screen screen = instance.screen;

        ItemStack prevHovered = trackingStack;
        hoveredStack = ItemStack.EMPTY;

        if (stack.isEmpty()) {
            page = 0;
            return;
        }
        if (prevHovered.isEmpty() || !prevHovered.is(stack.getItem())) {
            trackingStack = stack;
            page = 0;
        }
        hoveredStack = stack;
        trackingStack = stack;
    }

    public static boolean mouseScrolled(double scrollDelta) {
        if (!isExtended)
            return true;
        if (scrollDelta > 0) {
            page++;
            if (page > maxPage) {
                page = 0;
            }
        } else if (scrollDelta < 0) {
            page--;
            if (page < 0) {
                page = maxPage;
            }
        }
        return false;
    }
}
