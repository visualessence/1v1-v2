package gg.larp.onevone;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class MatchManager {

    private final Map<String, Match> byPlayer = new HashMap<>();
    private final OneVsOne plugin;

    public MatchManager(OneVsOne plugin) {
        this.plugin = plugin;
    }

    // returns error string or null on success
    public String startMatch(Player a, Player b) {
        if (byPlayer.containsKey(key(a.getName())))
            return a.getName() + " is already in a match.";
        if (byPlayer.containsKey(key(b.getName())))
            return b.getName() + " is already in a match.";

        Match m = new Match(a, b);
        byPlayer.put(key(a.getName()), m);
        byPlayer.put(key(b.getName()), m);
        runCountdown(m);
        return null;
    }

    public Match getMatch(String name) {
        return byPlayer.get(key(name));
    }

    public Match getMatchByUUID(java.util.UUID id) {
        return byPlayer.values().stream()
            .distinct()
            .filter(m -> m.isParticipant(id))
            .findFirst().orElse(null);
    }

    public void endMatch(String name) {
        Match m = byPlayer.get(key(name));
        if (m == null) return;
        m.removeBar();
        byPlayer.remove(key(m.getNameA()));
        byPlayer.remove(key(m.getNameB()));
    }

    public void clearAll() {
        byPlayer.values().stream().distinct().forEach(Match::removeBar);
        byPlayer.clear();
    }

    // ── countdown: 3 → 2 → 1 → GO, players frozen throughout ──
    private void runCountdown(Match m) {
        net.kyori.adventure.text.format.TextColor red    = net.kyori.adventure.text.format.NamedTextColor.RED;
        net.kyori.adventure.text.format.TextColor yellow = net.kyori.adventure.text.format.NamedTextColor.YELLOW;
        net.kyori.adventure.text.format.TextColor green  = net.kyori.adventure.text.format.NamedTextColor.GREEN;

        // tick 0 → show 3
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            m.titleBoth(
                net.kyori.adventure.text.Component.text("3", red)
                    .decorate(net.kyori.adventure.text.format.TextDecoration.BOLD),
                net.kyori.adventure.text.Component.text("Get ready!", net.kyori.adventure.text.format.NamedTextColor.GRAY)
            );
        }, 0L);

        // tick 20 → show 2
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            m.titleBoth(
                net.kyori.adventure.text.Component.text("2", yellow)
                    .decorate(net.kyori.adventure.text.format.TextDecoration.BOLD),
                net.kyori.adventure.text.Component.empty()
            );
        }, 20L);

        // tick 40 → show 1
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            m.titleBoth(
                net.kyori.adventure.text.Component.text("1", yellow)
                    .decorate(net.kyori.adventure.text.format.TextDecoration.BOLD),
                net.kyori.adventure.text.Component.empty()
            );
        }, 40L);

        // tick 60 → GO, unfreeze
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            m.unfreeze();
            m.titleBoth(
                net.kyori.adventure.text.Component.text("GO!", green)
                    .decorate(net.kyori.adventure.text.format.TextDecoration.BOLD),
                net.kyori.adventure.text.Component.empty()
            );
        }, 60L);
    }

    private String key(String n) { return n.toLowerCase(); }
}
