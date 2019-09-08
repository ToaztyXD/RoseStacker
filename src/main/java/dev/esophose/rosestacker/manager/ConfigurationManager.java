package dev.esophose.rosestacker.manager;

import dev.esophose.rosestacker.RoseStacker;
import dev.esophose.rosestacker.config.CommentedFileConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigurationManager extends Manager {

    private static final String[] HEADER = new String[] {
            "  __________                      _________ __                 __                 ",
            "  \\______   \\ ____  ______ ____  /   _____//  |______    ____ |  | __ ___________ ",
            "   |       _//  _ \\/  ___// __ \\ \\_____  \\\\   __\\__  \\ _/ ___\\|  |/ // __ \\_  __ \\",
            "   |    |   (  <_> )___ \\\\  ___/ /        \\|  |  / __ \\\\  \\___|    <\\  ___/|  | \\/",
            "   |____|_  /\\____/____  >\\___  >_______  /|__| (____  /\\___  >__|_ \\\\___  >__|   ",
            "          \\/           \\/     \\/        \\/           \\/     \\/     \\/    \\/       "
    };

    public enum Setting {
        LOCALE("locale", "en_US", "The locale to use in the /locale folder"),
        DISABLED_WORLDS("disabled-worlds", Collections.singletonList("disabled_world_name"), "A list of worlds that the plugin is disabled in"),

        STACKABLE_BLOCKS("stackable-blocks", Arrays.asList("DIAMOND_BLOCK", "GOLD_BLOCK", "IRON_BLOCK", "EMERALD_BLOCK", "LAPIS_BLOCK"), "Which blocks should be stackable?"),

        GLOBAL_ENTITY_SETTINGS("global-entity-settings", null, "Global entity settings", "Changed values in entity_settings.yml will override these values"),
        ENTITY_STACKING_ENABLED("global-entity-settings.stacking-enabled", true, "Should entity stacking be enabled at all?"),
        ENTITY_MIN_STACK_SIZE("global-entity-settings.min-stack-size", 2, "The minimum number of nearby entities required to form a stack", "Do not set this lower than 2"),
        ENTITY_MAX_STACK_SIZE("global-entity-settings.max-stack-size", 128, "The maximum number of entities that can be in a single stack"),
        ENTITY_MERGE_RADIUS("global-entity-settings.merge-radius", 5, "How close to entities need to be to merge with each other?"),
        ENTITY_KILL_ENTIRE_STACK_ON_DEATH("global-entity-settings.kill-entire-stack-on-death", false, "Should the entire stack of entities always be killed when the main entity dies?"),
        ENTITY_KILL_ENTIRE_STACK_CONDITIONS("global-entity-settings.kill-entire-stack-on-death-conditions", Collections.singletonList("FALL"), "Under what conditions should the entire stack be killed when the main entity dies?", "If kill-entire-stack-on-death is true, this setting will not be used", "Valid conditions can be found here:", "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/entity/EntityDamageEvent.DamageCause.html"),
        ENTITY_DROP_ACCURATE_ITEMS("global-entity-settings.drop-accurate-items", true, "Should items be dropped for all entities when an entire stack is killed at once?"),
        ENTITY_DROP_ACCURATE_EXP("global-entity-settings.drop-accurate-exp", true, "Should exp be dropped for all entities when an entire stack is killed at once?"),
        ENTITY_STACK_TO_BOTTOM("global-entity-settings.stack-to-bottom", false, "Should newly stacked entities be put on the bottom of the stack?"),
        ENTITY_REQUIRE_LINE_OF_SIGHT("global-entity-settings.require-line-of-sight", true, "Do entities need to be able to see each other to be able to stack?", "Setting this to true will prevent entities from stacking through walls"),
        ENTITY_TRANSFORM_ENTIRE_STACK("global-entity-settings.transform-entire-stack", true, "Should the entire stack of entities be transformed when the main entity is transformed?", "This applies to pigs getting struck by lightning, zombies drowning, etc"),

        GLOBAL_ITEM_SETTINGS("global-item-settings", null, "Global item settings", "Changed values in item_settings.yml will override these values"),
        ITEM_STACKING_ENABLED("global-item-settings.stacking-enabled", true, "Should item stacking be enabled at all?"),
        ITEM_MAX_STACK_SIZE("global-item-settings.max-stack-size", 1024, "The maximum number of items that can be in a single stack"),
        ITEM_MERGE_RADIUS("global-item-settings.merge-radius", 2.5, "How close do items need to be to merge with each other?"),

        GLOBAL_BLOCK_SETTINGS("global-block-settings", null, "Global block settings", "Changed values in block_settings.yml will override these values"),
        BLOCK_STACKING_ENABLED("global-block-settings.stacking-enabled", true, "Should block stacking be enabled at all?"),
        BLOCK_MAX_STACK_SIZE("global-block-settings.max-stack-size", 512, "The maximum number of blocks that can be in a single stack"),

        GLOBAL_SPAWNER_SETTINGS("global-spawner-settings", null, "Global spawner settings", "Changed values in spawner_settings.yml willw override these values"),
        SPAWNER_STACKING_ENABLED("global-spawner-settings.stacking-enabled", true, "Should spawner stacking be enabled at all?"),
        SPAWNER_MAX_STACK_SIZE("global-spawner-settings.max-stack-size", 64, "The maximum number of spawners that can be in a single stack"),

        MYSQL_SETTINGS("mysql-settings", null, "Settings for if you want to use MySQL for data management"),
        MYSQL_ENABLED("mysql-settings.enabled", false, "Enable MySQL", "If false, SQLite will be used instead"),
        MYSQL_HOSTNAME("mysql-settings.hostname", "", "MySQL Database Hostname"),
        MYSQL_PORT("mysql-settings.port", 3306, "MySQL Database Port"),
        MYSQL_DATABASE_NAME("mysql-settings.database-name", "", "MySQL Database Name"),
        MYSQL_USER_NAME("mysql-settings.user-name", "", "MySQL Database User Name"),
        MYSQL_USER_PASSWORD("mysql-settings.user-password", "", "MySQL Database User Password"),
        MYSQL_USE_SSL("mysql-settings.use-ssl", false, "If the database connection should use SSL", "You should enable this if your database supports SSL");

        private final String key;
        private final Object defaultValue;
        private final String[] comments;
        private Object value = null;

        Setting(String key, Object defaultValue, String... comments) {
            this.key = key;
            this.defaultValue = defaultValue;
            this.comments = comments != null ? comments : new String[0];
        }

        /**
         * Gets the setting as a boolean
         *
         * @return The setting as a boolean
         */
        public boolean getBoolean() {
            this.loadValue();
            return (boolean) this.value;
        }

        /**
         * @return the setting as an int
         */
        public int getInt() {
            this.loadValue();
            return (int) this.getNumber();
        }

        /**
         * @return the setting as a long
         */
        public long getLong() {
            this.loadValue();
            return (long) this.getNumber();
        }

        /**
         * @return the setting a double
         */
        public double getDouble() {
            this.loadValue();
            return this.getNumber();
        }

        /**
         * @return the setting a String
         */
        public String getString() {
            this.loadValue();
            return (String) this.value;
        }

        private double getNumber() {
            if (this.value instanceof Integer) {
                return (int) this.value;
            } else if (this.value instanceof Short) {
                return (short) this.value;
            } else if (this.value instanceof Byte) {
                return (byte) this.value;
            } else if (this.value instanceof Float) {
                return (float) this.value;
            }

            return (double) this.value;
        }

        /**
         * @return the setting as a string list
         */
        @SuppressWarnings("unchecked")
        public List<String> getStringList() {
            this.loadValue();
            return (List<String>) this.value;
        }

        public void setIfNotExists(CommentedFileConfiguration fileConfiguration) {
            this.loadValue();

            if (fileConfiguration.get(this.key) == null) {
                List<String> comments = Stream.of(this.comments).collect(Collectors.toList());
                if (!(this.defaultValue instanceof List) && this.defaultValue != null) {
                    String defaultComment = "Default: ";
                    if (this.defaultValue instanceof String) {
                        defaultComment += "'" + this.defaultValue + "'";
                    } else {
                        defaultComment += this.defaultValue;
                    }
                    comments.add(defaultComment);
                }

                if (this.defaultValue != null) {
                    fileConfiguration.set(this.key, this.defaultValue, comments.toArray(new String[0]));
                } else {
                    fileConfiguration.addComments(comments.toArray(new String[0]));
                }
            }
        }

        /**
         * Resets the cached value
         */
        public void reset() {
            this.value = null;
        }

        /**
         * @return true if this setting is only a section and doesn't contain an actual value
         */
        public boolean isSection() {
            return this.defaultValue == null;
        }

        /**
         * Loads the value from the config and caches it if it isn't set yet
         */
        private void loadValue() {
            if (this.value != null)
                return;

            this.value = RoseStacker.getInstance().getConfigurationManager().getConfig().get(this.key);
        }
    }

    private CommentedFileConfiguration configuration;

    public ConfigurationManager(RoseStacker roseStacker) {
        super(roseStacker);
    }

    @Override
    public void reload() {
        File configFile = new File(this.roseStacker.getDataFolder(), "config.yml");
        boolean setHeader = !configFile.exists();

        this.configuration = CommentedFileConfiguration.loadConfiguration(this.roseStacker, configFile);

        if (setHeader)
            this.configuration.addComments(HEADER);

        for (Setting setting : Setting.values()) {
            setting.reset();
            setting.setIfNotExists(this.configuration);
        }

        this.configuration.save();
    }

    @Override
    public void disable() {
        for (Setting setting : Setting.values())
            setting.reset();
    }

    /**
     * @return the config.yml as a CommentedFileConfiguration
     */
    public CommentedFileConfiguration getConfig() {
        return this.configuration;
    }

}
