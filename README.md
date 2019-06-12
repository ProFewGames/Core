# Core
This is only for use with MegaBukkit servers! (MegaBukkit is a fork of Spigot(1.8.8) added and pre-loaded with new
developer features making it easy for anyone developing on its software to hook onto simple methods and and variables
including: Jedis, MySQL, and extended Minecraft Server/Bukkit methods) The 'Core' module is used to load its sub-modules
into servers (e.g: Weather, Tags, ChatColor). It has to be compiled within an internal compiler which will output the build
to your maven folder. Once that is complete you can attach it to a main plugin and access its internal components. Before
exporting your main project be sure that all modules are going to be loaded by loading them from the ModuleManager on enable.
