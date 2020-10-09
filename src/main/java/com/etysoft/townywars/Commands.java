package com.etysoft.townywars;

import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.EconomyException;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;


import java.util.Objects;
import java.util.Set;

public class Commands implements CommandExecutor {
    public static TownyWars instance;
    public Commands(TownyWars TownyWars) {
        instance = TownyWars;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equals("twar")) {
            try {
                if (args.length > 0) {
                    switch (args[0]) {
                        case "info":
                            if (sender.hasPermission("twar.use")) {
                                Info.plugin(sender, instance, instance.towny);
                            } else {
                                sender.sendMessage(ColorCodes.toColor(instance.getConfig().getString("no-perm")));
                            }
                            break;
                        case "reload":
                            if (sender.hasPermission("twar.admin")) {
                                instance.reloadConfig();
                                instance.ConfigInit();
                                sender.sendMessage(ColorCodes.toColor("&aSuccessfully reloaded configs!"));
                            } else {
                                sender.sendMessage(ColorCodes.toColor(instance.getConfig().getString("no-perm")));
                            }
                            break;
                        case "declare":
                            if (sender.hasPermission("twar.mayor")) {
                                if (sender instanceof Player) {
                                    Player p = (Player) sender;
                                    try {
                                        Resident r = TownyUniverse.getInstance().getDataSource().getResident(p.getName());
                                        if (r.hasTown()) {
                                            if (args.length > 1) {
                                                if (r.getTown().getAccount().getHoldingBalance() >= TownyWars.instance.getConfig().getDouble("price-declare")) {
                                                    if (TownyUniverse.getInstance().getDataSource().hasTown(args[1])) {
                                                        Town tod = TownyUniverse.getInstance().getDataSource().getTown(args[1]);
                                                        if (Objects.requireNonNull(Bukkit.getPlayer(tod.getMayor().getName())).isOnline() | !TownyWars.instance.getConfig().getBoolean("mayor-online")) {
                                                            if (tod != null) {
                                                                if (!WarManager.getInstance().isNeutral(tod) && !WarManager.getInstance().isNeutral(r.getTown())) {
                                                                    if (!WarManager.getInstance().isInWar(r.getTown())) {
                                                                        boolean success = WarManager.getInstance().declare(r.getTown(), tod);
                                                                        if (!success) {
                                                                            p.sendMessage(ColorCodes.toColor(instance.getConfig().getString("msg-wrtown")));
                                                                        } else {
                                                                            r.getTown().getAccount().pay(TownyWars.instance.getConfig().getDouble("price-declare"), "War declaration");
                                                                        }
                                                                    }
                                                                } else {
                                                                    p.sendMessage(ColorCodes.toColor(instance.getConfig().getString("msg-ntown")));
                                                                }
                                                            } else {
                                                                p.sendMessage(ColorCodes.toColor(instance.getConfig().getString("msg-wrtown")));
                                                            }
                                                        }
                                                    } else {
                                                        sender.sendMessage(ColorCodes.toColor(instance.getConfig().getString("msg-online")));
                                                    }
                                                } else {
                                                    p.sendMessage(ColorCodes.toColor(Objects.requireNonNull(instance.getConfig().getString("msg-money")).replace("%s", TownyWars.instance.getConfig().getDouble("price-declare") + "")));
                                                }
                                            } else {
                                                sender.sendMessage(ColorCodes.toColor(instance.getConfig().getString("no-args")));
                                            }
                                        } else {
                                            p.sendMessage(ColorCodes.toColor(instance.getConfig().getString("msg-notown")));
                                        }
                                    } catch (NotRegisteredException | EconomyException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    sender.sendMessage("You can't do it from Console!");
                                }
                            } else {
                                sender.sendMessage(ColorCodes.toColor(instance.getConfig().getString("no-perm")));
                            }
                            break;
                        case "st":
                            if (WarManager.getInstance().getWars().size() > 0) {
                                if (args.length > 1) {
                                    FileConfiguration c = instance.getConfig();
                                    try {
                                        if (WarManager.getInstance().isInWar(TownyUniverse.getInstance().getDataSource().getTown(args[1]))) {
                                            War w = WarManager.getInstance().getTownWar(TownyUniverse.getInstance().getDataSource().getTown(args[1]));
                                            sender.sendMessage(ColorCodes.toColor(Objects.requireNonNull(c.getString("msg-warin1")).replace("%s", args[1])));
                                            StringBuilder am = new StringBuilder();
                                            StringBuilder jm = new StringBuilder();
                                            for (Town t :
                                                    w.getATowns()) {
                                                am.append(t.getName()).append("; ");
                                            }

                                            for (Town t :
                                                    w.getVTowns()) {
                                                jm.append(t.getName()).append("; ");
                                            }

                                            sender.sendMessage(ColorCodes.toColor(Objects.requireNonNull(c.getString("msg-warin2")).replace("%s", w.getAttacker().getName()) + am));
                                            sender.sendMessage(ColorCodes.toColor(Objects.requireNonNull(c.getString("msg-warin2")).replace("%s", w.getVictim().getName()) + jm));
                                            sender.sendMessage(ColorCodes.toColor(Objects.requireNonNull(c.getString("msg-warin3")).replace("%s", w.getAttacker().getName()).replace("%k", w.getAPoints() + "").replace("%j", w.getVictim().getName()).replace("%y", w.getVPoints() + "")));
                                        } else {
                                            sender.sendMessage(ColorCodes.toColor(c.getString("msg-peace")));
                                        }
                                    } catch (NotRegisteredException e) {
                                        sender.sendMessage(ColorCodes.toColor(instance.getConfig().getString("msg-notown")));
                                    }
                                } else {
                                    sender.sendMessage(ColorCodes.toColor(TownyWars.instance.getConfig().getString("msg-war")));
                                    for (War w :
                                            WarManager.getInstance().getWars()) {
                                        String members = "";
                                        if (w.getATowns().size() != 0 && w.getATowns().size() != 0) {
                                            int m = w.getATowns().size() + w.getATowns().size();
                                            members = " [" + m + "]";
                                        }
                                        sender.sendMessage(ColorCodes.toColor("&e" + w.getAttacker().getName() + "&f(&b" + w.getAPoints() + ") VS " + "&e" + w.getVictim().getName() + "&f(&b" + w.getVPoints() + ")" + members));
                                    }
                                }
                            } else {
                                sender.sendMessage(ColorCodes.toColor(TownyWars.instance.getConfig().getString("msg-warde")));
                            }
                            break;
                        case "fend":
                            if (sender.hasPermission("twar.admin")) {
                                if (args.length > 1) {
                                    try {
                                        Town t = TownyUniverse.getInstance().getDataSource().getTown(args[1]);
                                        War w = WarManager.getInstance().getTownWar(t);
                                        WarManager.getInstance().end(w, false);
                                        sender.sendMessage("War " + w.getVictim() + " VS " + w.getAttacker() + " stopped without pain ;)");
                                    } catch (NotRegisteredException e) {
                                        sender.sendMessage("Not registered!");
                                    }
                                }
                            } else {
                                sender.sendMessage(ColorCodes.toColor(instance.getConfig().getString("no-perm")));
                            }
                            break;
                        case "help":
                            if (sender.hasPermission("twar.use")) {
                                Info.help(sender, TownyWars.instance);
                            } else {
                                sender.sendMessage(ColorCodes.toColor(instance.getConfig().getString("no-perm")));
                            }
                            break;
                        case "end":
                            if (sender.hasPermission("twar.mayor")) {
                                try {
                                    if (sender instanceof Player) {
                                        Player p = (Player) sender;
                                        Resident r = TownyUniverse.getInstance().getDataSource().getResident(p.getName());
                                        if (WarManager.getInstance().getTownWar(r.getTown()) != null) {
                                            War w = WarManager.getInstance().getTownWar(r.getTown());
                                            Town tosend = w.getOppositeTown(r.getTown());
                                            if (w.fromreqtown == tosend) {
                                                TownyMessaging.sendTownMessagePrefixed(tosend, (ColorCodes.toColor(TownyWars.instance.getConfig().getString("msg-ended"))));
                                                TownyMessaging.sendTownMessagePrefixed(r.getTown(), (ColorCodes.toColor(TownyWars.instance.getConfig().getString("msg-ended"))));
                                                WarManager.getInstance().end(w, false);

                                                return true;
                                            }

                                            Resident mayor = tosend.getMayor();
                                            if (Bukkit.getPlayer(mayor.getName()) != null) {
                                                w.fromreqtown = r.getTown();
                                                sender.sendMessage(ColorCodes.toColor(TownyWars.instance.getConfig().getString("msg-sendreqend")));
                                                Objects.requireNonNull(Bukkit.getPlayer(mayor.getName())).sendMessage(ColorCodes.toColor(Objects.requireNonNull(TownyWars.instance.getConfig().getString("msg-reqend")).replace("%s", r.getTown().getName())));
                                            } else {
                                                sender.sendMessage(ColorCodes.toColor(TownyWars.instance.getConfig().getString("msg-mayoroffline")));
                                            }
                                        } else {
                                            sender.sendMessage(ColorCodes.toColor(instance.getConfig().getString("msg-wrtown")));
                                        }
                                    } else {
                                        sender.sendMessage("You can't do it from console!");
                                    }
                                } catch (Exception ignored) { }
                            } else {
                                sender.sendMessage(ColorCodes.toColor(instance.getConfig().getString("no-perm")));
                            }
                            break;
                        case "fdeclare":
                            if (sender.hasPermission("twar.admin")) {
                                if (args.length > 2) {
                                    try {
                                        Town a = TownyUniverse.getInstance().getDataSource().getTown(args[1]);
                                        Town v = TownyUniverse.getInstance().getDataSource().getTown(args[2]);
                                        if (WarManager.getInstance().declare(a, v)) {
                                            sender.sendMessage("Successfully declared war from " + args[1] + " to " + args[2]);
                                        } else {
                                            sender.sendMessage("Can't start war! (Is already in war?)");
                                        }
                                    } catch (NotRegisteredException e) {
                                        sender.sendMessage(ColorCodes.toColor(instance.getConfig().getString("msg-wrtown")));
                                    }
                                } else {
                                    sender.sendMessage(ColorCodes.toColor(instance.getConfig().getString("no-args")));
                                }
                            } else {
                                sender.sendMessage(ColorCodes.toColor(instance.getConfig().getString("no-perm")));
                            }
                            break;
                        case "joinwar":
                            if (sender.hasPermission("twar.mayor")) {
                                Player p = (Player) sender;
                                try {
                                    Resident r = TownyUniverse.getInstance().getDataSource().getResident(p.getName());
                                    if (!WarManager.getInstance().isSendedJoinRequest(r.getTown())) {
                                        if (args.length > 1) {
                                            Town t = TownyUniverse.getInstance().getDataSource().getTown(args[1]);
                                            WarManager.getInstance().sendJoinRequest(r.getTown(), t, p);
                                        } else {
                                            sender.sendMessage(ColorCodes.toColor(instance.getConfig().getString("no-args")));
                                        }
                                    } else {
                                        sender.sendMessage(ColorCodes.toColor(instance.getConfig().getString("msg-sended")));
                                    }
                                } catch (Exception ignored) { }
                            } else {
                                sender.sendMessage(ColorCodes.toColor(instance.getConfig().getString("no-perm")));
                            }
                            break;
                        case "canceljw":
                            if (sender.hasPermission("twar.mayor")) {
                                Player p = (Player) sender;
                                try {
                                    Resident r = TownyUniverse.getInstance().getDataSource().getResident(p.getName());
                                    if (WarManager.getInstance().isSendedJoinRequest(r.getTown())) {
                                        WarManager.getInstance().removeJoinRequest(r.getTown());
                                        sender.sendMessage(ColorCodes.toColor(instance.getConfig().getString("msg-cancel")));

                                    } else {
                                        sender.sendMessage(ColorCodes.toColor(instance.getConfig().getString("msg-noreq")));
                                    }
                                } catch (Exception ignored) { }
                            } else {
                                sender.sendMessage(ColorCodes.toColor(instance.getConfig().getString("no-perm")));
                            }
                            break;
                        case "invite":
                            if (sender.hasPermission("twar.mayor")) {
                                Player p = (Player) sender;
                                try {
                                    Resident r = TownyUniverse.getInstance().getDataSource().getResident(p.getName());
                                    if (args.length > 1) {
                                        Town from = TownyUniverse.getInstance().getDataSource().getTown(args[1]);
                                        if (!WarManager.getInstance().isInWar(from)) {
                                            boolean a = false;
                                            if (WarManager.getInstance().getTownWar(r.getTown()).getAttacker() == r.getTown()) {
                                                a = true;
                                            }

                                            if (WarManager.getInstance().hasJoinRequest(from, r.getTown())) {
                                                WarManager.getInstance().addTownToWar(from, WarManager.getInstance().getTownWar(r.getTown()), a);
                                                sender.sendMessage(ColorCodes.toColor(instance.getConfig().getString("msg-accept")));
                                            } else {
                                                sender.sendMessage(ColorCodes.toColor(instance.getConfig().getString("msg-norecrec")));
                                            }
                                        } else {
                                            sender.sendMessage(ColorCodes.toColor(instance.getConfig().getString("msg-inwar")));
                                        }
                                    } else {
                                        sender.sendMessage(ColorCodes.toColor(instance.getConfig().getString("no-args")));
                                    }
                                } catch (Exception e) {
                                    sender.sendMessage(ColorCodes.toColor(instance.getConfig().getString("msg-notown")));
                                }
                            } else {
                                sender.sendMessage(ColorCodes.toColor(instance.getConfig().getString("no-perm")));
                            }
                            break;
                        case "n":
                            if (args.length == 1) {
                                if (sender instanceof Player) {
                                    if (sender.hasPermission("twar.mayor")) {
                                        try {
                                            Resident r = TownyUniverse.getInstance().getDataSource().getResident(sender.getName());
                                            if (r.hasTown()) {
                                                if (!WarManager.getInstance().isInWar(r.getTown())) {
                                                    if (r.getTown().getAccount().getHoldingBalance() >= TownyWars.instance.getConfig().getDouble("price-neutral")) {
                                                        int nmessage = TownyWars.instance.getConfig().getInt("public-announce-neutral");
                                                        if (WarManager.getInstance().isNeutral(r.getTown())) {
                                                            WarManager.getInstance().setNeutrality(false, r.getTown());
                                                            if (nmessage == 3) {
                                                                sender.sendMessage(ColorCodes.toColor(Objects.requireNonNull(TownyWars.instance.getConfig().getString("msg-noff")).replace("%s", r.getTown().getName())));
                                                            } else if (nmessage == 1) {
                                                                TownyMessaging.sendTownMessagePrefixed(r.getTown(), ColorCodes.toColor(Objects.requireNonNull(TownyWars.instance.getConfig().getString("msg-noff")).replace("%s", r.getTown().getName())));
                                                            } else if (nmessage == 2) {
                                                                TownyMessaging.sendGlobalMessage(ColorCodes.toColor(Objects.requireNonNull(TownyWars.instance.getConfig().getString("msg-noff")).replace("%s", r.getTown().getName())));
                                                            }
                                                        } else {
                                                            WarManager.getInstance().setNeutrality(true, r.getTown());
                                                            r.getTown().getAccount().withdraw(TownyWars.instance.getConfig().getDouble("price-neutral"), "Neutrality cost");
                                                            if (nmessage == 3) {
                                                                sender.sendMessage(ColorCodes.toColor(Objects.requireNonNull(TownyWars.instance.getConfig().getString("msg-non")).replace("%s", r.getTown().getName())));
                                                            } else if (nmessage == 1) {
                                                                TownyMessaging.sendTownMessagePrefixed(r.getTown(), ColorCodes.toColor(Objects.requireNonNull(TownyWars.instance.getConfig().getString("msg-non")).replace("%s", r.getTown().getName())));
                                                            } else if (nmessage == 2) {
                                                                TownyMessaging.sendGlobalMessage(ColorCodes.toColor(Objects.requireNonNull(TownyWars.instance.getConfig().getString("msg-non")).replace("%s", r.getTown().getName())));
                                                            }
                                                        }
                                                    } else {
                                                        sender.sendMessage(ColorCodes.toColor(Objects.requireNonNull(instance.getConfig().getString("msg-money")).replace("%s", TownyWars.instance.getConfig().getDouble("price-neutral") + "")));
                                                    }
                                                } else {
                                                    sender.sendMessage(ColorCodes.toColor(instance.getConfig().getString("msg-ninwar")));
                                                }
                                            } else {
                                                sender.sendMessage(ColorCodes.toColor(Objects.requireNonNull(instance.getConfig().getString("msg-money")).replace("%s", TownyWars.instance.getConfig().getDouble("price-neutral") + "")));
                                            }
                                        } catch (Exception e) {
                                            Bukkit.getConsoleSender().sendMessage("TOWNYWARS CATCH AN ERROR:");
                                            e.printStackTrace();
                                            Bukkit.getConsoleSender().sendMessage("ERROR IN NEUTRALITY TOGGLE");
                                        }
                                    } else {
                                        sender.sendMessage(ColorCodes.toColor(instance.getConfig().getString("no-perm")));
                                    }
                                } else {
                                    sender.sendMessage("You can't do it from Console!");
                                }
                            } else {
                                try {
                                    if (sender.hasPermission("twar.admin")) {
                                        Town t = TownyUniverse.getInstance().getDataSource().getTown(args[1]);
                                        int nmessage = TownyWars.instance.getConfig().getInt("public-announce-neutral");
                                        if (WarManager.getInstance().isNeutral(t)) {
                                            WarManager.getInstance().setNeutrality(false, t);
                                            if (nmessage == 3) {
                                                sender.sendMessage(ColorCodes.toColor(Objects.requireNonNull(TownyWars.instance.getConfig().getString("msg-noff")).replace("%s", t.getName())));
                                            } else if (nmessage == 1) {
                                                TownyMessaging.sendTownMessagePrefixed(t, ColorCodes.toColor(Objects.requireNonNull(TownyWars.instance.getConfig().getString("msg-noff")).replace("%s", t.getName())));
                                            } else if (nmessage == 2) {
                                                TownyMessaging.sendGlobalMessage(ColorCodes.toColor(Objects.requireNonNull(TownyWars.instance.getConfig().getString("msg-noff")).replace("%s", t.getName())));
                                            }
                                        } else {
                                            WarManager.getInstance().setNeutrality(true, t);
                                            if (nmessage == 3) {
                                                sender.sendMessage(ColorCodes.toColor(Objects.requireNonNull(TownyWars.instance.getConfig().getString("msg-non")).replace("%s", t.getName())));
                                            } else if (nmessage == 1) {
                                                TownyMessaging.sendTownMessagePrefixed(t, ColorCodes.toColor(Objects.requireNonNull(TownyWars.instance.getConfig().getString("msg-non")).replace("%s", t.getName())));
                                            } else if (nmessage == 2) {
                                                TownyMessaging.sendGlobalMessage(ColorCodes.toColor(Objects.requireNonNull(TownyWars.instance.getConfig().getString("msg-non")).replace("%s", t.getName())));
                                            }
                                        }
                                    } else {
                                        sender.sendMessage(ColorCodes.toColor(instance.getConfig().getString("no-perm")));
                                    }
                                } catch (Exception e) {
                                    sender.sendMessage(ColorCodes.toColor(instance.getConfig().getString("msg-wrtown")));
                                }
                            }
                            break;
                        case "nlist":
                            if (sender.hasPermission("twar.use")) {
                                String list = "";
                                Set<Town> ts = WarManager.getInstance().getNeutralTowns();
                                if (ts.size() > 0) {
                                    boolean isfirst = true;
                                    for (Town t :
                                            ts) {
                                        if (isfirst) {
                                            list = "&e" + t.getName();
                                            isfirst = false;
                                        } else {
                                            list = list + "&f, &e" + t.getName();
                                        }
                                    }
                                    sender.sendMessage(ColorCodes.toColor(TownyWars.instance.getConfig().getString("msg-nlist")));
                                    sender.sendMessage(ColorCodes.toColor(list));
                                } else {
                                    sender.sendMessage(ColorCodes.toColor(TownyWars.instance.getConfig().getString("msg-listde")));
                                }
                            } else {
                                sender.sendMessage(ColorCodes.toColor(instance.getConfig().getString("no-perm")));
                            }
                            break;
                        default:
                            sender.sendMessage(ColorCodes.toColor(instance.getConfig().getString("no-args")));
                            break;
                    }
                } else {
                    sender.sendMessage(ColorCodes.toColor(instance.getConfig().getString("no-args")));
                }
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage("TownyWars catch an error (Is Towny outdated?): ");
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
}
