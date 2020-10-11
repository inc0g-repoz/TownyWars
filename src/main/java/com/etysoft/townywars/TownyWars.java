package com.etysoft.townywars;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.EconomyException;
import com.palmergames.bukkit.towny.object.Town;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class TownyWars extends JavaPlugin {
    public Plugin towny;
    public static TownyWars instance;
    public WarManager wm;
    public boolean isPreRelease = false;
    private String supported = "0.96.2.19";
    public boolean discord;
    public static String latestVersion;

    public static void callEvent(Event event) {
        if (event == null) {
            Bukkit.getConsoleSender().sendMessage("TownyWars error: Event is null!");
            return;
        }

        if (Bukkit.getPluginManager() == null) {
            Bukkit.getConsoleSender().sendMessage("TownyWars error: pm is null!");
            return;
        }

        Bukkit.getPluginManager().callEvent(event);
    }

    public boolean isCompatible(String version) {
        return supported.contains(version);
    }

    public void ConfigInit() {
        getConfig().addDefault("msg-compatible", "&aCompatible with current Towny version");
        getConfig().addDefault("msg-nocompatible", "&cWasn't tested with Towny %s");
        getConfig().addDefault("msg-declare", "&b%s &cdeclared war on &6%j!");
        getConfig().addDefault("msg-end", "&b%s &cwin war on &6%j!");
        getConfig().addDefault("msg-notown", "&cYou don't belong to a town!");
        getConfig().addDefault("msg-wrtown", "&cWrong town!");
        getConfig().addDefault("msg-ntown", "&cNeutral town can't be in war!");
        getConfig().addDefault("msg-wrtown", "&cWrong town!");
        getConfig().addDefault("msg-war", "&bCurrent wars: ");
        getConfig().addDefault("msg-nlist", "&aNeutral towns: ");
        getConfig().addDefault("msg-non", "&cNow your town no longer neutral");
        getConfig().addDefault("msg-noff", "&aNow your town is neutral");
        getConfig().addDefault("msg-listde", "&cNo neutral towns!");
        getConfig().addDefault("msg-warde", "&cNo active wars!");
        getConfig().addDefault("msg-tde", "&aTown doesn't exists!");

        getConfig().addDefault("public-announce-neutral", 2);
        getConfig().addDefault("public-announce-warstart", 2);
        getConfig().addDefault("public-announce-warend", 2);

        getConfig().addDefault("no-args", "&cWrong arguments.");
        getConfig().addDefault("no-args", "&cWrong arguments.");

        getConfig().addDefault("msg-win", "&aWinner! &bYour town win the war and now you have all territory of loser town!");
        getConfig().addDefault("msg-lose", "&cLose! &bYour town lose the war and now all your territory has another town!");

        getConfig().addDefault("trfeatures", true);
        getConfig().addDefault("only-town-delete", false);
        getConfig().addDefault("bye-bye", "&6Пока-пока, &b%s.&6 F.");
        saveDefaultConfig();
        reloadConfig();
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        if(isPreRelease) {
            Bukkit.getConsoleSender().sendMessage("You are using the pre-release of TownyWars! If any errors occur, please contact Discord using the link https://discord.gg/Etd4XXH");
        }
        latestVersion = getDescription().getVersion();
        if(getServer().getPluginManager().getPlugin("Towny") == null) {
            Bukkit.getConsoleSender().sendMessage("Towny not found. Disabling...");
            Bukkit.getPluginManager().disablePlugin(this);
        } else {
            towny = getServer().getPluginManager().getPlugin("Towny");
            Bukkit.getConsoleSender().sendMessage("Using Towny " + towny.getDescription().getVersion());
            if(isCompatible(towny.getDescription().getVersion())) {
                Bukkit.getConsoleSender().sendMessage("Towny version was tested with plugin.");
            } else {
                Bukkit.getConsoleSender().sendMessage("Towny version wasn't tested with TownyWars!");
            }

            // Deprecated, will be removed in future updates
            try {
                Bukkit.getConsoleSender().sendMessage("Initializing Towny plugin before WarManager started for correct data...");
                getServer().getPluginManager().enablePlugin(Objects.requireNonNull(getServer().getPluginManager().getPlugin("Towny")));

                Bukkit.getConsoleSender().sendMessage("TownyWars was successfully enabled Towny");
            }
          catch (Exception e) {
              Bukkit.getConsoleSender().sendMessage("TownyWars can't enable Towny(is it already enabled?)");
          }
        }

        isGood();
        if (getConfig().getDouble("config-ver") != 1.4) {
            Bukkit.getConsoleSender().sendMessage("Outdated configuration file!");
        }

        getServer().getPluginManager().registerEvents(new Listeners(), this);
        instance = this;
        Objects.requireNonNull(getCommand("townwars")).setExecutor(new Commands(this));
        Objects.requireNonNull(getCommand("townwars")).setTabCompleter((sender, command, alias, args) -> {
            List<String> tabs = new ArrayList<>();
            if(args.length == 1) {
                tabs.add("declare");
                tabs.add("n");
                tabs.add("st");
                tabs.add("nlist");
                tabs.add("joinwar");
                tabs.add("end");
                if(sender.hasPermission("twar.admin")) {
                    tabs.add("reload");
                    tabs.add("fend");
                    tabs.add("fdeclare");
                }

                tabs.add("info");
                tabs.add("help");
                tabs.add("canceljw");
                tabs.add("invite");
            } else {
                if(args[0].equals("declare") || args[0].equals("fend") || args[0].equals("invite")) {
                    List<Town> towns = TownyUniverse.getInstance().getDataSource().getTowns();
                    for(Town t : WarManager.getInstance().getNeutralTowns()) {
                        towns.remove(t);
                    }

                    for(Town t : towns) {
                        tabs.add(t.getName());
                    }
                } else if (args[0].equals("n") || args[0].equals("st") || args[0].equals("joinwar") || args[0].equals("fdeclare")) {
                    List<Town> towns = TownyUniverse.getInstance().getDataSource().getTowns();
                    for(Town t : towns) {
                        tabs.add(t.getName());
                    }
                }
            }

            List<String> finals = new ArrayList<>();
            for(String s : tabs) {
                if(args.length == 1) {
                    if (s.contains(args[0])) {
                        finals.add(s);
                    }
                } else if(args.length == 2) {
                    if (s.contains(args[1])) {
                        finals.add(s);
                    }
                } else if (args.length == 3) {
                    if (args[0].equals("fdeclare")) {
                        finals.add(s);
                    }
                }
            }

            return finals;
        });

        Bukkit.getConsoleSender().sendMessage("Initializing bStats metrics...");
        try {
            int pluginId = 7801; // <-- Replace with the id of your plugin!
            new Metrics(this, pluginId);
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("Can't initialize metrics!");
        }

        Bukkit.getConsoleSender().sendMessage("Initializing config.yml...");
        ConfigInit();
        Bukkit.getConsoleSender().sendMessage("Checking for updates...");
        Thread updateChecker = new Thread(new Runnable() {
            @Override
            public void run() {
                checkUpdates();
            }
        });
        updateChecker.start();

        wm = new WarManager();

        try {
            Town t = new Town("test");
            t.getAccount().getHoldingBalance();
            Bukkit.getConsoleSender().sendMessage("TownWars " + this.getDescription().getVersion() + " successfully enabled!");
        } catch (NoSuchMethodError | EconomyException e) {
            Bukkit.getConsoleSender().sendMessage("[CRITICAL ERROR]: Old Towny economy (Use latest version of Towny)! Disabling TownyWars...");
            Bukkit.getPluginManager().disablePlugin(this);
        } catch (Exception ignored) { }

        discord = getConfig().getBoolean("activate-discord");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        DataManager.saveNeutrals(WarManager.getInstance().getNeutralTowns());
        Bukkit.getConsoleSender().sendMessage("TownWars " + this.getDescription().getVersion() + " successfully disabled!");
    }

    public void isGood() {
        try {
            TownyUniverse.getInstance().getDataSource();
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("You are using an incompatible version of TownyWars with your version of Towny! (DataSource, old Towny version)");
        }
    }

    public void sendDiscord(String message) {
        TextChannel textChannel = DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(getConfig().getString("channel-name"));
        String prefix = getConfig().getString("message-prefix");
        textChannel.sendMessage(prefix + message).queue();
    }

    public boolean checkUpdates() {
        try {
            String url = "https://api.spigotmc.org/legacy/update.php?resource=80038/";

            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();


            if (response.toString().equals(getDescription().getVersion())) {
                Bukkit.getConsoleSender().sendMessage("You are using latest version of TownyWars!");
                return false;
            } else {
                Bukkit.getConsoleSender().sendMessage("New version of TownyWars has been found! (" + response.toString() + ")");
                latestVersion = response.toString();
                return true;
            }
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage("Can't check updates!");
            e.printStackTrace();
            return false;
        }
    }
}
