package com.cimdy.awakenedsilverfish.effect;

import com.cimdy.awakenedsilverfish.AwakenedSilverfish;
import com.cimdy.awakenedsilverfish.effect.custom.*;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EffectRegister {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, AwakenedSilverfish.MODID);
    //固有 双倍最大生命值16和最大攻击力2  攻击有10%几率暴击 暴击造成150%攻击伤害
    //固有 稀有度 普通蠹虫 无  魔法1条词缀 稀有2条词缀 传奇3条词缀 每条词缀使最大生命提高50%  ps传奇基础血量为 (50% * 3 + 100%) * 16 = 40
    //普通
    public static final Holder<MobEffect> NORMAL = MOB_EFFECTS.register("normal", () -> new Normal(
            MobEffectCategory.BENEFICIAL, 0xffffff));
    //魔法
    public static final Holder<MobEffect> MAGIC = MOB_EFFECTS.register("magic", () -> new Magic(
            MobEffectCategory.BENEFICIAL, 0xffffff)
            .addAttributeModifier(
                    Attributes.ATTACK_DAMAGE,
                    ResourceLocation.fromNamespaceAndPath("awakened_silverfish", "attack_damage"),
                    0.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE
            )
    );
    //稀有
    public static final Holder<MobEffect> RARE = MOB_EFFECTS.register("rare", () -> new Rare(
            MobEffectCategory.BENEFICIAL, 0xffffff)
            .addAttributeModifier(
                    Attributes.ATTACK_DAMAGE,
                    ResourceLocation.fromNamespaceAndPath("awakened_silverfish", "attack_damage"),
                    1.0, AttributeModifier.Operation.ADD_MULTIPLIED_BASE
            ));
    //传奇
    public static final Holder<MobEffect> UNIQUE = MOB_EFFECTS.register("unique", () -> new Unique(
            MobEffectCategory.BENEFICIAL, 0xffffff)
            .addAttributeModifier(
                    Attributes.ATTACK_DAMAGE,
                    ResourceLocation.fromNamespaceAndPath("awakened_silverfish", "attack_damage"),
                    1.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE
            ));
    //免疫必死(固有) 具有CD
    public static final Holder<MobEffect> IMMORTAL = MOB_EFFECTS.register("immortal", () -> new Immortal(
            MobEffectCategory.BENEFICIAL, 0xffffff));

    //死亡爆炸 死亡后制造爆炸
    public static final Holder<MobEffect> DEATH_EXPLOSION = MOB_EFFECTS.register("death_explosion", () -> new DeathExplosion(
            MobEffectCategory.BENEFICIAL, 0xffffff));
    //死亡分裂 死亡后原地生成两只且最大生命降低50% 但其他属性相同的个体 当复制体死亡时再复制两只最大生命降为最初1/4 其他属性相同 无法复制的 的个体
    public static final Holder<MobEffect> DEATH_REPRODUCE = MOB_EFFECTS.register("death_reproduce", () -> new DeathReproduce(
            MobEffectCategory.BENEFICIAL, 0xffffff));
    //牺牲者 死亡给予附近友军瞬间生命恢复 等同于自己最大生命的50%
    public static final Holder<MobEffect> SACRIFICE = MOB_EFFECTS.register("sacrifice", () -> new Sacrifice(
            MobEffectCategory.BENEFICIAL, 0xffffff));
    //镜像 生成时 复制一只所有属性完全相同的个体
    public static final Holder<MobEffect> MIRROR = MOB_EFFECTS.register("mirror", () -> new Mirror(
            MobEffectCategory.BENEFICIAL, 0xffffff));
    //生命恢复 每4秒回复80%最大生命值
    public static final Holder<MobEffect>  LIFE_REGENERATION = MOB_EFFECTS.register("life_regeneration", () -> new LifeRegeneration(
            MobEffectCategory.BENEFICIAL, 0xffffff));
    //哨兵 受到的伤害降低30%
    public static final Holder<MobEffect> SENTINEL = MOB_EFFECTS.register("sentinel", () -> new Sentinel(
            MobEffectCategory.BENEFICIAL, 0xffffff)
            .addAttributeModifier(
                    Attributes.SCALE,
                    ResourceLocation.fromNamespaceAndPath("awakened_silverfish", "scale"),
                    0.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE
            )
    );
    //巨象 最大生命提高100%
    public static final Holder<MobEffect> GIANT_ELEPHANT = MOB_EFFECTS.register("giant_elephant", () -> new GiantElephant(
            MobEffectCategory.BENEFICIAL, 0xffffff)
            .addAttributeModifier(
                    Attributes.SCALE,
                    ResourceLocation.fromNamespaceAndPath("awakened_silverfish", "scale"),
                    1.0, AttributeModifier.Operation.ADD_MULTIPLIED_BASE
            )
    );
    //刺客 暴击几率增加600% 暴击伤害增加200%
    public static final Holder<MobEffect> ASSASSIN = MOB_EFFECTS.register("assassin", () -> new Assassin(
            MobEffectCategory.BENEFICIAL, 0xffffff)
            .addAttributeModifier(
                    Attributes.SCALE,
                    ResourceLocation.fromNamespaceAndPath("awakened_silverfish", "scale"),
                    -0.4, AttributeModifier.Operation.ADD_MULTIPLIED_BASE
            )
    );
    //生命偷取 攻击造成伤害的50%偷取为自己生命值
    public static final Holder<MobEffect>  LIFE_STEAL = MOB_EFFECTS.register("life_steal", () -> new LifeSteal(
            MobEffectCategory.BENEFICIAL, 0xffffff));
    //迅捷光环 附近友军移动速度提高30%
    public static final Holder<MobEffect> FAST_AURA = MOB_EFFECTS.register("fast_aura", () -> new FastAura(
            MobEffectCategory.BENEFICIAL, 0xffffff));
    //生命恢复光环 每秒恢复附近友军最大生命10%
    public static final Holder<MobEffect>  LIFE_REGENERATION_AURA = MOB_EFFECTS.register("life_regeneration_aura", () -> new LifeRegenerationAura(
            MobEffectCategory.BENEFICIAL, 0xffffff));
    //暴击光环 使附近友军暴击率增加300% 暴击伤害增加100%
    public static final Holder<MobEffect> CRIT_AURA = MOB_EFFECTS.register("crit_aura", () -> new CritAura(
            MobEffectCategory.BENEFICIAL, 0xffffff));
    //伤害光环 使附近友军造成50%额外伤害
    public static final Holder<MobEffect> DAMAGE_AURA = MOB_EFFECTS.register("damage_aura", () -> new DamageAura(
            MobEffectCategory.BENEFICIAL, 0xffffff));

}

