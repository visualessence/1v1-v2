package gg.larp.onevone;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor {

    private final MatchManager mm;
    private final OneVsOne plugin;

    public CommandHandler(MatchManager mm, OneVsOne plugin) {
        this.mm = mm;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        switch (cmd.getName().toLowerCase()) {

            case "1v1" -> {
                if (args.length < 2) { sender.sendMessage(err("/1v1 <player1> <player2>")); return true; }
                Player a = Bukkit.getPlayerExact(args[0]);
                Player b = Bukkit.getPlayerExact(args[1]);
                if (a == null || b == null) { sender.sendMessage(err("One or both players offline.")); return true; }
                String fail = mm.startMatch(a, b);
                if (fail != null) { sender.sendMessage(err(fail)); return true; }
                Bukkit.broadcast(Component.text("⚔ 1v1: ", NamedTextColor.YELLOW)
                    .append(Component.text(a.getName(), NamedTextColor.AQUA))
                    .append(Component.text(" vs ", NamedTextColor.GRAY))
                    .append(Component.text(b.getName(), NamedTextColor.RED)));
            }

            case "addpoint" -> {
                if (args.length < 1) { sender.sendMessage(err("/addpoint <player>")); return true; }
                Match m = mm.getMatch(args[0]);
                if (m == null) { sender.sendMessage(err(args[0] + " not in a match.")); return true; }
                m.addPoint(args[0]);
            }

            case "win" -> {
                if (args.length < 1) { sender.sendMessage(err("/win <player>")); return true; }
                Match m = mm.getMatch(args[0]);
                if (m == null) { sender.sendMessage(err(args[0] + " not in a match.")); return true; }
                m.declareWin(args[0]);
                Bukkit.broadcast(Component.text("★ " + args[0] + " has won the 1v1!", NamedTextColor.GOLD));
                Bukkit.getScheduler().runTaskLater(plugin,
                    () -> mm.endMatch(args[0]), 160L);
            }
        }
        return true;
    }

    private Component err(String msg) {
        return Component.text("✗ ", NamedTextColor.RED)
            .append(Component.text(msg, NamedTextColor.GRAY));
    }
}
