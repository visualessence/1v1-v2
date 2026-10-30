package gg.larp.onevone;

import org.bukkit.plugin.java.JavaPlugin;

public class OneVsOne extends JavaPlugin {

    private MatchManager matchManager;

    @Override
    public void onEnable() {
        matchManager = new MatchManager(this);
        CommandHandler h = new CommandHandler(matchManager, this);
        getCommand("1v1").setExecutor(h);
        getCommand("addpoint").setExecutor(h);
        getCommand("win").setExecutor(h);
        getServer().getPluginManager().registerEvents(new FreezeListener(matchManager), this);
    }

    @Override
    public void onDisable() {
        matchManager.clearAll();
    }
}
