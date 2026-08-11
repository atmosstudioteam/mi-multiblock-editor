package dev.atmos_studio.mimultiblockeditor.compat.yet_another_industrialization;

import aztech.modern_industrialization.machines.multiblocks.SimpleMember;
import dev.atmos_studio.mimultiblockeditor.MIMultiblockEditor;
import dev.atmos_studio.mimultiblockeditor.adapter.MultiblockAdapterResolution;
import dev.atmos_studio.mimultiblockeditor.adapter.MultiblockAdapterType;
import dev.atmos_studio.mimultiblockeditor.adapter.MultiblockVariantOrderer;
import dev.atmos_studio.mimultiblockeditor.data.StructureMemberDefinition;
import dev.atmos_studio.mimultiblockeditor.data.StructureMemberType;
import dev.atmos_studio.mimultiblockeditor.kubejs.KubeJSStructureRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class YAIArboreousGreenhouseCompat {
    public static final String MOD_ID =
            "yet_another_industrialization";

    public static final ResourceLocation CONTROLLER_ID =
            ResourceLocation.fromNamespaceAndPath(
                    MOD_ID,
                    "arboreous_greenhouse"
            );

    private static final String SPECIAL_TAG_NAMESPACE =
            "mi_multiblock_editor";

    private static final String SOIL_TAG_PATH_PREFIX =
            "yai_greenhouse_soil/";

    public static final ResourceLocation
            HANGING_LANTERN_TAG_ID =
            ResourceLocation.fromNamespaceAndPath(
                    SPECIAL_TAG_NAMESPACE,
                    "hanging_lantern"
            );

    private static final String BLOCK_ENTITY_CLASS =
            "me.luligabi.yet_another_industrialization."
                    + "common.block.machine.arboreous_greenhouse."
                    + "ArboreousGreenhouseBlockEntity";

    private static final String TIER_MEMBER_CLASS =
            BLOCK_ENTITY_CLASS
                    + "$TierSimpleMember";

    private static final SimpleMember
            HANGING_LANTERN_MEMBER =
            new SimpleMember() {
                @Override
                public boolean matchesState(
                        BlockState state,
                        BlockEntity blockEntity
                ) {
                    return state.is(
                            Blocks.LANTERN
                    ) && state.getValue(
                            LanternBlock.HANGING
                    );
                }

                @Override
                public BlockState getPreviewState() {
                    return Blocks.LANTERN
                            .defaultBlockState()
                            .setValue(
                                    LanternBlock.HANGING,
                                    true
                            );
                }

                @Override
                public BlockEntity newBlockEntity(
                        RegistryAccess registries,
                        Level level,
                        BlockPos pos,
                        BlockState state
                ) {
                    return null;
                }
            };

    private YAIArboreousGreenhouseCompat() {
    }

    public static boolean isController(
            ResourceLocation controllerId
    ) {
        return CONTROLLER_ID.equals(
                controllerId
        );
    }

    public static boolean isDynamicSoilTag(
            ResourceLocation tagId
    ) {
        return tagId != null
                && SPECIAL_TAG_NAMESPACE.equals(
                tagId.getNamespace()
        )
                && tagId.getPath().startsWith(
                SOIL_TAG_PATH_PREFIX
        );
    }

    public static ResourceLocation getSoilTierId(
            ResourceLocation tagId
    ) {
        if (!isDynamicSoilTag(tagId)) {
            throw new IllegalArgumentException(
                    "Tag "
                            + tagId
                            + " is not a YAI greenhouse "
                            + "dynamic soil tag"
            );
        }

        String encodedTierId =
                tagId.getPath().substring(
                        SOIL_TAG_PATH_PREFIX.length()
                );

        int namespaceSeparator =
                encodedTierId.indexOf('/');

        if (namespaceSeparator <= 0
                || namespaceSeparator
                == encodedTierId.length() - 1) {

            throw new IllegalArgumentException(
                    "Invalid YAI greenhouse soil tag "
                            + tagId
                            + ". Expected path "
                            + SOIL_TAG_PATH_PREFIX
                            + "<namespace>/<path>"
            );
        }

        String namespace =
                encodedTierId.substring(
                        0,
                        namespaceSeparator
                );

        String path =
                encodedTierId.substring(
                        namespaceSeparator + 1
                );

        try {
            return ResourceLocation.fromNamespaceAndPath(
                    namespace,
                    path
            );
        } catch (RuntimeException exception) {
            throw new IllegalArgumentException(
                    "Invalid greenhouse soil tier encoded "
                            + "in tag "
                            + tagId,
                    exception
            );
        }
    }

    public static SimpleMember createSoilMember(
            ResourceLocation tierId
    ) {
        Object tier =
                findTier(
                        tierId
                );

        try {
            Class<?> memberClass =
                    Class.forName(
                            TIER_MEMBER_CLASS,
                            true,
                            YAIArboreousGreenhouseCompat
                                    .class
                                    .getClassLoader()
                    );

            Constructor<?> constructor =
                    memberClass.getDeclaredConstructor(
                            tier.getClass()
                    );

            if (!constructor.trySetAccessible()) {
                throw new IllegalStateException(
                        "Cannot access YAI TierSimpleMember "
                                + "constructor"
                );
            }

            Object rawMember =
                    constructor.newInstance(
                            tier
                    );

            if (!(rawMember
                    instanceof SimpleMember member)) {

                throw new IllegalStateException(
                        "Constructed YAI soil member "
                                + "does not implement "
                                + SimpleMember.class.getName()
                );
            }

            return member;
        } catch (
                ReflectiveOperationException
                | LinkageError exception
        ) {
            throw new IllegalStateException(
                    "Failed to create YAI greenhouse "
                            + "soil member for tier "
                            + tierId,
                    exception
            );
        }
    }

    public static SimpleMember
    getHangingLanternMember() {
        return HANGING_LANTERN_MEMBER;
    }

    public static List<ResourceLocation>
    getTierOrder() {
        List<?> tiers =
                loadTierObjects();

        List<ResourceLocation> tierOrder =
                new ArrayList<>(
                        tiers.size()
                );

        Set<ResourceLocation> uniqueTierIds =
                new LinkedHashSet<>();

        for (Object tier : tiers) {
            ResourceLocation tierId =
                    getTierId(
                            tier
                    );

            if (!uniqueTierIds.add(
                    tierId
            )) {
                throw new IllegalStateException(
                        "YAI Arboreous Greenhouse contains "
                                + "duplicate soil tier "
                                + tierId
                );
            }

            tierOrder.add(
                    tierId
            );
        }

        MIMultiblockEditor.LOGGER.info(
                "Arboreous Greenhouse soil tier order "
                        + "for {}: {}",
                CONTROLLER_ID,
                tierOrder
        );

        return List.copyOf(
                tierOrder
        );
    }

    public static List<KubeJSStructureRegistration>
    orderRegistrations(
            ResourceLocation controllerId,
            MultiblockAdapterResolution resolution,
            List<KubeJSStructureRegistration> registrations
    ) {
        if (!isController(
                controllerId
        )) {
            throw new IllegalArgumentException(
                    "Controller "
                            + controllerId
                            + " is not the YAI "
                            + "Arboreous Greenhouse"
            );
        }

        if (resolution.adapterType()
                != MultiblockAdapterType
                .STANDARD_CRAFTING) {

            throw new IllegalArgumentException(
                    "Arboreous Greenhouse controller "
                            + controllerId
                            + " was resolved to adapter "
                            + resolution.adapterType().id()
                            + ", but "
                            + MultiblockAdapterType
                            .STANDARD_CRAFTING
                            .id()
                            + " was expected"
            );
        }

        for (
                KubeJSStructureRegistration registration
                : registrations
        ) {
            validateRegistration(
                    controllerId,
                    registration
            );
        }

        return MultiblockVariantOrderer
                .orderByVariantKeys(
                        controllerId,
                        resolution.adapterType(),
                        registrations,
                        getTierOrder()
                );
    }

    public static int getActiveSoilIndex(
            Object blockEntity
    ) {
        if (blockEntity == null) {
            throw new IllegalArgumentException(
                    "Arboreous Greenhouse block entity "
                            + "cannot be null"
            );
        }

        try {
            Method getActiveSoilMethod =
                    blockEntity
                            .getClass()
                            .getMethod(
                                    "getActiveSoil"
                            );

            Object activeSoil =
                    getActiveSoilMethod.invoke(
                            blockEntity
                    );

            if (activeSoil == null) {
                throw new IllegalStateException(
                        "Arboreous Greenhouse returned "
                                + "a null activeSoil component"
                );
            }

            Method getActiveShapeMethod =
                    activeSoil
                            .getClass()
                            .getMethod(
                                    "getActiveShape"
                            );

            Object rawIndex =
                    getActiveShapeMethod.invoke(
                            activeSoil
                    );

            if (!(rawIndex instanceof Number number)) {
                throw new IllegalStateException(
                        "SuppliedActiveShapeComponent."
                                + "getActiveShape() returned "
                                + rawIndex
                );
            }

            return Math.max(
                    0,
                    number.intValue()
            );
        } catch (
                ReflectiveOperationException
                | LinkageError exception
        ) {
            throw new IllegalStateException(
                    "Failed to read the active soil index "
                            + "from YAI Arboreous Greenhouse",
                    exception
            );
        }
    }

    private static void validateRegistration(
            ResourceLocation controllerId,
            KubeJSStructureRegistration registration
    ) {
        ResourceLocation variantId =
                registration
                        .variantId()
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "Arboreous Greenhouse "
                                                        + "structure "
                                                        + registration
                                                        .structureId()
                                                        + " must define "
                                                        + ".variant(...)"
                                        )
                        );

        Set<ResourceLocation> mappedSoilTiers =
                new LinkedHashSet<>();

        for (
                StructureMemberDefinition member
                : registration
                .definition()
                .mappings()
                .values()
        ) {
            if (member.type()
                    != StructureMemberType.TAG) {
                continue;
            }

            member.tagId().ifPresent(
                    tagId -> {
                        if (isDynamicSoilTag(
                                tagId
                        )) {
                            mappedSoilTiers.add(
                                    getSoilTierId(
                                            tagId
                                    )
                            );
                        }
                    }
            );
        }

        if (mappedSoilTiers.isEmpty()) {
            throw new IllegalArgumentException(
                    "Arboreous Greenhouse structure "
                            + registration.structureId()
                            + " has no dynamic soil mapping"
            );
        }

        if (mappedSoilTiers.size() != 1) {
            throw new IllegalArgumentException(
                    "Arboreous Greenhouse structure "
                            + registration.structureId()
                            + " references multiple soil tiers: "
                            + mappedSoilTiers
            );
        }

        ResourceLocation mappedTierId =
                mappedSoilTiers.iterator().next();

        if (!variantId.equals(
                mappedTierId
        )) {
            throw new IllegalArgumentException(
                    "Arboreous Greenhouse structure "
                            + registration.structureId()
                            + " uses variant "
                            + variantId
                            + " but its dynamic soil mapping "
                            + "references "
                            + mappedTierId
                            + " for controller "
                            + controllerId
            );
        }
    }

    private static Object findTier(
            ResourceLocation requestedTierId
    ) {
        for (Object tier : loadTierObjects()) {
            ResourceLocation tierId =
                    getTierId(
                            tier
                    );

            if (requestedTierId.equals(
                    tierId
            )) {
                return tier;
            }
        }

        throw new IllegalArgumentException(
                "Unknown YAI Arboreous Greenhouse "
                        + "soil tier "
                        + requestedTierId
                        + ". Available tiers: "
                        + getTierOrder()
        );
    }

    private static List<?> loadTierObjects() {
        requireModLoaded();

        try {
            Class<?> blockEntityClass =
                    Class.forName(
                            BLOCK_ENTITY_CLASS,
                            true,
                            YAIArboreousGreenhouseCompat
                                    .class
                                    .getClassLoader()
                    );

            Field companionField =
                    blockEntityClass.getField(
                            "Companion"
                    );

            Object companion =
                    companionField.get(
                            null
                    );

            if (companion == null) {
                throw new IllegalStateException(
                        "YAI Arboreous Greenhouse "
                                + "Companion is null"
                );
            }

            Method getTiersMethod =
                    companion
                            .getClass()
                            .getMethod(
                                    "getTIERS"
                            );

            Object rawTiers =
                    getTiersMethod.invoke(
                            companion
                    );

            if (!(rawTiers instanceof List<?> tiers)) {
                throw new IllegalStateException(
                        "YAI Arboreous Greenhouse getTIERS() "
                                + "returned "
                                + (
                                rawTiers == null
                                        ? "null"
                                        : rawTiers
                                        .getClass()
                                        .getName()
                        )
                                + " instead of java.util.List"
                );
            }

            if (tiers.isEmpty()) {
                throw new IllegalStateException(
                        "YAI Arboreous Greenhouse tier list "
                                + "is empty. Tiers may not have "
                                + "been initialized yet"
                );
            }

            return tiers;
        } catch (
                ReflectiveOperationException
                | LinkageError exception
        ) {
            throw new IllegalStateException(
                    "Failed to load YAI Arboreous "
                            + "Greenhouse tiers",
                    exception
            );
        }
    }

    private static ResourceLocation getTierId(
            Object tier
    ) {
        if (tier == null) {
            throw new IllegalStateException(
                    "YAI Arboreous Greenhouse tier "
                            + "list contains null"
            );
        }

        try {
            Method getIdMethod =
                    tier.getClass().getMethod(
                            "getId"
                    );

            Object rawId =
                    getIdMethod.invoke(
                            tier
                    );

            if (!(rawId
                    instanceof ResourceLocation tierId)) {

                throw new IllegalStateException(
                        "YAI Arboreous Greenhouse tier "
                                + tier
                                + " returned invalid ID "
                                + rawId
                );
            }

            return tierId;
        } catch (
                ReflectiveOperationException
                | LinkageError exception
        ) {
            throw new IllegalStateException(
                    "Failed to read YAI Arboreous "
                            + "Greenhouse tier ID",
                    exception
            );
        }
    }

    private static void requireModLoaded() {
        if (!ModList.get().isLoaded(
                MOD_ID
        )) {
            throw new IllegalStateException(
                    "Cannot use YAI Arboreous Greenhouse "
                            + "compatibility because mod "
                            + MOD_ID
                            + " is not loaded"
            );
        }
    }
}