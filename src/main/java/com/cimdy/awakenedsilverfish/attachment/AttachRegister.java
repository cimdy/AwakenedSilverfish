package com.cimdy.awakenedsilverfish.attachment;

import com.cimdy.awakenedsilverfish.AwakenedSilverfish;
import com.mojang.serialization.Codec;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class AttachRegister {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, AwakenedSilverfish.MODID);

    public static final Supplier<AttachmentType<Integer>> RARITY = ATTACHMENT_TYPES.register(
            "rarity", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).build());

    public static final Supplier<AttachmentType<Integer>> MAX_HEALTH = ATTACHMENT_TYPES.register(
            "max_health", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).build());

    public static final Supplier<AttachmentType<Double>> MOVE_SPEED = ATTACHMENT_TYPES.register(
            "move_speed", () -> AttachmentType.builder(() -> 0.0).serialize(Codec.DOUBLE).build());

    public static final Supplier<AttachmentType<Integer>> CRIT_CHANCE = ATTACHMENT_TYPES.register(
            "crit_chance", () -> AttachmentType.builder(() -> 10).serialize(Codec.INT).build());

    public static final Supplier<AttachmentType<Integer>> CRIT_DAMAGE = ATTACHMENT_TYPES.register(
            "crit_damage", () -> AttachmentType.builder(() -> 150).serialize(Codec.INT).build());

    public static final Supplier<AttachmentType<Integer>> DEATH_REPRODUCE_NUMBER = ATTACHMENT_TYPES.register(
            "death_reproduce_number", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).build());
}
