package net.engarde.mixin.attributes;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.engarde.EnGarde;
import net.engarde.client.EnGardeClient;
import net.engarde.config.ParryItemConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiConsumer;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "forEachModifier(Lnet/minecraft/world/entity/EquipmentSlot;Ljava/util/function/BiConsumer;)V", at = @At("HEAD"))
    private void engarde$injectConfigAttributes(EquipmentSlot slot, BiConsumer<Holder<Attribute>, AttributeModifier> consumer, CallbackInfo ci) {
        ItemStack itemStack = (ItemStack) (Object) this;
        Identifier itemId = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
        ParryItemConfig itemConfig = EnGardeClient.PARRY_ITEM_CONFIGS.get(itemId);


        if (slot == EquipmentSlot.MAINHAND && itemConfig != null && itemConfig.combat != null) {
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
}
