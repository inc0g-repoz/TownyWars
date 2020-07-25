# TownyWars by karlov_m

<p><img align=left src="https://i.imgur.com/9MjzbiA.png">

___

A simple plugin that provides arcade wars with your Towny plugin. The author of the plugin is karlov_m. TownyWars is a fairly new plugin, but it is already able to provide simple, and most importantly interesting wars between towns. Other towns can join the war as allies of one of the parties.
___

## 
## How it works
There are neutral towns and non-neutral ones. Only non-neutral cities can declare war. After the Declaration of war, both cities have points that are calculated from the number of citizens. When a citizen from one city is killed by a citizen from another, one point is taken away from the town of the victim and transferred to the attacking city. During the war, both towns cannot turn off the PvP. The winner gets the entire territory of the losing town.
## Compability with Towny
| TownyWars version | Towny(tested) | Minecraft Version |
| ------ | ------ | ----- |
| 1.2.0 | 0.96.2.0+  | 1.15.*, 1.16.*, 1.12.*, 1.14.*
| 1.1.*.-1.15 | 0.96.1.0, 0.96.2.0  | 1.15.*, 1.16.*
| 1.1*.-1.12 | 0.93.1.0 | 1.12.*
## Commands

___
#### /twar declare <town> - declare a war
#### /twar n - toggle neutrality of your town (costs 200.0 by default)
#### /twar fend <town> - admin command, stop the war "without pain"
#### /twar st - list of wars with points
#### /twar reload - reload the plugin
#### /twar help - help command
#### /twar info - info command
#### /twar joinwar <town> - send request to join war
#### /twar end - send request to end war
#### /twar canceljw - cancel request
#### /twar invite <town> - accept join-request
___

## Permissions

___

#### twar.use - simple use /twar info, /twar help and /twar st
#### twar.mayor - declare a war
#### twar.admin - access admin commands
___

## About code
The code is not structured and looks bad, but it works and works well, and this is the most important thing. In future updates, we will improve the code.

## For developers
Read API
https://github.com/karlovm/TownyWars/wiki/Developers-API

## Join our Discord
https://discord.gg/Etd4XXH
   
