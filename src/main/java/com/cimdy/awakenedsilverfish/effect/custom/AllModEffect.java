package com.cimdy.awakenedsilverfish.effect.custom;

import com.cimdy.awakenedsilverfish.effect.EffectRegister;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;

import java.util.ArrayList;
import java.util.List;

public class AllModEffect {
    public static List<Holder<MobEffect>> AllEffectList() {
        List<Holder<MobEffect>> EffectList = new ArrayList<>();
        EffectList.add(EffectRegister.DEATH_EXPLOSION);
        EffectList.add(EffectRegister.DEATH_REPRODUCE);
        EffectList.add(EffectRegister.SACRIFICE);
        EffectList.add(EffectRegister.MIRROR);
        EffectList.add(EffectRegister.LIFE_REGENERATION);
        EffectList.add(EffectRegister.SENTINEL);
        EffectList.add(EffectRegister.GIANT_ELEPHANT);
        EffectList.add(EffectRegister.ASSASSIN);
        EffectList.add(EffectRegister.LIFE_STEAL);
        EffectList.add(EffectRegister.FAST_AURA);
        EffectList.add(EffectRegister.LIFE_REGENERATION_AURA);
        EffectList.add(EffectRegister.CRIT_AURA);
        EffectList.add(EffectRegister.DAMAGE_AURA);
        return EffectList;
    }
}
