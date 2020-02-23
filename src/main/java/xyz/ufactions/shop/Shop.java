package xyz.ufactions.shop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.ufactions.libs.ItemBuilder;
import xyz.ufactions.libs.UtilMath;
import xyz.ufactions.updater.UpdateType;
import xyz.ufactions.updater.event.UpdateEvent;

public class Shop implements Listener {

    public enum ShopFiller {
        NONE, RAINBOW, PANE;
    }

    private Inventory inventory;
    private List<IButton> items = new ArrayList<>();
    private ShopFiller filler = ShopFiller.NONE;
    private Random random;
    private JavaPlugin plugin;
    private Shop returnShop;
    private String name;
    protected boolean updatableItems = false;

    public Shop(JavaPlugin plugin, String name, ShopFiller filler) {
        this.plugin = plugin;
        this.filler = filler;
        name = ChatColor.translateAlternateColorCodes('&', name);
        this.name = name;
    }

    public Shop(JavaPlugin plugin, Shop returnShop, String name, ShopFiller filler) {
        this.plugin = plugin;
        this.filler = filler;
        this.returnShop = returnShop;
        name = ChatColor.translateAlternateColorCodes('&', name);
        this.name = name;
    }

    public Shop(JavaPlugin plugin, String name, int length, ShopFiller filler, IButton... items) {
        this.filler = filler;
        this.random = new Random();
        this.plugin = plugin;
        this.items.addAll(Arrays.asList(items));
        name = ChatColor.translateAlternateColorCodes('&', name);
        this.name = name;
        this.inventory = Bukkit.createInventory(null, length, name);
        sortInventory();
        System.out.println(" ");
        System.out.println(" ");
        System.out.println("Filler is " + filler.toString());
        System.out.println(" ");
        System.out.println(" ");
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) != null && inventory.getItem(i).getType() != Material.AIR) {
                System.out.println("Is not empty " + inventory.getItem(i));
                continue;
            }
            if (filler == ShopFiller.NONE) {
                System.out.println("Filler is none");
                continue;
            }
            if (filler == ShopFiller.PANE) {
                System.out.println("Setting at " + i);
                inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, 11).name(" ").build());
            }
        }
    }

    public Shop(JavaPlugin plugin, Shop returnShop, String name, int length, ShopFiller filler, IButton... items) {
        this.filler = filler;
        this.returnShop = returnShop;
        this.random = new Random();
        this.plugin = plugin;
        this.items.addAll(Arrays.asList(items));
        name = ChatColor.translateAlternateColorCodes('&', name);
        this.name = name;
        this.inventory = Bukkit.createInventory(null, length, name);
        sortInventory();
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) != null)
                continue;
            if (filler == ShopFiller.NONE)
                continue;
            if (filler == ShopFiller.PANE)
                inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, 11).name(" ").build());
        }
    }

    public Shop(JavaPlugin plugin, Shop returnShop, String name, int length, ShopFiller filler, List<IButton> items) {
        this.filler = filler;
        this.returnShop = returnShop;
        this.random = new Random();
        this.plugin = plugin;
        name = ChatColor.translateAlternateColorCodes('&', name);
        this.name = name;
        this.items = items;
        this.inventory = Bukkit.createInventory(null, length, name);
        sortInventory();
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) != null)
                continue;
            if (filler == ShopFiller.NONE)
                continue;
            if (filler == ShopFiller.PANE)
                inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, 11).name(" ").build());
        }
    }

    public Shop(JavaPlugin plugin, String name, int length, ShopFiller filler, List<IButton> items) {
        this.filler = filler;
        this.random = new Random();
        name = ChatColor.translateAlternateColorCodes('&', name);
        this.name = name;
        this.plugin = plugin;
        this.items = items;
        this.inventory = Bukkit.createInventory(null, length, name);
        sortInventory();
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) != null)
                continue;
            if (filler == ShopFiller.NONE)
                continue;
            if (filler == ShopFiller.PANE)
                inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, 11).name(" ").build());
        }
    }

    @SuppressWarnings("rawtypes")
    private void sortInventory() {
        if (inventory == null) {
            inventory = Bukkit.createInventory(null, UtilMath.round(items.size()), name);
        }
        inventory.clear();
        List<OrderingButton> ordering = new ArrayList<>();
        for (IButton item : items) {
            if (item instanceof OrderingButton) {
                ordering.add((OrderingButton) item);
                continue;
            }
            if (item instanceof ShopItem) {
                ShopItem i = (ShopItem) item;
                inventory.setItem(i.getPosition(), i.getItem());
            } else if (item instanceof PanelButton) {
                inventory.setItem(((PanelButton) item).getPosition(), item.getItem());
            }
        }
        for (OrderingButton button : ordering) {
            if (inventory.firstEmpty() == -1) {
                break;
            }
            inventory.setItem(inventory.firstEmpty(), button.getItem());
        }
    }

    public List<IButton> getButtons() {
        return items;
    }

    public void setButtons(List<IButton> buttons) {
        items.clear();
        addButtons(buttons);
    }

    public void addButtons(List<IButton> buttons) {
        items.addAll(buttons);
        sortInventory();
    }

    public void addButton(IButton button) {
        items.add(button);
        sortInventory();
    }

    public void removeButton(IButton button) {
        items.remove(button);
        sortInventory();
    }

    public boolean containsButton(IButton button) {
        return items.contains(button);
    }

    public boolean canOpenInventory(Player player) {
        return true;
    }

    public void openInventory(Player player) {
        if (canOpenInventory(player)) {
            if (player.getOpenInventory() != null)
                player.closeInventory();
            if (inventory == null)
                inventory = Bukkit.createInventory(null, UtilMath.round(items.size()), name);
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
            player.openInventory(inventory);
            onInventoryOpen(player);
        }
    }

    public void onInventoryOpen(Player player) {
    }

    public Inventory getInventory() {
        if (inventory == null) {
            inventory = Bukkit.createInventory(null, UtilMath.round(items.size()), name);
        }
        return inventory;
    }

    @EventHandler
    public void updateItems(UpdateEvent e) {
        if (e.getType() != UpdateType.SEC)
            return;
        if (!updatableItems)
            return;
        for (IButton button : items) {
            button.getItem();
        }
    }

    @EventHandler
    public void updatePanels(UpdateEvent e) {
        if (e.getType() != UpdateType.FAST)
            return;
        if (filler == ShopFiller.RAINBOW) {
            for (int i = 0; i < inventory.getSize(); i++) {
                ItemStack item = inventory.getItem(i);
                if (item == null || item.getType() == Material.STAINED_GLASS_PANE) {
                    inventory.setItem(i,
                            new ItemBuilder(Material.STAINED_GLASS_PANE, random.nextInt(15)).name(" ").build());
                    continue;
                }
            }
        }
    }

    @SuppressWarnings("rawtypes")
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player))
            return;
        if (e.getCurrentItem() == null)
            return;
        if (e.getClickedInventory().equals(inventory)) {
            ItemStack item = e.getCurrentItem();
            e.setCancelled(true);
            List<IButton> tempList = new ArrayList<>();
            tempList.addAll(items);
            for (IButton i : tempList) {
                if (item.equals(i.getItem())) {
                    if (i instanceof InverseButton) {
                        if (((InverseButton) i).canInverse((Player) e.getWhoClicked())) {
                            items.remove(i);
                            items.add(((InverseButton) i).getReverse());
                            inventory.clear();
                            sortInventory();
                        }
                    }
                    i.onClick((Player) e.getWhoClicked(), e.getClick());
                    onClick((Player) e.getWhoClicked(), i);
                }
            }
        }
    }

    public void setReturnShop(Shop returnShop) {
        this.returnShop = returnShop;
    }

    public void onClick(Player player, IButton button) {
    }

    public void onClose(Player player) {
    }

    public boolean canClose(Player player) {
        return true;
    }

    public boolean openReturnShop = true;

    @EventHandler
    public void onClose(final InventoryCloseEvent e) {
        if (e.getInventory().equals(inventory)) {
            if (!canClose((Player) e.getPlayer())) {
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

                    @Override
                    public void run() {
                        e.getPlayer().openInventory(getInventory());
                    }
                }, 1);
                return;
            }
            HandlerList.unregisterAll(this);
            onClose((Player) e.getPlayer());
        }
    }
}