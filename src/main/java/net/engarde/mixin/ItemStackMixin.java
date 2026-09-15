package net.engarde.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.engarde.EnGarde;
import net.engarde.client.EnGardeClient;
import net.engarde.config.EnGardeConfig;
import net.engarde.config.ParryItemConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.apache.commons.lang3.function.TriConsumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BiConsumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    /* DURABILITY */
    @Inject(method = "processDurabilityChange", at = @At("HEAD"), cancellable = true)
    private void engarde$disableDurability(int amount, ServerLevel level, ServerPlayer player, CallbackInfoReturnable<Integer> cir) {
        ItemStack itemStack = (ItemStack) (Object) this;
        if (!itemStack.is(Items.WOLF_ARMOR) && EnGardeConfig.loadConfig().disableDurability) {
            cir.setReturnValue(0);
        }
    }

    @Inject(method = "isDamageableItem", at = @At("HEAD"), cancellable = true)
    private void engarde$disableDurability(CallbackInfoReturnable<Boolean> cir) {
        ItemStack itemStack = (ItemStack) (Object) this;
        if (!itemStack.is(Items.WOLF_ARMOR) && EnGardeConfig.loadConfig().disableDurability) {
            cir.setReturnValue(false);
        }
    }

    /* CONFIGURABLE ITEM ATTRIBUTES */
    @Inject(method = "forEachModifier(Lnet/minecraft/world/entity/EquipmentSlot;Ljava/util/function/BiConsumer;)V", at = @At("HEAD"))
    private void engarde$injectConfigAttributes(EquipmentSlot slot, BiConsumer<Holder<Attribute>, AttributeModifier> consumer, CallbackInfo ci) {
        ItemStack itemStack = (ItemStack) (Object) this;
        Identifier itemId = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
        ParryItemConfig itemConfig = EnGardeClient.PARRY_ITEM_CONFIGS.get(itemId);


        if (slot == EquipmentSlot.MAINHAND && itemConfig != null && itemConfig.combat != null) {
            injectAttributesBiConsumer(itemConfig, consumer);
        }
    }

    @WrapOperation(method = "forEachModifier(Lnet/minecraft/world/entity/EquipmentSlot;Ljava/util/function/BiConsumer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/component/ItemAttributeModifiers;forEach(Lnet/minecraft/world/entity/EquipmentSlot;Ljava/util/function/BiConsumer;)V"))
    private void engarde$cancelOriginalAttributes(ItemAttributeModifiers instance, EquipmentSlot slot, BiConsumer<Holder<Attribute>, AttributeModifier> consumer, Operation<Void> original) {
        ItemStack itemStack = (ItemStack) (Object) this;
        Identifier itemId = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
        ParryItemConfig itemConfig = EnGardeClient.PARRY_ITEM_CONFIGS.get(itemId);
        if (!(itemConfig != null && itemConfig.combat != null)) {
            original.call(instance, slot, consumer);
        }
    }

    @Unique
    private void injectAttributesBiConsumer(ParryItemConfig itemConfig, BiConsumer<Holder<Attribute>, AttributeModifier> consumer) {
        if (itemConfig.combat.damage() != null) {
            AttributeModifier damageModifier = new AttributeModifier(
                    Identifier.fromNamespaceAndPath(EnGarde.MOD_ID, "config_damage"),
                    itemConfig.combat.damage() -1,
                    AttributeModifier.Operation.ADD_VALUE
            );
            consumer.accept(Attributes.ATTACK_DAMAGE, damageModifier);
        }
        if (itemConfig.combat.speed() != null) {
            AttributeModifier speedModifier = new AttributeModifier(
                    Identifier.fromNamespaceAndPath(EnGarde.MOD_ID, "config_speed"),
                    itemConfig.combat.speed() -4,
                    AttributeModifier.Operation.ADD_VALUE
            );
            consumer.accept(Attributes.ATTACK_SPEED, speedModifier);
        }
        if (itemConfig.combat.range() != null) {
            AttributeModifier rangeModifier = new AttributeModifier(
                    Identifier.fromNamespaceAndPath(EnGarde.MOD_ID, "config_range"),
                    itemConfig.combat.range() -3,
                    AttributeModifier.Operation.ADD_VALUE
            );
            consumer.accept(Attributes.ENTITY_INTERACTION_RANGE, rangeModifier);
        }
    }

    // Dealing with the item tooltips
    @Inject(method = "forEachModifier(Lnet/minecraft/world/entity/EquipmentSlotGroup;Lorg/apache/commons/lang3/function/TriConsumer;)V", at = @At("HEAD"))
    private void engarde$overrideTooltip(EquipmentSlotGroup slot, TriConsumer<Holder<Attribute>, AttributeModifier, ItemAttributeModifiers.Display> consumer, CallbackInfo ci) {
        ItemStack itemStack = (ItemStack) (Object) this;
        Identifier itemId = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
        ParryItemConfig itemConfig = EnGardeClient.PARRY_ITEM_CONFIGS.get(itemId);

        if (slot == EquipmentSlotGroup.MAINHAND && itemConfig != null && itemConfig.combat != null) {
            TriConsumer<Holder<Attribute>, AttributeModifier, ItemAttributeModifiers.Display> triConsumer = (attribute, modifier, _) ->
                    consumer.accept(attribute, modifier, ItemAttributeModifiers.Display.attributeModifiers());
            injectAttributesTriConsumer(triConsumer, itemConfig);
        }
    }

    @WrapOperation(method = "forEachModifier(Lnet/minecraft/world/entity/EquipmentSlotGroup;Lorg/apache/commons/lang3/function/TriConsumer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/component/ItemAttributeModifiers;forEach(Lnet/minecraft/world/entity/EquipmentSlotGroup;Lorg/apache/commons/lang3/function/TriConsumer;)V"))
    private void engarde$cancelOriginalTooltip(ItemAttributeModifiers instance, EquipmentSlotGroup slot, TriConsumer<Holder<Attribute>, AttributeModifier, ItemAttributeModifiers.Display> consumer, Operation<Void> original) {
        ItemStack itemStack = (ItemStack) (Object) this;
        Identifier itemId = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
        ParryItemConfig itemConfig = EnGardeClient.PARRY_ITEM_CONFIGS.get(itemId);
        if (!(itemConfig != null && itemConfig.combat != null)) {
            original.call(instance, slot, consumer);
        }
    }

    @Unique
    private void injectAttributesTriConsumer(TriConsumer<Holder<Attribute>, AttributeModifier, ItemAttributeModifiers.Display> consumer, ParryItemConfig itemConfig) {

        ItemAttributeModifiers.Display display = ItemAttributeModifiers.Display.attributeModifiers();
        if (itemConfig != null && itemConfig.combat != null) {
            if (itemConfig.combat.damage() != null) {
                consumer.accept(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                        Identifier.fromNamespaceAndPath(EnGarde.MOD_ID, "config_damage"),
                        itemConfig.combat.damage(),
                        AttributeModifier.Operation.ADD_VALUE
                ), display);
            }
            if (itemConfig.combat.speed() != null) {
                consumer.accept(Attributes.ATTACK_SPEED, new AttributeModifier(
                        Identifier.fromNamespaceAndPath(EnGarde.MOD_ID, "config_speed"),
                        itemConfig.combat.speed(),
                        AttributeModifier.Operation.ADD_VALUE
                ), display);
            }
            if (itemConfig.combat.range() != null && itemConfig.combat.range() != 3) {
                consumer.accept(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(
                        Identifier.fromNamespaceAndPath(EnGarde.MOD_ID, "config_range"),
                        itemConfig.combat.range() -3,
                        AttributeModifier.Operation.ADD_VALUE
                ), display);
            }
        }
    }
}
