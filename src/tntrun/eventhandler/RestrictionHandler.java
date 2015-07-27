/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package tntrun.eventhandler;

import java.util.Arrays;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

import tntrun.TNTRun;
import tntrun.arena.Arena;
import tntrun.messages.Messages;
import tntrun.utils.Shop;

public class RestrictionHandler implements Listener {

	private TNTRun plugin;

	public RestrictionHandler(TNTRun plugin) {
		this.plugin = plugin;
	}

	private HashSet<String> allowedcommands = new HashSet<String>(
		Arrays.asList("/tntrun leave", "/tntrun vote", "/tr leave", "/tr vote", "/tr help", "/tr info")
	);

	// player should not be able to issue any commands besides /tr leave and /tr vote while in arena
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
		Player player = e.getPlayer();
		Arena arena = plugin.amanager.getPlayerArena(player.getName());
		// ignore if player is not in arena
		if (arena == null) {
			return;
		}
		// allow use any command if player has permission
		if (player.hasPermission("tntrun.cmdblockbypass")) {
			return;
		}
		// now check command
		if (!allowedcommands.contains(e.getMessage().toLowerCase())) {
			Messages.sendMessage(player, Messages.nopermission);
			e.setCancelled(true);
		}
	}

	// player should not be able to break blocks while in arena
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerBlockBreak(BlockBreakEvent e) {
		Player player = e.getPlayer();
		Arena arena = plugin.amanager.getPlayerArena(player.getName());
		// ignore if player is not in arena
		if (arena == null) {
			return;
		}
		e.setCancelled(true);
	}

	// player should not be able to place blocks while in arena
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerBlockPlace(BlockPlaceEvent e) {
		Player player = e.getPlayer();
		Arena arena = plugin.amanager.getPlayerArena(player.getName());
		// ignore if player is not in arena
		if (arena == null) {
			return;
		}
		e.setCancelled(true);
	}

	//player is not able to drop items while in arena
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerItemDrop(PlayerDropItemEvent e) {
		Player player = e.getPlayer();
		Arena arena = plugin.amanager.getPlayerArena(player.getName());
		// ignore if player is not in arena
		if (arena == null) {
			return;
		}
		e.setCancelled(true);
	}
	
	//check interact
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		Arena arena = plugin.amanager.getPlayerArena(player.getName());
		// check item
		if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
	        if(e.getMaterial() == Material.BED){
				if (arena != null) {
					e.setCancelled(true);
					arena.getPlayerHandler().leavePlayer(player, Messages.playerlefttoplayer, Messages.playerlefttoothers);
				}
	        }
		}
		if(e.getMaterial() == Material.NETHER_STAR){
			if (arena != null) {
				Inventory inv = Bukkit.createInventory(null, Shop.invsize, Shop.invname);
				Shop.setItems(inv);
				player.openInventory(inv);
			}
		}
		
        if(e.getMaterial() == Material.EMERALD){
        	if (arena != null) {
      	   	     for(String list : plugin.getConfig().getStringList("info.list")){
       		    	 player.sendMessage(list.replace("&", "§"));
       		     }
       	   	     player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
        	}
        }
        
        if(e.getMaterial() == Material.DIAMOND){
        	if (arena != null) {
        		if(arena.getStatusManager().isArenaStarting()){
        			player.sendMessage(Messages.arenastarting.replace("&", "§"));
        			return;
        		}
      	   	     if(arena.getPlayerHandler().vote(player)){
      	   	     player.sendMessage(Messages.playervotedforstart.replace("&", "§"));
       	   	     }else{
       	   	   player.sendMessage(Messages.playeralreadyvotedforstart.replace("&", "§"));
       	   	     }
       	   	     player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
        	}
        }
	}
}
