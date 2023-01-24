package net.code4me.anoduels.bukkit.compatilibity;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import net.code4me.anoduels.api.component.LocationComponent;
import net.code4me.anoduels.common.util.PairFactory;
import org.jetbrains.annotations.Nullable;

public final class WorldEditCompatibility {
    private WorldEditCompatibility() {
    }

    @Nullable
    public static PairFactory<LocationComponent, LocationComponent> getBounds(String playerName) {
        LocalSession session = WorldEdit.getInstance().getSessionManager().findByName(playerName);
        if (session == null) return null;

        World world = session.getSelectionWorld();
        if (world == null) return null;

        try {
            Region region = session.getRegionSelector(world).getRegion();

            return new PairFactory<>(
                    LocationComponent.of(world.getName(),
                            region.getMinimumPoint().getBlockX(), region.getMinimumPoint().getBlockY(), region.getMinimumPoint().getBlockZ()),
                    LocationComponent.of(world.getName(),
                            region.getMaximumPoint().getBlockX(), region.getMaximumPoint().getBlockY(), region.getMaximumPoint().getBlockZ()));
        } catch (IncompleteRegionException e) {
            return null;
        }
    }
}
