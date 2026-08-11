package dev.atmos_studio.mimultiblockeditor;

import com.mojang.logging.LogUtils;
import dev.atmos_studio.mimultiblockeditor.runtime.MultiblockOverrideCache;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import org.slf4j.Logger;

@Mod(MIMultiblockEditor.MOD_ID)
public final class MIMultiblockEditor {
    public static final String MOD_ID =
            "mi_multiblock_editor";

    public static final Logger LOGGER =
            LogUtils.getLogger();

    public MIMultiblockEditor(
            IEventBus modEventBus
    ) {
        NeoForge.EVENT_BUS.addListener(
                MIMultiblockEditor::onServerStarted
        );

        LOGGER.info(
                "Loading MI Multiblock Editor"
        );
    }

    private static void onServerStarted(
            ServerStartedEvent event
    ) {
        MultiblockOverrideCache.ensureBuilt();
    }

    public static ResourceLocation id(
            String path
    ) {
        return ResourceLocation.fromNamespaceAndPath(
                MOD_ID,
                path
        );
    }
}