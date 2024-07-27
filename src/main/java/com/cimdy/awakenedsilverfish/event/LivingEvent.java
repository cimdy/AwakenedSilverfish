package com.cimdy.awakenedsilverfish.event;

import com.cimdy.awakenedsilverfish.attachment.AttachRegister;
import com.cimdy.awakenedsilverfish.effect.EffectRegister;
import com.cimdy.awakenedsilverfish.effect.custom.AllModEffect;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.List;
import java.util.Objects;

public class LivingEvent {
    @SubscribeEvent
    public static void LivingIncomingDamageEvent(LivingIncomingDamageEvent event) {
        LivingEntity living = event.getEntity();
        if (!living.level().isClientSide) {
            ServerLevel serverLevel = (ServerLevel) living.level();
            RandomSource random = serverLevel.random;
            float damage = event.getAmount();
            //哨兵
            if (living.hasEffect(EffectRegister.SENTINEL)) {
                damage *= 0.7F;
            }

            if (event.getSource().getEntity() instanceof LivingEntity target) {
                AABB aabb = AABB.ofSize(target.getEyePosition(), 12.0, 12.0, 12.0);
                List<Silverfish> silverfishList = target.level().getEntitiesOfClass(Silverfish.class, aabb);
                //伤害提高光环
                boolean HAS_DAMAGE_AURA = target.hasEffect(EffectRegister.DAMAGE_AURA);
                if (!HAS_DAMAGE_AURA) {
                    for (Silverfish silverfish : silverfishList) {//没有 寻找附近是否存在友方具有
                        if (silverfish.hasEffect(EffectRegister.DAMAGE_AURA)) {
                            HAS_DAMAGE_AURA = true;
                        }
                    }
                }
                if (HAS_DAMAGE_AURA) {
                    damage = damage * (100 + 50) / 100;
                }
                //暴击几率 暴击伤害
                int CRIT_CHANCE_INC = 0;
                int CRIT_DAMAGE_INC = 0;

                if (target.hasEffect(EffectRegister.ASSASSIN)) {
                    CRIT_CHANCE_INC += 600;
                    CRIT_DAMAGE_INC += 200;
                }//刺客
                //暴击光环
                boolean HAS_CRIT_AURA = target.hasEffect(EffectRegister.CRIT_AURA);
                if (!HAS_DAMAGE_AURA) {
                    for (Silverfish silverfish : silverfishList) { //没有 寻找附近是否存在友方具有
                        if (silverfish.hasEffect(EffectRegister.CRIT_AURA)) {
                            HAS_CRIT_AURA = true;
                        }
                    }
                }
                if (HAS_CRIT_AURA) {
                    CRIT_CHANCE_INC += 300;
                    CRIT_DAMAGE_INC += 100;
                }
                //暴击判定
                boolean IS_CRIT = false;
                int CRIT_CHANCE = target.getData(AttachRegister.CRIT_CHANCE) * (100 + CRIT_CHANCE_INC) / 100;
                if (CRIT_CHANCE >= random.nextInt(100) + 1) {
                    IS_CRIT = true;
                }
                //暴伤加成
                if (IS_CRIT) {
                    damage = damage * (target.getData(AttachRegister.CRIT_DAMAGE) + CRIT_DAMAGE_INC) / 100;
                }
                //生命偷取
                if (target.hasEffect(EffectRegister.LIFE_STEAL)) {
                    float heal = damage / 2;
                    target.heal(heal);
                }
            }

            event.setAmount(damage);
        }
    }

