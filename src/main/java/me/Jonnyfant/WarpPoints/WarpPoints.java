package me.Jonnyfant.WarpPoints;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class WarpPoints extends JavaPlugin {
    String configpath = "Amount of WarpPoints";

    public void onEnable() {
        loadConfig();
    }

    public void loadConfig() {
        getConfig().addDefault(configpath, 5);
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String commandName = command.getName();
        switch (commandName.toLowerCase()) {
            case "setwarppointslimit":
                return setWarppointsLimit(args, sender);
            case "getwarppointslimit":
                return getWarpPointsLimit(sender);
            case "warphelp":
                return warpHelp(sender);
            case "warp":
                return warp(sender, args);
            case "setwarp":
                return setWarp(sender, args);
            case "warplist":
                return warplist(args, sender);
            case "deleteunusedwarppoints":
                return deleteUnusedWarpPoints(sender);
            default:
                return false;
        }
    }

    private boolean deleteUnusedWarpPoints(CommandSender sender) {
        if (sender.hasPermission("warppoints.admin")) {
            File[] files = getDataFolder().listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().equals("config.yml") == false) {
                    YamlConfiguration userWarp = YamlConfiguration.loadConfiguration(files[i]);
                    if (userWarp.getInt("id") > getWarpPointsamount()) {
                        files[i].delete();
                    }
                }
            }
            return true;
        } else {
            sender.sendMessage("You don't have permission");
            return false;
        }
    }

    public boolean setWarppointsLimit(String[] args, CommandSender sender) {
        try{
        getConfig().set(configpath, Integer.parseInt(args[0]));
        saveConfig();
        sender.sendMessage("The new limit is " + getWarpPointsamount() + ".");
        return true;
        }
        catch (Exception e){sender.sendMessage("You must specify a new amount of WarpPoints"); return false;}
    }

    public boolean getWarpPointsLimit(CommandSender commandSender) {
        commandSender.sendMessage("Players are able to use " + getWarpPointsamount() + " WarpPoints. You can adjust that with /setwarppointslimit <amount>");
        return true;
    }

    public boolean warpHelp(CommandSender sender) {
        sender.sendMessage("Welcome to Jonnyfants WarpPoints Plugin!\n\nYou are allowed to have " + getWarpPointsamount() +
                " different WarpPoints. Contact your admin if you are unhappy that amount.\nYou can use the following commands:\n" +
                "/setwarp <id> [name] : This lets you set or reset one of your WarpPoints. An example would be /setwarp 1 Home." +
                "This sets the warppoint to your current location. Giving your WarpPoint a name is optional.\n" +
                "/warp <id | name> : This lets you teleport to one of your WarpPoints. You can specify which one by either writing its id or given name.(Lower and upper case matters)\n" +
                "Example from the previous example /warp Home or /warp 1 would teleport you to the previous set WarpPoint. Warping works between the different dimensions." +
                "/warplist : gives you a list with your current WarpPoints.");
        if (sender.hasPermission("wappoints.admin")) {
            sender.sendMessage("You are an admin for WarpPoints.\nAdditionally you can use:\n/getwarppointslimit : gives you the current maximum WarpPoints for ALL players.\n" +
                    "/setwaprpointslimit <amount> : lets you adjust the limits for WarpPoints for all Players. Be warned, that players might be unhappy if you take away WarpPoints\n" +
                    "/warplist <player> gives you the warplist of another player.\n/warplist all gives you all WarpPoints of all Players.");
        }
        return true;
    }

    public boolean warp(CommandSender sender, String[] args) {
        if (sender instanceof Player == false) {
            sender.sendMessage("This command is for Players only, dummy.");
            return false;
        } else {
            if (sender.hasPermission("warppoints.use")) {
                int id = -1;
                try {
                    id = Integer.parseInt(args[0]);
                } catch (Exception e) {
                }
                if (id > 0 && id <= getWarpPointsamount()) {
                    File warpfile = new File(this.getDataFolder(), sender.getName() + "UUID" + ((Player) sender).getUniqueId() + "ID" + id + ".yml");
                    if (warpfile.exists()) {
                        YamlConfiguration userWarp = YamlConfiguration.loadConfiguration(warpfile);
                        return teleportToWarp(userWarp, sender, id);
                    }
                }
                for (int i = 1; i <= getWarpPointsamount(); i++) {
                    File warpfile = new File(this.getDataFolder(), sender.getName() + "UUID" + ((Player) sender).getUniqueId() + "ID" + i + ".yml");
                    if (warpfile.exists()) {
                        YamlConfiguration userWarp = YamlConfiguration.loadConfiguration(warpfile);
                        if (args[0].equals(userWarp.getString("warpName"))) {
                            return teleportToWarp(userWarp, sender, i);
                        }
                    }
                }
                sender.sendMessage("Couldn't locate your WarpPoint");
                return false;
            } else {
                sender.sendMessage("You don't have the permission warppoints.use");
                return false;
            }
        }
    }

    public boolean teleportToWarp(YamlConfiguration userWarp, CommandSender sender, int id) {
        Location location = ((Player) sender).getLocation();
        location.setX(userWarp.getDouble("warpX"));
        location.setY(userWarp.getDouble("warpY"));
        location.setZ(userWarp.getDouble("warpZ"));
        location.setYaw((float) userWarp.getDouble("warpYaw"));
        location.setPitch((float) userWarp.getDouble("warpPitch"));
        location.setWorld(Bukkit.getWorld(userWarp.getString("warpWorld")));
        ((Player) sender).teleport(location);
        sender.sendMessage("Sending you to " + id + " " + userWarp.getString("warpName") + ".");
        return true;
    }

    public boolean setWarp(CommandSender sender, String[] args) {
        if (sender instanceof Player == false) {
            sender.sendMessage("This command is for Players only, dummy.");
            return false;
        } else {
            if (sender.hasPermission("warppoints.use")) {
                int id;
                try {
                    id = Integer.parseInt(args[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                    sender.sendMessage("You did not provide a valid id.");
                    return false;
                }
                if (id > getWarpPointsamount() || id < 1) {
                    sender.sendMessage("You id is too high or negative. You id should be between 1 and " + getWarpPointsamount() + ".");
                    return false;
                }
                String warpname = "";
                if (args.length > 0) {
                    try {
                        warpname = args[1];
                    } catch (Exception e) {

                    }
                }
                File warpfile = new File(this.getDataFolder(), sender.getName() + "UUID" + ((Player) sender).getUniqueId() + "ID" + id + ".yml"); //"Name" + warpname +
                //if(!warpfile.exists())
                //{
                try {
                    Player p = (Player) sender;
                    YamlConfiguration userWarp = YamlConfiguration.loadConfiguration(warpfile);
                    userWarp.set("warpX", p.getLocation().getX());
                    userWarp.set("warpY", p.getLocation().getY());
                    userWarp.set("warpZ", p.getLocation().getZ());
                    userWarp.set("warpWorld", p.getLocation().getWorld().getName());
                    userWarp.set("warpPitch", p.getLocation().getPitch());
                    userWarp.set("warpYaw", p.getLocation().getYaw());
                    userWarp.set("warpName", warpname);
                    userWarp.set("warpOwner", p.getName());
                    userWarp.set("id", id);
                    userWarp.save(warpfile);
                    sender.sendMessage("New WarpPoint created at XYZ " + (int) p.getLocation().getX() + " " + (int) p.getLocation().getY() + " " + (int) p.getLocation().getZ() + " with the name " + warpname + " and the id " + id + " in " + p.getLocation().getWorld().getName());
                } catch (Exception e) {
                    e.printStackTrace();
                    sender.sendMessage("Something went wrong");
                    return false;
                }
                //}
                return true;
            } else {
                sender.sendMessage("You don't have the permission warppoints.use");
                return false;
            }
        }
    }

    public void printWarp(CommandSender sender, File warpfile) {
        YamlConfiguration userWarp = YamlConfiguration.loadConfiguration(warpfile);
        sender.sendMessage("Player " + userWarp.getString("warpOwner") + " WarpPoint " + userWarp.getInt("id") + ": XYZ " + (int) userWarp.getDouble("warpX") + (int) userWarp.getDouble("warpY") + (int) userWarp.getDouble("warpZ") + "" +
                " Name: " + userWarp.getString("warpName") + " in " + userWarp.getString("warpWorld") + ".");
    }

    public boolean warplist(String[] args, CommandSender sender) {
        String arg="";
        try{
            arg=args[0];
        }
        catch (Exception e){}
        if (arg.equals("")) {
            if (sender instanceof Player) {
                if (sender.hasPermission("warppoints.use")) {
                    for (int i = 1; i <= getWarpPointsamount(); i++) {
                        try {
                            File warpfile = new File(this.getDataFolder(), sender.getName() + "UUID" + ((Player) sender).getUniqueId() + "ID" + i + ".yml");
                            printWarp(sender, warpfile);
                        } catch (Exception e) {
                        }
                    }
                }
            } else {
                sender.sendMessage("This command is for Players only.");
            }
        } else if (arg.equalsIgnoreCase("all")) {
            if (sender.hasPermission("warppoints.admin")) {
                File[] files = getDataFolder().listFiles();
                for (int i = 0; i < files.length; i++) {
                    if (files[i].getName().equals("config.yml") == false) {
                        printWarp(sender, files[i]);
                    }
                }
            } else {
                sender.sendMessage("You don't have permission.");
                return false;
            }
        } else {
            if (sender.hasPermission("warppoints.admin")) {
                Player p = getServer().getPlayer(arg);
                for (int i = 1; i <= getWarpPointsamount(); i++) {
                    try {
                        File warpfile = new File(this.getDataFolder(), sender.getName() + "UUID" + p.getUniqueId() + "ID" + i + ".yml");
                        printWarp(sender, warpfile);
                    } catch (Exception e) {
                    }
                }
            } else {
                sender.sendMessage("You don't have permission.");
                return false;
            }
        }

        return true;
    }

    public int getWarpPointsamount() {
        reloadConfig();
        return getConfig().getInt(configpath);
    }
}
