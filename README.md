# BukkitWarpPoints
https://www.curseforge.com/minecraft/bukkit-plugins/warppoints


I like the idea of simple teleportation commands like /sethome and /home. However, I also would like to have a few more than that. Therefore I developed this plugin. You can choose howmany individual WarpPoints all players have (default is 5).
Players can simply set a WarpPoint with an optional name and teleport with /warp <id|name>.

 

Commands:
/setwarp <id> [name]
<id> is 1 to the defined amount of warps that players have. Name is optional. Example /setwarp 1 home
/warp <id|name> warps a player to the id or name specified. From previous example both /warp 1 and /warp home would work.
/warphelp gives you information about the commands. Admin commands are also displayed for admins
/warplist shows a list of all of your WarpPoints
/warplist <Player> (Admin only) Shows a List of a Players WarpPoints
/warplist all (Admin only) Shows all the WarpPoints
/getwarppointslimit (Admin only) Displays the current number of WarpPoints each Player can utilize
/setwarppointslimit (Admin only) Sets a new limit, exists, so you don't have to even go into the config. If lowering the WarpPoints, players will be unable to access or Display them, however the files are still there.
/deleteunusedwarppoints (Admin only) gets rid of all WarpPoints above the threshold

Permissions:
warppoint.use default is true Lets users use the standard commands.
warppoints.admin default op All the admin commands.
Since there are defaults, you do not need a permission manager.
