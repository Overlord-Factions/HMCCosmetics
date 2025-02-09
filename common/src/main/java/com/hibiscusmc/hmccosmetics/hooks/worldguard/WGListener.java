package com.hibiscusmc.hmccosmetics.hooks.worldguard;

import com.hibiscusmc.hmccosmetics.config.Wardrobe;
import com.hibiscusmc.hmccosmetics.config.WardrobeSettings;
import com.hibiscusmc.hmccosmetics.user.CosmeticUser;
import com.hibiscusmc.hmccosmetics.user.CosmeticUsers;
import com.hibiscusmc.hmccosmetics.util.MessagesUtil;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Contains {@link com.sk89q.worldguard.WorldGuard WorldGuard} related event listeners
 */
public class WGListener implements Listener {
    @EventHandler
    public void onPlayerMove(@NotNull PlayerMoveEvent event) {
        CosmeticUser user = CosmeticUsers.getUser(event.getPlayer());
        if (user == null) return;
        Location location = event.getPlayer().getLocation();
        com.sk89q.worldedit.util.Location loc = BukkitAdapter.adapt(location);
        RegionContainer region = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = region.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(loc);
        if (user.getHidden()) {
            if (user.getHiddenReason() == CosmeticUser.HiddenReason.WORLDGUARD && set.getRegions().size() == 0) {
                user.showCosmetics();
            }
        }
        for (ProtectedRegion protectedRegion : set.getRegions()) {
            if (protectedRegion.getFlags().containsKey(WGHook.getCosmeticEnableFlag())) {
                if (protectedRegion.getFlags().get(WGHook.getCosmeticEnableFlag()).toString().equalsIgnoreCase("ALLOW")) {
                    if (user.getHiddenReason() == CosmeticUser.HiddenReason.WORLDGUARD) user.showCosmetics();
                    return;
                }
                user.hideCosmetics(CosmeticUser.HiddenReason.WORLDGUARD);
                return;
            }
            if (protectedRegion.getFlags().containsKey(WGHook.getCosmeticWardrobeFlag())) {
                if (!WardrobeSettings.getWardrobeNames().contains(protectedRegion.getFlags().get(WGHook.getCosmeticWardrobeFlag()).toString())) return;
                Wardrobe wardrobe = WardrobeSettings.getWardrobe(protectedRegion.getFlags().get(WGHook.getCosmeticWardrobeFlag()).toString());
                user.enterWardrobe(true, wardrobe);
            }
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        CosmeticUser user = CosmeticUsers.getUser(event.getPlayer());
        if (user == null) return;
        Location location = event.getTo();
        com.sk89q.worldedit.util.Location loc = BukkitAdapter.adapt(location);
        RegionContainer region = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = region.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(loc);
        if (user.getHidden()) {
            if (user.getHiddenReason() == CosmeticUser.HiddenReason.WORLDGUARD && set.getRegions().size() == 0) {
                user.showCosmetics();
            }
        }
        for (ProtectedRegion protectedRegion : set.getRegions()) {
            if (protectedRegion.getFlags().containsKey(WGHook.getCosmeticEnableFlag())) {
                if (protectedRegion.getFlags().get(WGHook.getCosmeticEnableFlag()).toString().equalsIgnoreCase("ALLOW")) {
                    if (user.getHiddenReason() == CosmeticUser.HiddenReason.WORLDGUARD) user.showCosmetics();
                    return;
                }
                user.hideCosmetics(CosmeticUser.HiddenReason.WORLDGUARD);
                return;
            }
            if (protectedRegion.getFlags().containsKey(WGHook.getCosmeticWardrobeFlag())) {
                if (!WardrobeSettings.getWardrobeNames().contains(protectedRegion.getFlags().get(WGHook.getCosmeticWardrobeFlag()).toString())) return;
                Wardrobe wardrobe = WardrobeSettings.getWardrobe(protectedRegion.getFlags().get(WGHook.getCosmeticWardrobeFlag()).toString());
                user.enterWardrobe(true, wardrobe);
            }
        }
    }
}
