# papers

<h2>project setup</h2>

1. install Java 8
1. import maven project
1. create mysql database `papers` and create user `root` with password `root`
1. in project directory run `mvn clean install flyway:migrate`
1. in the Jetbrains IDEA setup plugin `lombok` in `File -> Settings -> Plugins`
1. enable `annotation processing` in the `Compiler` options
