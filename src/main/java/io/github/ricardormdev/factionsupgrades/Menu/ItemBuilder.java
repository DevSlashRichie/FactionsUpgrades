package io.github.ricardormdev.factionsupgrades.Menu;

import com.google.common.collect.Lists;
import io.github.ricardormdev.factionsupgrades.FactionsUpgrades;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * API made to create ItemStack on spigot.
 *
 * @author Ricardo Rodríguez Medina (RicardormDev)
 * @version 1.0
 *
 */
public class ItemBuilder {

    // CHANGE THIS FOR YOU PLUGIN
    private static Plugin plugin = FactionsUpgrades.getInstance();
    private Logger log = plugin.getLogger();

    private Material material;
    private int quantity;
    private Byte data;

    private String displayName;
    private List<String> lore;
    private List<Enchant> enchants;

    /**
     *
     * Create a new object ItemBuilder
     *
     */
    public ItemBuilder() {
        this.quantity = 1;
        this.data = 0;
    }

    public static ItemStack of(Material material) {
        return new ItemBuilder().setMaterial(material).setQuantity(1).build();
    }

    /**
     *
     * Set the material of the items stack (cannot be null)
     *
     * @param material The type that you need (It's enum)
     * @return The builder.
     * @see Material
     *
     */
    public ItemBuilder setMaterial(Material material) {
        this.material = material;
        return this;
    }

    /**
     *
     * How many you need?
     *
     * @param q The amount of items on the ItemStack
     * @return The builder.
     */
    public ItemBuilder setQuantity(int q) {
        this.quantity = q;
        return this;
    }

    /**
     *
     * If you need data (example: the orange color of the wool, 35:1
     *
     * @param d The amount.
     * @return The builder.
     */
    public ItemBuilder setData(int d) {
        this.data = (byte) d;
        return this;
    }

    /**
     *
     * The name that the player will see when he takes the item.
     *
     * @param name The name / text you need
     * @return The builder.
     */
    public ItemBuilder setDisplayName(String name) {
        this.displayName = name;
        return this;
    }

    /**
     *
     * Set the lore, the text below the name.
     * <p>This is displayed when you hover the item.</p>
     *
     * @param index The line of the text you need to be displayed.
     * @param text The text.
     * @return The builder.
     */
    public ItemBuilder setLore(int index, String text) {
        if(this.lore == null) lore = new ArrayList<>();

        this.lore.set(index, ChatColor.translateAlternateColorCodes('&', text));
        return this;
    }

    /**
     *
     * Set the lore with a List
     *
     * @param lore The actual list.
     * @return The builder.
     */
    public ItemBuilder setLore(List<String> lore) {
        List<String> converted = Lists.newArrayList();

        for (String s : lore) {
            converted.add(ChatColor.translateAlternateColorCodes('&', s));
        }

        this.lore = converted;
        return this;
    }

    /**
     *
     * Add a new line to the lore.
     *
     * @param text The line to be added.
     * @return The builder.
     */
    public ItemBuilder addLoreLine(String text) {
        if(this.lore == null) lore = new ArrayList<>();
        this.lore.add(ChatColor.translateAlternateColorCodes('&', text));
        return this;
    }

    /**
     *
     * If you need to add an enchantment, do it here.
     *
     * @param e The type of Enchant.
     * @param level Level of the enchant.
     * @return The builder.
     */
    public ItemBuilder addEnchant(Enchantment e, int level) {
        if(this.enchants == null) enchants = new ArrayList<>();

        this.enchants.add(new Enchant(e, level));
        return this;
    }

    /**
     * Applies an argument to the message with the given name. All occurrences
     * of the '<name>' will be replaced with the string value of the argument.
     */
    public ItemBuilder arg(String name, Object arg) {
        displayName = displayName.replace("{" + name + "}", String.valueOf(arg));
        lore = lore.stream().map(s -> s.replace("{" + name + "}", String.valueOf(arg))).collect(Collectors.toList());
        return this;
    }

    /**
     * Applies multiple arguments to the message in a varargs form. Elements in
     * the array with even indexes are considered to be the argument name, while
     * those with odd numbers are the argument itself.
     */
    public ItemBuilder args(Object... args) {
        for (int i = 1; i < args.length; i += 2) {
            arg(String.valueOf(args[i-1]), args[i]);
        }
        return this;
    }

    /**
     *
     * Finally build the item into an itemstack.
     *
     * @return ItemStack ready to use.
     * @see ItemStack
     *
     */
    @SuppressWarnings("deprecation")
    public ItemStack build() {
        ItemStack item = new ItemStack(material, quantity, (short) 1);
        ItemMeta meta = item.getItemMeta();
        if(displayName != null)
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));

        if(lore != null && !lore.isEmpty())
            meta.setLore(lore);

        if(enchants != null && !enchants.isEmpty())
            for(Enchant e : enchants) {
                meta.addEnchant(e.getE(), e.getLevel(), true);
            }

        item.setItemMeta(meta);
        return item;
    }


    public ItemBuilder buildFromConfiguration(ConfigurationSection section) {
        String displayName = ChatColor.RED + "Could not load this from config.";
        Material material = Material.BEDROCK;

        if(section.contains("displayName")) {
            displayName = section.getString("displayName");
        } else log.warning("Could not load the name for the item in configuration section: \"" + section.getName() + "\"");
        setDisplayName(displayName);

        if(section.contains("item")) {

            try {
                material = Material.getMaterial(section.getString("item"));
            } catch (Exception e) {
                log.warning("Item " + section.getString("item") + " not found in: \"" + section.getName() + "\"");
            }

        } else log.warning("Could not load the item for the item in configuration section: \"" + section.getName() + "\"");
        setMaterial(material);


        if(section.contains("data-value")) {
            String ac = section.getString("data-value");

            if(!ac.matches("[0-9]+")) {
                log.warning("Data value only accepts numbers. Error at configuration section : \"" + section.getName() + "\"");
            }

            setData(Byte.parseByte(ac));
        }

        if(section.contains("lore")) {
            setLore(section.getStringList("lore"));
        }

        if(section.contains("amount")) {
            String ac = section.getString("amount");

            if(!ac.matches("[0-9]+")) {
                log.warning("Amount only accepts numbers. Error at configuration section : \"" + section.getName() + "\"");
            }

            setQuantity(Integer.parseInt(ac));
        }

        if(section.contains("enchantments")) {
            List<String> act = section.getStringList("enchantments");

            for (String s : act) {
                s = s.trim();
                String[] enc_data = s.split(":");

                if(!enc_data[1].matches("[0-9]+")) {
                    log.warning("Enchantment level only accepts numbers. Error at configuration section : \"" + section.getName() + "\"");
                }

                Enchantment enchantment = Enchantment.getByName(enc_data[0].toUpperCase());
                int level = Integer.parseInt(enc_data[1]);

                if(enchantment != null) {
                    addEnchant(enchantment, level);
                } else {
                    log.warning("Skipping enchantment " + enc_data[0] + " because doesn't exists. in " + section.getName());
                }


            }

        }

        return this;
    }

    /**
     *
     * The class to manage the enchant.
     */
    private class Enchant {

        private Enchantment e;
        private int level;

        /**
         *
         * The builder.
         *
         * @param e Type of Enchant.
         * @param l Level of the Enchant.
         */
        private Enchant(Enchantment e, int l) {
            this.e = e;
            this.level = l;
        }

        /**
         *
         * @return Level of the enchant.
         */
        private int getLevel() {
            return level;
        }

        /**
         *
         * @return Type of enchantment.
         */
        private Enchantment getE() {
            return e;
        }
    }
}