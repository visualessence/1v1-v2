package gg.larp.onevone;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class FreezeListener implements Listener {

    private final MatchManager mm;

    public FreezeListener(MatchManager mm) {
        this.mm = mm;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Match m = mm.getMatchByUUID(p.getUniqueId());
        if (m == null || !m.isFrozen()) return;

        // allow head rotation, block position change
        if (e.getFrom().getBlockX() != e.getTo().getBlockX()
         || e.getFrom().getBlockY() != e.getTo().getBlockY()
         || e.getFrom().getBlockZ() != e.getTo().getBlockZ()) {
            e.setTo(e.getFrom().clone()
                .add(0, 0, 0)
                .setDirection(e.getTo().getDirection()));
            // keep yaw/pitch from TO so they can look around, block XYZ
            org.bukkit.Location fixed = e.getFrom().clone();
            fixed.setYaw(e.getTo().getYaw());
            fixed.setPitch(e.getTo().getPitch());
            e.setTo(fixed);
        }
    }
}
