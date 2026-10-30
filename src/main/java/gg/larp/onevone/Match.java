package gg.larp.onevone;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Match {

    // spawn positions — same world as players at start
    private static final double AX = 37, AY = 64, AZ = 4;
    private static final double BX = 0,  BY = 64, BZ = 4;

    private final String nameA;
    private final String nameB;
    private final UUID idA;
    private final UUID idB;
    private int scoreA = 0;
    private int scoreB = 0;
    private final BossBar bar;
    private boolean frozen = true;

    public Match(Player a, Player b) {
        this.nameA = a.getName();
        this.nameB = b.getName();
        this.idA   = a.getUniqueId();
        this.idB   = b.getUniqueId();

        // teleport to spawn positions
        World w = a.getWorld();
        a.teleport(new Location(w, AX + 0.5, AY, AZ + 0.5, 90f, 0f));  // face each other
        b.teleport(new Location(w, BX + 0.5, BY, BZ + 0.5, 270f, 0f));

        bar = BossBar.bossBar(
            buildTitle(),
            1.0f,
            BossBar.Color.RED,
            BossBar.Overlay.PROGRESS
        );
        a.showBossBar(bar);
        b.showBossBar(bar);
    }

    public boolean isFrozen() { return frozen; }
    public void unfreeze()    { frozen = false; }

    public boolean hasPlayer(String name) {
        return nameA.equalsIgnoreCase(name) || nameB.equalsIgnoreCase(name);
    }

    public boolean isParticipant(UUID id) {
        return idA.equals(id) || idB.equals(id);
    }

    public void addPoint(String name) {
        if (nameA.equalsIgnoreCase(name)) scoreA++;
        else if (nameB.equalsIgnoreCase(name)) scoreB++;
        bar.name(buildTitle());
    }

    public void declareWin(String winner) {
        frozen = true; // re-freeze on win so nobody moves during display
        bar.color(BossBar.Color.YELLOW);
        bar.progress(1.0f);
        bar.name(
            Component.text("★ ", NamedTextColor.GOLD)
                .append(Component.text(winner, NamedTextColor.YELLOW)
                    .decorate(TextDecoration.BOLD))
                .append(Component.text(" has won!", NamedTextColor.GOLD))
        );
    }

    public void sendBoth(Component msg) {
        Player a = Bukkit.getPlayer(idA);
        Player b = Bukkit.getPlayer(idB);
        if (a != null) a.sendMessage(msg);
        if (b != null) b.sendMessage(msg);
    }

    public void titleBoth(Component title, Component sub) {
        Player a = Bukkit.getPlayer(idA);
        Player b = Bukkit.getPlayer(idB);
        if (a != null) a.showTitle(net.kyori.adventure.title.Title.title(title, sub,
            net.kyori.adventure.title.Title.Times.times(
                java.time.Duration.ofMillis(100),
                java.time.Duration.ofMillis(800),
                java.time.Duration.ofMillis(100))));
        if (b != null) b.showTitle(net.kyori.adventure.title.Title.title(title, sub,
            net.kyori.adventure.title.Title.Times.times(
                java.time.Duration.ofMillis(100),
                java.time.Duration.ofMillis(800),
                java.time.Duration.ofMillis(100))));
    }

    public void removeBar() {
        Player a = Bukkit.getPlayer(idA);
        Player b = Bukkit.getPlayer(idB);
        if (a != null) a.hideBossBar(bar);
        if (b != null) b.hideBossBar(bar);
    }

    private Component buildTitle() {
        return Component.text(nameA, NamedTextColor.AQUA)
            .decorate(TextDecoration.BOLD)
            .append(Component.text("  " + scoreA, NamedTextColor.WHITE))
            .append(Component.text("  —  ", NamedTextColor.GRAY))
            .append(Component.text(scoreB + "  ", NamedTextColor.WHITE))
            .append(Component.text(nameB, NamedTextColor.RED)
                .decorate(TextDecoration.BOLD));
    }

    public String getNameA() { return nameA; }
    public String getNameB() { return nameB; }
}
