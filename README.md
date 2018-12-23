# DanBot1
a little Discord Chat Bot.<br>
For a list of Commands and standard-permissions visit https://www.wwwmaster.at/daniel/data/DanBot1/

Programm your own Plugins:
* Create a new Maven Project
* Make sure that you are using Java 1.8
* Copy all dependencies from DanBot1 (pom.xml)
* import DanBot1_lib.jar to your Project
* Commands for the Bot have to be annotated with @commands.BotCommand
* the field aliases in @BotCommand mean the Command aliases (should be at least one)
* Commands should implement the Interface commands.BotCommand
* Listeners should be annotated with @listeners.BotListener
* Listeners should extend net.dv8tion.jda.core.hooks.ListenerAdapter
* You can run the Bot by executing core.Main.main(String[] args);