    public static void LivingDeathEvent(LivingDeathEvent event) {
        LivingEntity living = event.getEntity();
        if(!living.level().isClientSide) {
            ServerLevel serverLevel = (ServerLevel) living.level();
            RandomSource random = serverLevel.random;
            //固有技能 每隔一段时间秒免疫一次必死伤害
            if(living.hasEffect(EffectRegister.IMMORTAL)){
                living.setHealth(1);
                living.forceAddEffect(new MobEffectInstance(EffectRegister.IMMORTAL,1 ,0), living);
                event.setCanceled(true);
            }

            //死亡爆炸
            if(living.hasEffect(EffectRegister.DEATH_EXPLOSION)){
                living.level().explode(
                        living, living.getX(), living.getY(), living.getZ(),
                        2, Level.ExplosionInteraction.MOB);
            }

            AABB aabb = AABB.ofSize(living.getEyePosition(), 12.0, 12.0, 12.0);
            List<Silverfish> silverfishList = living.level().getEntitiesOfClass(Silverfish.class, aabb);
            //牺牲者
            if(living.hasEffect(EffectRegister.SACRIFICE)){
                float heal = living.getMaxHealth() / 2;
                for(Silverfish silverfish : silverfishList){
                    silverfish.heal(heal);
                }
            }

            List<Holder<MobEffect>> effectList = AllModEffect.AllEffectList();//获取词缀列表
            //死亡复制
            if(living.hasEffect(EffectRegister.DEATH_REPRODUCE)){
                int DEATH_REPRODUCE_NUMBER = living.getData(AttachRegister.DEATH_REPRODUCE_NUMBER);
                if(DEATH_REPRODUCE_NUMBER < 2){//复制次数最多2次
                    int randomNumber = random.nextInt(2) + 2;
                    for(int a = 0; a < randomNumber; a++){
                        Silverfish silverfish = EntityType.SILVERFISH.create(serverLevel);
                        if(silverfish != null) {
                            silverfish.moveTo(living.getX(), living.getY(), living.getZ(), 0.0F, 0.0F);
                            silverfish.setData(AttachRegister.MAX_HEALTH, living.getData(AttachRegister.MAX_HEALTH) / 2);
                            silverfish.setData(AttachRegister.MOVE_SPEED, living.getData(AttachRegister.MOVE_SPEED));
                            //生命值 体积
                            Objects.requireNonNull(silverfish.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(silverfish.getData(AttachRegister.MAX_HEALTH));
                            silverfish.heal(silverfish.getMaxHealth());
                            Objects.requireNonNull(silverfish.getAttribute(Attributes.SCALE)).setBaseValue(DEATH_REPRODUCE_NUMBER == 0 ? 0.5 : 0.25);
                            //词缀
                            silverfish.setData(AttachRegister.RARITY, living.getData(AttachRegister.RARITY));
                            if(silverfish.getData(AttachRegister.RARITY) == 1) {
                                silverfish.addEffect(new MobEffectInstance(EffectRegister.NORMAL,-1,0));
                            }
                            if(silverfish.getData(AttachRegister.RARITY) == 2) {
                                silverfish.addEffect(new MobEffectInstance(EffectRegister.MAGIC,-1,0));
                            }
                            if(silverfish.getData(AttachRegister.RARITY) == 3) {
                                silverfish.addEffect(new MobEffectInstance(EffectRegister.RARE,-1,0));
                            }
                            if(silverfish.getData(AttachRegister.RARITY) == 4) {
                                silverfish.addEffect(new MobEffectInstance(EffectRegister.UNIQUE,-1,0));
                            }
                            //设置稀有度
                            for (Holder<MobEffect> effect : effectList) {
                                if (living.hasEffect(effect)) {
                                    silverfish.addEffect(new MobEffectInstance(effect, -1, 0));
                                }
                            }
                            //传递复制次数 并+1
                            silverfish.setData(AttachRegister.DEATH_REPRODUCE_NUMBER, DEATH_REPRODUCE_NUMBER + 1);
                            serverLevel.addFreshEntity(silverfish);
                            silverfish.spawnAnim();
                        }
                    }
                }
            }
        }
    }

    public static void LivingDropsEvent(LivingDropsEvent event) {
        LivingEntity living = event.getEntity();
        if(!living.level().isClientSide && living.getData(AttachRegister.RARITY) > 0) {
            ServerLevel serverLevel = (ServerLevel) living.level();
            RandomSource random = serverLevel.random;
            //死亡掉落随机矿物 普通以上稀有度每增加1级 掉落物品数量增加100%
            int number = random.nextInt(6);
            number = number * living.getData(AttachRegister.RARITY);
            for(int a = 0; a < number; a++) {
                ItemStack itemStack = ItemStack.EMPTY;
                int ore = random.nextInt(100) + 1;
                if (ore <= 20) {
                    itemStack = Items.COAL.getDefaultInstance();
                } else if (ore <= 40) {
                    itemStack = Items.REDSTONE.getDefaultInstance();
                } else if (ore <= 50) {
                    itemStack = Items.IRON_NUGGET.getDefaultInstance();
                } else if (ore <= 60) {
                    itemStack = Items.GOLD_NUGGET.getDefaultInstance();
                } else if (ore <= 65) {
                    itemStack = Items.RAW_COPPER.getDefaultInstance();
                } else if (ore <= 70) {
                    itemStack = Items.RAW_IRON.getDefaultInstance();
                } else if (ore <= 75) {
                    itemStack = Items.RAW_GOLD.getDefaultInstance();
                } else if (ore <= 80) {
                    itemStack = Items.EMERALD.getDefaultInstance();
                } else if (ore <= 85) {
                    itemStack = Items.LAPIS_LAZULI.getDefaultInstance();
                } else if (ore <= 90) {
                    itemStack = Items.AMETHYST_SHARD.getDefaultInstance();
                } else if (ore <= 95) {
                    itemStack = Items.QUARTZ.getDefaultInstance();
                } else if (ore <= 99) {
                    itemStack = Items.DIAMOND.getDefaultInstance();
                } else if (ore == 100) {
                    itemStack = Items.NETHERITE_SCRAP.getDefaultInstance();
                }

                if (itemStack != ItemStack.EMPTY) {
                    ItemEntity itemEntity = living.spawnAtLocation(itemStack, 0.3F);
                    event.getDrops().add(itemEntity);
                }
            }
        }
    }
}
