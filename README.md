# VanillaHungerGames

A Hunger Games minigame plugin that can be added to 
your server without interferring with the rest of the server.

## Description
This plugin will allow a player to create a new hunger games world and players may
join and leave as they want (if they join mid-tournament they will be in spectator mode).
After the tournament, or when a player leaves, they will be brought back to right where
they were. Because the plugin keeps track of the original player location/inventory, I put 
this plugin in my SMP and it's a seemless experience if we want to play a quick game of hunger games.

The hunger games tournament is, as you would imagine, a battle royal. I call it "Vanilla"
hunger games because players will need to find and/or craft weapons and armor themselves if
they want to survive the games.

## Classes
To make things a little spicy, I've added a handful of classes. Check out this [YouTube video I made for the classes](https://www.youtube.com/watch?v=h8884krttm0&t=8s).

## Commands
#### /hgcreate (Requires OP permission by default)
Creates a new hunger games world and lets user know when it's ready to join. Update the `require_admin_to_create` field to allow any users to be able to create a Hunger Games.
#### /hgjoin
Join the hunger games world. If a player joins _after_ a tournament has already started, they can spectate.
#### /hgleave
Leave the hunger games world and be brought back to wherever the player was previously.
#### /hgteleport
Teleport to another hunger games participant (only usable while spectating).
#### /hgclasses
List all available classes.
#### /hgclassinfo <CLASS_NAME>
Get info about a certain class.
#### /hgclass <CLASS_NAME>
Set your class for the tournament. Must be done before the tournament starts.
#### /hgrefresh (Requires OP permission)
Refreshes yml configuration files

## Configurations:

See available configurations [here](configurations.md).

## Contact Me
Discord: https://discord.gg/UTjtJPTjGG
