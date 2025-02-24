distributionUrl=https\://services.gradle.org/distributions/gradle-7.0.2-bin.zip
gradle/wrapper/gradle-wrapper.properties

The error:
Could not create task ':app:processDebugResources'.
Cannot use @TaskAction annotation on method IncrementalTask.taskAction$gradle_core() because interface org.gradle.api.tasks.incremental.IncrementalTaskInputs is not a valid parameter to an action method.
was introduced in Gradle 8.0, where IncrementalTaskInputs was removed as part of Gradle's API deprecations.

Gradle Version	Minimum JDK	Maximum JDK
7.0.2	JDK 8	JDK 16

org.gradle.java.home=/opt/homebrew/opt/openjdk@11/libexec/openjdk.jdk/Contents/Home
/.idea/gradle.xml
-        <option name="gradleJvm" value="Android Studio default JDK" />
+        <option name="gradleJvm" value="#GRADLE_LOCAL_JAVA_HOME" />
The 'android-gif-keyboard' project JDK configuration is now set through the #GRADLE_LOCAL_JAVA_HOME macro. This simplifies the JDK configuration experience and makes it more reliable. You can change this in the Gradle JDK settings.


Deprecated Gradle features were used in this build, making it incompatible with Gradle 8.0.
Use '--warning-mode all' to show the individual deprecation warnings.
See https://docs.gradle.org/7.0.2/userguide/command_line_interface.html#sec:command_line_warnings
