package com.cimdy.awakenedsilverfish.event;

import com.cimdy.awakenedsilverfish.attachment.AttachRegister;
import com.cimdy.awakenedsilverfish.effect.EffectRegister;
import com.cimdy.awakenedsilverfish.effect.custom.AllModEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.List;

public class BlockEvents {
    public static void BreakEvent(BlockEvent.BreakEvent event){

        if(!event.getLevel().isClientSide()){//不再局限于虫蚀方块 所有的方块被挖掘都有概率出现蠹虫
            BlockPos blockPos = event.getPos();
            ServerLevel serverLevel = (ServerLevel) event.getLevel();
            Block block = serverLevel.getBlockState(blockPos).getBlock();
            if(block.defaultMapColor() == MapColor.STONE || block.defaultMapColor() == MapColor.DEEPSLATE
                    || block.defaultMapColor() == MapColor.NETHER || block == Blocks.END_STONE){
                Level level = (Level) event.getLevel();
                ItemStack itemStack = event.getPlayer().getMainHandItem();
                Player player = event.getPlayer();
                RandomSource randomSource = serverLevel.random;
                Holder<Enchantment> holder = null;
                if(level.holder(Enchantments.SILK_TOUCH).isPresent()){
                    holder = level.holder(Enchantments.SILK_TOUCH).get().getDelegate();
                }
                boolean b = true;
                if(holder != null){
                    b = EnchantmentHelper.getTagEnchantmentLevel(holder, itemStack) == 0;
                }
                if (b && (serverLevel.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS) && !player.isCreative())) {
                    int random = randomSource.nextInt(100) + 1;
                    if (random >= 90) {//随机概率
                        int random_number = randomSource.nextInt(5) + 1;
                        for (int silverfish_number = 0; silverfish_number < random_number; silverfish_number++) {//随机生成数量
                            Silverfish silverfish = EntityType.SILVERFISH.create(level);
                            if (silverfish != null) {
                                silverfish.moveTo((double) blockPos.getX() + 0.5, (double) blockPos.getY(), (double) blockPos.getZ() + 0.5, 0.0F, 0.0F);
                                int rarity = serverLevel.random.nextInt(100) + 1;//随机 稀有度
                                if (rarity <= 40) {
                                    silverfish.setData(AttachRegister.RARITY, 1);
                                    silverfish.addEffect(new MobEffectInstance(EffectRegister.NORMAL, -1, 0));
                                } else if (rarity <= 70) {
                                    silverfish.setData(AttachRegister.RARITY, 2);
                                    silverfish.addEffect(new MobEffectInstance(EffectRegister.MAGIC, -1, 0));
                                } else if (rarity <= 90) {
                                    silverfish.setData(AttachRegister.RARITY, 3);
                                    silverfish.addEffect(new MobEffectInstance(EffectRegister.RARE, -1, 0));
                                } else if (rarity <= 100) {
                                    silverfish.setData(AttachRegister.RARITY, 4);
                                    silverfish.addEffect(new MobEffectInstance(EffectRegister.UNIQUE, -1, 0));
                                }
                                //设置稀有度
                                int number = silverfish.getData(AttachRegister.RARITY) - 1;
                                //获取词缀数量
                                List<Holder<MobEffect>> effectList = AllModEffect.AllEffectList();
                                //获取词缀列表
                                int all_number = effectList.size();
                                for (int a = 0; a < number; a++) {//随机词条
                                    int mod = randomSource.nextInt(all_number) + 1;
                                    int mod_number = 0;
                                    for (Holder<MobEffect> effect : effectList) {
                                        mod_number++;
                                        if (mod == mod_number) {
                                            if (silverfish.hasEffect(effect)) {
                                                a -= 1;
                                            }
                                            if (!silverfish.hasEffect(effect)) {
                                                silverfish.addEffect(new MobEffectInstance(effect, -1, 0));
                                            }
                                        }
                                    }
                                }
                                silverfish.addEffect(new MobEffectInstance(EffectRegister.IMMORTAL, -1, 0));//固有 不死
                                silverfish.setData(AttachRegister.MAX_HEALTH, (int) silverfish.getMaxHealth());//获取存储原版最大生命
                                silverfish.setData(AttachRegister.MOVE_SPEED, silverfish.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue());//获取存储原版移动速度
                                int MAX_HEALTH_INC = 0;//最大生命提高
                                //最大生命
                                if (silverfish.hasEffect(EffectRegister.MAGIC)) {
                                    MAX_HEALTH_INC += 50;
                                }//稀有度 魔法
                                if (silverfish.hasEffect(EffectRegister.RARE)) {
                                    MAX_HEALTH_INC += 100;
                                }//稀有度 稀有
                                if (silverfish.hasEffect(EffectRegister.UNIQUE)) {
                                    MAX_HEALTH_INC += 150;
                                }//稀有度 传奇
                                if (silverfish.hasEffect(EffectRegister.GIANT_ELEPHANT)) {
                                    MAX_HEALTH_INC += 100;
                                }//巨象
                                double MAX_HEALTH = (double) silverfish.getData(AttachRegister.MAX_HEALTH) * (100 + MAX_HEALTH_INC) / 100;
                                silverfish.getAttribute(Attributes.MAX_HEALTH).setBaseValue(MAX_HEALTH);//设定最大生命值
                                silverfish.setHealth((float) MAX_HEALTH);
                                //如果存在镜像 生成镜像
                                if (silverfish.hasEffect(EffectRegister.MIRROR)) {
                                    Silverfish silverfish_mirror = EntityType.SILVERFISH.create(level);
                                    if (silverfish_mirror != null) {
                                        silverfish_mirror.moveTo((double) blockPos.getX() + 0.5, (double) blockPos.getY(), (double) blockPos.getZ() + 0.5, 0.0F, 0.0F);
                                        silverfish_mirror.setData(AttachRegister.RARITY, silverfish.getData(AttachRegister.RARITY));
                                        if (silverfish_mirror.getData(AttachRegister.RARITY) == 1) {
                                            silverfish_mirror.addEffect(new MobEffectInstance(EffectRegister.NORMAL, -1, 0));
                                        }
                                        if (silverfish_mirror.getData(AttachRegister.RARITY) == 2) {
                                            silverfish_mirror.addEffect(new MobEffectInstance(EffectRegister.MAGIC, -1, 0));
                                        }
                                        if (silverfish_mirror.getData(AttachRegister.RARITY) == 3) {
                                            silverfish_mirror.addEffect(new MobEffectInstance(EffectRegister.RARE, -1, 0));
                                        }
                                        if (silverfish_mirror.getData(AttachRegister.RARITY) == 4) {
                                            silverfish_mirror.addEffect(new MobEffectInstance(EffectRegister.UNIQUE, -1, 0));
                                        }//设置稀有度
                                        for (Holder<MobEffect> effect : effectList) {
                                            if (silverfish.hasEffect(effect)) {
                                                silverfish_mirror.addEffect(new MobEffectInstance(effect, -1, 0));
                                            }
                                        }
                                        silverfish_mirror.setData(AttachRegister.MAX_HEALTH, silverfish.getData(AttachRegister.MAX_HEALTH));//获取存储原版最大生命
                                        silverfish_mirror.setData(AttachRegister.MOVE_SPEED, silverfish.getData(AttachRegister.MOVE_SPEED));//获取存储原版移动速度
                                        //最大生命
                                        silverfish_mirror.getAttribute(Attributes.MAX_HEALTH).setBaseValue(MAX_HEALTH);//设定最大生命值
                                        silverfish_mirror.setHealth((float) MAX_HEALTH);
                                        serverLevel.addFreshEntity(silverfish_mirror);
                                        silverfish_mirror.spawnAnim();
                                    }
                                }
                                serverLevel.addFreshEntity(silverfish);
                                silverfish.spawnAnim();
                            }
                        }
                    }
                }
            }
        }
    }
}
