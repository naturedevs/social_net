# OrbVPN API
This project provides REST API for orbvpn project. The project is written using Spring Boot
framework.

## Project Packaging Structure

The packaging structure is defined by having top packages for each module.
The Core top level package is used for shared code between those top
level modules. (e.g exception handling, security, etc)
Each top level package has a 'common' package, which includes shared code between the subpackages.
For example repositories and entities are placed in common package under each top level package in
order for the developer to be able to use it in other subpackages. Finally the lowermost package is
defined by the specific domain and includes controller, services, dtos, etc for that specific
domain.

## Project setup
- Make sure you have OpenJDK 15 installed
- Make sure you have a `MySQL` database instance installed and running (App tested with MySQL 8.0.x)
- Edit the port if you want the app to run on a different port than 9000

## Building the project
- You can build the project running `./gradlew build` command
- The output war file will be located in `build/libs` directory

## Running the project from command line
- Make sure you are in `OrbVPN-back-end-Java` directory
- Run `./gradlew bootRun` to run the project
- the API will be accessible on port 9000 by default (if you want to change the port, please read
  the `Project setup` section)

# Development

## Code styling

The code style rules for Eclipse and IntelliJ IDEs are placed in `codestyle` directory. There is
also a linter which will check for code style problems. The configuration for the linter is placed
in `checkstyle` directory.

The linter(checkstyle) check will run before each commit operation to check for the code styles.

On push operation, in addition to the checkstyle task, the tests will be run and prevent a push if
any tests are failing.

These checks are done via git hooks. The bash files for git hooks are placed in `githooks` directory
and are copied to the appropriate location when any gradle task is triggered (e.g `gradlew build`)

## Using Eclipse IDE

These instructions were written for Eclipse 2020-12. The steps may not work with a higher version of
Eclipse.

### Importing the project

- Open Eclipse and choose File -> Import option
- In the presented dialog choose Gradle -> Existing Gradle Project
- In the wizard, choose the cloned project source code, `OrbVPN-back-end-Java` directory and go through
  wizard steps to import the project.

### Enabling the -parameters flag for the compiler

Runtime errors will occur until this option is enabled.
It is disabled by default in Eclipse, so it must be manually enabled.
Required in order for reflection to take full advantage of method parameter names.

To enable:

- Open Eclipse preferences (Window->Preferences)
- Navigate to Java -> Compiler
- Enable Store information about method parameters (usable via reflection)

![Preferences->Java->Compiler->Store information about method parameters (usable via reflection)](https://i.stack.imgur.com/3uFYe.png)

### Installing lombok plugin for eclipse

Please go through official documentation for project lombok to install the plugin for Eclipse IDE.
The documentation is provided at: https://projectlombok.org/setup/eclipse

### Installing Spring tools suite for eclipse

To take the most advantage of the eclipse IDE, install Spring Tools Suite for eclipse. Instuctions
could be found at: https://www.eclipse.org/community/eclipse_newsletter/2018/february/springboot.php

### Setting up debug and run configurations

- Choose Run -> Run Configurations from the top menu and select Spring Boot
- Fill in the form and save
- Choose Run -> Debug Configurations from the top menu and select Spring Boot
- The debug configuration should be created with the same name automatically

### Setting up checkstyle plugin

This project uses checkstyle as the linter. Please install checkstyle plugin from the marketplace.
For more instructions, please visit: https://checkstyle.org/eclipse-cs/#!/install
After installing checkstyle plugin, please go to eclipse preferences -> checkstyle and add new
configuration. Specify the type as 'external configuration' and choose
the `config/checkstyle/checkstyle.xml` as the config file. Save the configs and choose the newly
created checkstyle config in the list as the default configuration.

### Setting up code styling configuration

- This project uses custom code formatting rules, to use the rules provided by the team, in open
  eclipse preferences -> java -> formatter
- Import formatter settings from `codestyle/eclise/eclipse-java-google-style.xml` file

## Using IntelliJ IDE

These instructions were written for IntelliJ IDEA 2020.3. The steps may not work with a higher
version of IntelliJ IDEA.

### Importing the project

- Open IntelliJ and choose File -> new -> From existing sources
- Choose the location of the project and click open
- Choose gradle in the presented dialog and import the project

### Setting up debug and run configurations

The configurations should be automatically created after importing the project. You can use the
automatically created configuration to run and debug the application.

### Setting up checkstyle plugin
This project uses checkstyle as the linter. Please install checkstyle plugin from the marketplace. For more instructions, please visit: https://plugins.jetbrains.com/plugin/1065-checkstyle-idea
After installing checkstyle plugin, please go to IntelliJ preferences -> tools -> checkstyle and add new configuration. Choose the use a local configuration option and choose the `config/checkstyle/checkstyle.xml` as the config file.
Save the configs and activate the newly created checkstyle config in the list.

### Setting up code styling configuration
- This project uses custom code formatting rules, to use the rules provided by the team, in open IntelliJ preferences -> Editor -> Code Style
- From the gear icon next to the code style name select import scheme -> IntelliJ code style xml and choose `codestyle/intellij/intellij-java-google-style.xml` file
