package xyz.ufactions.crates.objects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import xyz.ufactions.crates.managers.CrateSpinner;
import xyz.ufactions.crates.managers.SoundManager;
import xyz.ufactions.crates.utils.MathUtil;
import xyz.ufactions.libs.F;
import xyz.ufactions.libs.ItemBuilder;

public class Crate {

    // Crate block
    private int blockId;
    private int blockData;
    private ItemStack crateItem;

    // Messaging settings
    private String playerMessage = "";
    private String broadcastMessage = "";

    private String genericName;
    private String name;
    private Key key;
    private List<Prize> prizes;
    private int spinTime;
    private int closeTime;
    private Sound openSound;
    private Sound spinSound;
    private Sound closeSound;
    private CrateType crateType;
    private static List<Crate> crates = new ArrayList<>();

    public Crate(FileConfiguration config) {
        // FIXME
//        Bukkit.broadcastMessage("Delta " + config.getCurrentPath());
        try {
//            Bukkit.broadcastMessage("A " + config.getName());
//            Bukkit.broadcastMessage("B " + config.getName().replaceAll(" ", "_"));
//            Bukkit.broadcastMessage("C " + config.getName().replaceAll(" ", "_").toLowerCase());
//            Bukkit.broadcastMessage("D " + F.capitalizeFirstLetter(config.getName().replaceAll(" ", "_").toLowerCase()));
//            this.genericName = F.capitalizeFirstLetter(config.getName().replaceAll(" ", "_").toLowerCase());
            this.genericName = "test";
            this.name = ChatColor.translateAlternateColorCodes('&', config.getString("Crate.name"));
            this.crateType = CrateType.valueOf(config.getString("Crate.type"));
            this.playerMessage = config.getString("Crate.player message");
            if (playerMessage == null) {
                playerMessage = "";
            }
            this.broadcastMessage = config.getString("Crate.broadcast message");
            if (broadcastMessage == null) {
                broadcastMessage = "";
            }
            this.spinTime = config.getInt("Crate.spin time");
            this.closeTime = config.getInt("Crate.close time");
            this.openSound = SoundManager.getInstance().getSound(config.getString("Crate.open sound"));
            this.spinSound = SoundManager.getInstance().getSound(config.getString("Crate.spin sound"));
            this.closeSound = SoundManager.getInstance().getSound(config.getString("Crate.close sound"));
            this.blockId = config.getInt("Crate.block id");
            this.blockData = config.getInt("Crate.block data");
            this.crateItem = new ItemBuilder(blockId, blockData).name(name)
                    .lore("&7Place this chest down anywhere", "&7to mark the location of", "&7your crate").build();
            this.key = new Key(config);
            this.prizes = new ArrayList<>();
            for (String prizeName : config.getConfigurationSection("Prizes").getKeys(false)) {
                prizes.add(new Prize(config, prizeName));
            }
            crates.add(this);
        } catch (Exception e) {
            System.out.println("There was an error while loading a crate!");
            System.out.println(
                    "The following message is the error message, please provide this while making a support thread.");
            e.printStackTrace();
        }
    }

    public String getPlayerMessage() {
        return playerMessage;
    }

    public String getBroadcastMessage() {
        return broadcastMessage;
    }

    public ItemStack getCrateItem() {
        return crateItem;
    }

    public int getBlockData() {
        return blockData;
    }

    public int getBlockId() {
        return blockId;
    }

    public CrateType getCrateType() {
        return crateType;
    }

    public static List<Crate> getCrates() {
        return crates;
    }

    public static Crate getCrate(String name) {
        for (Crate crate : crates) {
            if (crate.getGenericName().equalsIgnoreCase(name)
                    || crate.getGenericName().replaceAll(" ", "_").equalsIgnoreCase(name)) {
                return crate;
            }
        }
        return null;
    }

    public void spin(Player player) {
        new CrateSpinner(this, player).spin();
    }

    public void preview(Player player) {
        Inventory inv = Bukkit.createInventory(null, MathUtil.round(prizes.size()), name);
        for (int i = 0; i < prizes.size(); i++) {
            inv.setItem(i, prizes.get(i).getItem());
        }
        player.openInventory(inv);
    }

    public Sound getCloseSound() {
        return closeSound;
    }

    public int getCloseTime() {
        return closeTime;
    }

    public Key getKey() {
        return key;
    }

    public String getGenericName() {
        return genericName;
    }

    public String getDisplayName() {
        return name;
    }

    public Sound getOpenSound() {
        return openSound;
    }

    public List<Prize> getPrizes() {
        return prizes;
    }

    public Sound getSpinSound() {
        return spinSound;
    }

    public int getSpinTime() {
        return spinTime;
    }

    public void disable() {
        this.genericName = null;
        this.closeSound = null;
        this.closeTime = 0;
        this.key = null;
        this.name = null;
        this.openSound = null;
        this.blockData = 0;
        this.blockId = 0;
        this.crateItem = null;
        this.prizes = null;
        this.spinSound = null;
        this.spinTime = 0;
        crates.remove(this);
    }
}
