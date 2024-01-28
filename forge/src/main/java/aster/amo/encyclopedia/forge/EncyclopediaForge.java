package aster.amo.encyclopedia.forge;

import aster.amo.encyclopedia.Encyclopedia;
import aster.amo.encyclopedia.client.HoverHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

import static aster.amo.encyclopedia.client.HoverHandler.INFO_KEY;

@Mod(Encyclopedia.MOD_ID)
public class EncyclopediaForge {
    public EncyclopediaForge() {
        Encyclopedia.init();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onTooltipEvent);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientTick);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::mouseScrolled);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerKeys);
    }

    public void onTooltipEvent(ItemTooltipEvent event){
        ItemStack stack = event.getItemStack();
        List<Component> lines = event.getToolTip();
        HoverHandler.addToTooltip(stack, lines);
    }

    public void clientTick(TickEvent.ClientTickEvent event) {
        HoverHandler.tick(INFO_KEY.getKey().getValue());
    }

    public void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(INFO_KEY);
    }

    public void mouseScrolled(InputEvent.MouseScrollingEvent event) {
        if(HoverHandler.mouseScrolled(event.getScrollDelta())){
            event.setCanceled(true);
        }
    }
}