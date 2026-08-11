package dev.atmos_studio.mimultiblockeditor.kubejs;

import dev.atmos_studio.mimultiblockeditor.MIMultiblockEditor;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class KubeJSStructureRegistry {
    private static final Map<
            ResourceLocation,
            KubeJSStructureRegistration
            > BY_STRUCTURE_ID =
            new LinkedHashMap<>();

    private KubeJSStructureRegistry() {
    }

    public static synchronized void register(
            KubeJSStructureRegistration registration
    ) {
        ResourceLocation structureId =
                registration.structureId();

        if (BY_STRUCTURE_ID.containsKey(structureId)) {
            throw new IllegalArgumentException(
                    "Duplicate KubeJS multiblock "
                            + "structure ID: "
                            + structureId
            );
        }

        BY_STRUCTURE_ID.put(
                structureId,
                registration
        );

        MIMultiblockEditor.LOGGER.info(
                "Registered KubeJS multiblock structure "
                        + "{} for controller {}",
                registration.structureId(),
                registration.controllerId()
        );
    }

    public static synchronized int size() {
        return BY_STRUCTURE_ID.size();
    }

    public static synchronized void clear() {
        BY_STRUCTURE_ID.clear();
    }

    public static synchronized List<
            KubeJSStructureRegistration
            > all() {

        return List.copyOf(
                BY_STRUCTURE_ID.values()
        );
    }

    public static synchronized Map<
            ResourceLocation,
            List<KubeJSStructureRegistration>
            > byController() {

        Map<
                ResourceLocation,
                List<KubeJSStructureRegistration>
                > grouped =
                new LinkedHashMap<>();

        for (KubeJSStructureRegistration registration
                : BY_STRUCTURE_ID.values()) {

            grouped.computeIfAbsent(
                    registration.controllerId(),
                    ignored -> new ArrayList<>()
            ).add(registration);
        }

        Map<
                ResourceLocation,
                List<KubeJSStructureRegistration>
                > immutable =
                new LinkedHashMap<>();

        for (Map.Entry<
                ResourceLocation,
                List<KubeJSStructureRegistration>
                > entry : grouped.entrySet()) {

            immutable.put(
                    entry.getKey(),
                    List.copyOf(entry.getValue())
            );
        }

        return Collections.unmodifiableMap(
                immutable
        );
    }
}