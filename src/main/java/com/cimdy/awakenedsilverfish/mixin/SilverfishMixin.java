package com.cimdy.awakenedsilverfish.mixin;

import com.cimdy.awakenedsilverfish.attachment.AttachRegister;
import com.cimdy.awakenedsilverfish.effect.EffectRegister;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Silverfish.class)
public class SilverfishMixin extends Monster {

    protected SilverfishMixin(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    @Unique
    private static final EntityDataAccessor<Integer> DATA_IMMORTAL_TIME = SynchedEntityData.defineId(SilverfishMixin.class, EntityDataSerializers.INT);
    @Unique
    private static final EntityDataAccessor<Integer> DATA_TICK_TIME = SynchedEntityData.defineId(SilverfishMixin.class, EntityDataSerializers.INT);
    @Unique
    private static final EntityDataAccessor<Integer> DATA_TIME_S = SynchedEntityData.defineId(SilverfishMixin.class, EntityDataSerializers.INT);

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(DATA_IMMORTAL_TIME, 0);
        pBuilder.define(DATA_TICK_TIME, 0);
        pBuilder.define(DATA_TIME_S, 0);
    }

    @Inject(method = "createAttributes", at = @At("RETURN"), cancellable = true)//蠹虫具有两倍基础血量 16 和两倍基础伤害 2点
    private static void createAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> cir){
        cir.setReturnValue(Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 16.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25).add(Attributes.ATTACK_DAMAGE, 2.0));
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if (this.getData(AttachRegister.RARITY) > 0) {
            AABB aabb = AABB.ofSize(this.getEyePosition(), 12.0, 12.0, 12.0);
            List<Silverfish> silverfishList = this.level().getNearbyEntities(Silverfish.class, TargetingConditions.DEFAULT, this,aabb);
            int TICK_TIME = this.getEntityData().get(DATA_TICK_TIME);//计时器
            TICK_TIME++;
            if(TICK_TIME == 20){//每秒
                int TIME_S = this.getEntityData().get(DATA_TIME_S);
                TICK_TIME = 0;
                //生命恢复光环
                boolean HAS_LIFE_REGENERATION_AURA = this.hasEffect(EffectRegister.LIFE_REGENERATION_AURA);
                if(!HAS_LIFE_REGENERATION_AURA){
                    for (Silverfish silverfish : silverfishList) {//没有 寻找附近是否存在友方具有
                        if(silverfish.hasEffect(EffectRegister.LIFE_REGENERATION_AURA)) {
                            HAS_LIFE_REGENERATION_AURA = true;
                        }
                    }
                }
                if(HAS_LIFE_REGENERATION_AURA){
                    float heal = this.getMaxHealth() / 10;
                    this.heal(heal);
                }

                TIME_S++;
                if(TIME_S == 4){//每4秒
                    TIME_S = 0;
                    if(this.hasEffect(EffectRegister.LIFE_REGENERATION)){float heal = this.getMaxHealth() * 8 / 10; this.heal(heal);}}//生命恢复
                this.getEntityData().set(DATA_TIME_S, TIME_S);
            }
            this.getEntityData().set(DATA_TICK_TIME, TICK_TIME);
            //移动速度
            int MOVE_SPEED_INC = 0;//移动速度提高
            //迅捷光环
            boolean HAS_FAST_AURA = this.hasEffect(EffectRegister.FAST_AURA);
            if(!HAS_FAST_AURA){
                for (Silverfish silverfish : silverfishList) {//没有 寻找附近是否存在友方具有
                    if(silverfish.hasEffect(EffectRegister.FAST_AURA)) {
                        HAS_FAST_AURA = true;
                    }
                }
            }

            if(HAS_FAST_AURA){
                MOVE_SPEED_INC += 30;
            }
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.getData(AttachRegister.MOVE_SPEED) * (100 + MOVE_SPEED_INC) / 100);//设定速度

            if(!this.hasEffect(EffectRegister.IMMORTAL)){//每10s获得一次机会必定免疫必死伤害(固有属性)
                int immortal = this.getEntityData().get(DATA_IMMORTAL_TIME);
                immortal++;
                if(immortal % 20 == 0){
                    this.heal(this.getMaxHealth() / 10);
                }
                if(immortal == 200){
                    immortal = 0;
                    this.addEffect(new MobEffectInstance(EffectRegister.IMMORTAL,-1, 0));
                }
                this.getEntityData().set(DATA_IMMORTAL_TIME, immortal);
            }
        }
    }
}
