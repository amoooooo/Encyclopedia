package aster.amo.encyclopedia.fabric;

import aster.amo.encyclopedia.Encyclopedia;
import aster.amo.encyclopedia.client.HoverHandler;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import static aster.amo.encyclopedia.client.HoverHandler.INFO_KEY;

public class EncyclopediaFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Encyclopedia.init();
        KeyBindingHelper.registerKeyBinding(INFO_KEY);
        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            HoverHandler.addToTooltip(stack, lines);
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            HoverHandler.tick(KeyBindingHelper.getBoundKeyOf(INFO_KEY).getValue());
        });
        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if(screen != null)
                ScreenMouseEvents.allowMouseScroll(screen).register((screen2, mouseX, mouseY, horizontalAmount, verticalAmount) -> HoverHandler.mouseScrolled(verticalAmount));
        });
    }
}