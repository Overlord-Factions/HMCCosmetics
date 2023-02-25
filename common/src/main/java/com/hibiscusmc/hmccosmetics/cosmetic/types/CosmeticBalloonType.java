package com.hibiscusmc.hmccosmetics.cosmetic.types;

import com.hibiscusmc.hmccosmetics.config.Settings;
import com.hibiscusmc.hmccosmetics.cosmetic.Cosmetic;
import com.hibiscusmc.hmccosmetics.user.manager.UserBalloonManager;
import com.hibiscusmc.hmccosmetics.user.CosmeticUser;
import com.hibiscusmc.hmccosmetics.util.packets.PacketManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.List;

public class CosmeticBalloonType extends Cosmetic {

    private final String modelName;
    private List<String> dyableParts;
    //private HashMap<Animations, String> animationBalloons;

    public CosmeticBalloonType(String id, ConfigurationNode config) {
        super(id, config);

        String modelId = config.node("model").getString();

        try {
            if (!config.node("dyable-parts").virtual()) dyableParts = config.node("dyable-parts").getList(String.class);

            /*
            if (!config.node("animations").virtual()) {
                for (ConfigurationNode animationNode : config.node("animations").childrenMap().values()) {
                    if (EnumUtils.isValidEnum(Animations.class, animationNode.key().toString().toUpperCase())) continue;
                    animationBalloons.put(Animations.valueOf(animationNode.key().toString().toUpperCase()), animationNode.getString());
                }
            }
             */

        } catch (SerializationException e) {
            // Seriously?
            throw new RuntimeException(e);
        }

        this.modelName = modelId;
    }

    @Override
    public void update(@NotNull CosmeticUser user) {
        Player player = Bukkit.getPlayer(user.getUniqueId());
        UserBalloonManager userBalloonManager = user.getBalloonManager();

        if (player == null || userBalloonManager == null) return;
        if (user.isInWardrobe()) return;

        Location newLocation = player.getLocation();
        Location currentLocation = user.getBalloonManager().getLocation();
        newLocation = newLocation.clone().add(Settings.getBalloonOffset());

        List<Player> viewer = PacketManager.getViewers(player.getLocation());
        viewer.add(player);

        if (player.getLocation().getWorld() != userBalloonManager.getLocation().getWorld()) {
            userBalloonManager.getModelEntity().teleport(newLocation);
            PacketManager.sendTeleportPacket(userBalloonManager.getPufferfishBalloonId(), newLocation, false, viewer);
            return;
        }

        Vector velocity = newLocation.toVector().subtract(currentLocation.toVector());
        userBalloonManager.setVelocity(velocity.multiply(1.1));
        userBalloonManager.setLocation(newLocation);

        PacketManager.sendTeleportPacket(userBalloonManager.getPufferfishBalloonId(), newLocation, false, viewer);
        if (!user.getHidden()) PacketManager.sendLeashPacket(userBalloonManager.getPufferfishBalloonId(), player.getEntityId(), viewer);
    }

    public String getModelName() {
        return this.modelName;
    }

    public List<String> getDyableParts() {
        return dyableParts;
    }

    public boolean isDyablePart(String name) {
        // If player does not define parts, dye whole model
        if (dyableParts == null) return true;
        if (dyableParts.isEmpty()) return true;
        return dyableParts.contains(name);
    }

    /*
    public String getAnimation(Animations animation) {
        return animationBalloons.get(animation);
    }
     */
}
