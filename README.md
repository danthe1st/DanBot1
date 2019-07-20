# DanBot1 [![Build Status](https://travis-ci.com/danthe1st/DanBot1.svg?branch=master)](https://travis-ci.com/danthe1st/DanBot1)
a little, pluginable Discord Chat Bot.<br>
For a list of Commands and standard-permissions visit [https://danthe1st.github.io/DanBot1](https://danthe1st.github.io/DanBot1)

Program your own Plugins:

An example for creating Plugins for DanBot1 can be found [here](https://github.com/danthe1st/DanBot1ExamplePlugin)
* Create a new Maven Project
* Make sure that you are using at least Java 8
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
* if you use *IntelliJ*, make sure to tick `Include dependencies with "Provided" scope` in your run configuration. ![include provided dependencies](https://github.com/danthe1st/DanBot1/raw/master/.github/resc/IntelliJtickIncludeProvidedDependencies.png "TODO")
* Commands for the Bot have to be annotated with `@io.github.danthe1st.danbot1.commands.BotCommand`
* Commands should have a no-args-Constructor
* the field *aliases* in `@BotCommand` mean the Command aliases (should be at least one)
* Commands should implement the Interface `io.github.danthe1st.danbot1.commands.Command`
* Listeners should be annotated with `@io.github.danthe1st.danbot1.listeners.BotListener`
* Listeners should have a no-args-Constructor
* Listeners should extend `net.dv8tion.jda.api.hooks.ListenerAdapter`
* You can run the Bot by executing io.github.danthe1st.danbot1.core.Main.main(String[] args);
* you can export the plugin as a normal JAR File or use a maven export(mvn package), but the Commands and Listeners should be accessible and should hava a no-args-Constructor.

used Libraries:
* [JDA](https://github.com/DV8FromTheWorld/JDA/) (in order to interact with Discord)
* [lavaplayer](https://github.com/sedmelluq/lavaplayer/) (for the music command)
* [reflections](https://github.com/ronmamo/reflections) (for the Plugin mechanism)
* [Apache Common Collections 4](https://github.com/apache/commons-collections) (because JDA uses it(Bags))
* [slf4j](https://github.com/qos-ch/slf4j/) (because JDA logs using slf4j)


Dev-Libraries(used for testing/used in development):
* [JUnit](https://github.com/junit-team/junit5) (for Tests)
* [Spotbugs](https://github.com/spotbugs/spotbugs) (in order to test for possible bugs)
