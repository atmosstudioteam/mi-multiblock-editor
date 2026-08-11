package dev.atmos_studio.mimultiblockeditor.kubejs;

import dev.atmos_studio.mimultiblockeditor.MIMultiblockEditor;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.script.ScriptType;

public final class MIMultiblockKubeJSPlugin
        implements KubeJSPlugin {

    @Override
    public void registerBindings(
            BindingRegistry bindings
    ) {
        if (bindings.type() == ScriptType.STARTUP) {
            bindings.add(
                    "MIMultiblocks",
                    MIMultiblocksBinding.INSTANCE
            );
        }
    }

    @Override
    public void beforeScriptsLoaded(
            ScriptManager manager
    ) {
        if (manager.scriptType != ScriptType.STARTUP) {
            return;
        }

        KubeJSStructureRegistry.clear();
    }

    @Override
    public void afterScriptsLoaded(
            ScriptManager manager
    ) {
        if (manager.scriptType != ScriptType.STARTUP) {
            return;
        }

        MIMultiblockEditor.LOGGER.info(
                "Loaded {} KubeJS MI multiblock "
                        + "structure definition(s)",
                KubeJSStructureRegistry.size()
        );
    }
}