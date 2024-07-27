package com.cimdy.awakenedsilverfish;

import com.cimdy.awakenedsilverfish.attachment.AttachRegister;
import com.cimdy.awakenedsilverfish.effect.EffectRegister;
import com.cimdy.awakenedsilverfish.event.BlockEvents;
import com.cimdy.awakenedsilverfish.event.LivingEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(AwakenedSilverfish.MODID)
public class AwakenedSilverfish
{
    public static final String MODID = "awakened_silverfish";

    public AwakenedSilverfish(IEventBus modEventBus)
    {
        AttachRegister.ATTACHMENT_TYPES.register(modEventBus);
        EffectRegister.MOB_EFFECTS.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.addListener(BlockEvents::BreakEvent);

        NeoForge.EVENT_BUS.addListener(LivingEvent::LivingIncomingDamageEvent);
        NeoForge.EVENT_BUS.addListener(LivingEvent::LivingDeathEvent);
        NeoForge.EVENT_BUS.addListener(LivingEvent::LivingDropsEvent);

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {

    }

}
