# DanBot1
a little Discord Chat Bot.<br>
For a list of Commands and standard-permissions visit https://www.wwwmaster.at/daniel/data/DanBot1/

Programm your own Plugins:
* Create a new Java Project
* Make sure that you are using Java 1.8
* import DanBot1.jar to your Project (it is located in DanBot1/)
* Commands for the Bot have to be annotated with @commands.BotCommand
* the field aliases in @BotCommand mean the Command aliases (should be at least one)
* Commands should implement the Interface commands.BotCommand
* Listeners should be annotated with @listeners.BotListener
* Listeners should extend net.dv8tion.jda.core.hooks.ListenerAdapter
* You can run the Bot by executing core.Main.main(String[] args);
* you can export the plugin as a normal JAR File, but the Commands and Listeners should be accessible and should hava a no-args-Constructor.

Licences of used Libraries
* [unirest-java](https://github.com/Kong/unirest-java/blob/master/LICENS)
* [JDA](https://github.com/DV8FromTheWorld/JDA/blob/master/LICENS)
* [lavaplayer](https://raw.githubusercontent.com/sedmelluq/lavaplayer/master/LICENS)
* [slf4j](https://github.com/qos-ch/slf4j/blob/master/LICENSE.txt)
* [reflections](https://github.com/ronmamo/reflections/blob/master/COPYING.txt)