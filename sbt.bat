set SCRIPT_DIR=%~dp0
java -Xmx1024M -XX:ReservedCodeCacheSize=64m -XX:MaxPermSize=256M -XX:+CMSClassUnloadingEnabled -jar "%SCRIPT_DIR%\tools\sbt-launch.jar" %*