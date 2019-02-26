# DanBot1
a little Discord Chat Bot.<br>
For a list of Commands and standard-permissions visit https://www.wwwmaster.at/daniel/data/DanBot1/

Programm your own Plugins:
* Create a new Maven Project
* Make sure that you are using Java 1.8
* add DanBot1 to the Dependencies (replace VERSION with [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.danthe1st/DanBot1/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.danthe1st/DanBot1)):
```xml
<dependencies>
	<dependency>
		<groupId>io.github.danthe1st</groupId>
		<artifactId>DanBot1</artifactId>
		<version>VERSION</version>
		<scope>provided</scope>
	</dependency>
</dependencies>
```
* Commands for the Bot have to be annotated with @io.github.danthe1st.danbot1.commands.BotCommand
* Commands should have a no-args-Constructor
* the field aliases in @BotCommand mean the Command aliases (should be at least one)
* Commands should implement the Interface commands.BotCommand
* Listeners should be annotated with @io.github.danthe1st.danbot1.listeners.BotListener
* Listeners should have a no-args-Constructor
* Listeners should extend net.dv8tion.jda.core.hooks.ListenerAdapter
* You can run the Bot by executing io.github.danthe1st.danbot1.core.Main.main(String[] args);
* you can export the plugin as a normal JAR File or use a maven export(mvn package), but the Commands and Listeners should be accessible and should hava a no-args-Constructor.

used Libraries
* [unirest-java](https://github.com/Kong/unirest-java/)
* [JDA](https://github.com/DV8FromTheWorld/JDA/)
* [lavaplayer](https://github.com/sedmelluq/lavaplayer/)
* [slf4j](https://github.com/qos-ch/slf4j/)
* [reflections](https://github.com/ronmamo/reflections)