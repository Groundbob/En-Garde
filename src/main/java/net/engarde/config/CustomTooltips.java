package net.engarde.config;

import net.engarde.client.EnGardeClient;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class CustomTooltips {
    public static void register() {
        ItemTooltipCallback.EVENT.register(((stack, _, _, lines) -> {
            Identifier itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
            ParryItemConfig itemConfig = EnGardeClient.PARRY_ITEM_CONFIGS.get(itemId);
            if (itemConfig == null || itemConfig.parryItem == null) return;
            if (itemConfig.parryItem) {

                TooltipDisplay tooltipDisplay = stack.getOrDefault(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT);

                int enchantmentLineCount = 0;
                if (tooltipDisplay.shows(DataComponents.ENCHANTMENTS)) {
                    ItemEnchantments enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
                    enchantmentLineCount = enchantments.size();
                }

                int insertIndex = Math.min(1 + enchantmentLineCount, lines.size());
                if (!itemConfig.heavyItem) {
                    lines.add(insertIndex, Component.literal("Parry Item").withStyle(ChatFormatting.DARK_GREEN));
                } else {
                    lines.add(insertIndex, Component.literal("Heavy Parry Item").withStyle(ChatFormatting.DARK_AQUA));
                }
            }
        }));
    }
}
