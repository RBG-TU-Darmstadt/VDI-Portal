**Virtual Desktop Infrastructure Portal**

Bachelor-Praktikum Sommersemester 2011 TU Darmstadt

## Developer Guide

1. Clone this project
2. Install VirtualBox (see below)
3. Install Eclipse
    * Install Maven for Eclipse: [m2e](http://download.eclipse.org/technology/m2e/milestones/1.0), [m2e-wtp](http://download.jboss.org/jbosstools/updates/m2eclipse-wtp)
    * Install tomcat, Windows -> Preferences -> Servers -> Runtime Environments -> Add 
4. Import sourcecode:
    * File -> Import -> Maven -> Maven Projects, choose the folder from step 1, in Advanced select Naming template: `[groupId].[artifactId]`
5. Install PostgreSQL (see below)
6. Add `vboxjxpcom.jar` to tomcat `shared.loader`
    * in the servers view tab double click the Server -> Open launch configuration -> Tab Classpath -> Users Entries -> Add external jar -> select the vboxjxpcom.jar
7. Configure NodeController (see below)
8. Configure ManagementServer (see below)
9. Configure WebInterface (see below)

IMPORTANT: Due to Java XPCOM-issues the VirtualBox-API can not be accessed from Windows machines. Linux and Mac OS X works just fine.

## PostgreSQL Setup

1. Install PostgreSQL Server
2. Create database
    * Database name: `VDIPortal`
3. Edit `hibernate.cfg.xml` located in `src/management/src/main/resources`, depending on your configuration set properties
    * connection.url
    * connection.username
    * connection.password

## VirtualBox Setup

1. Install the latest VirtualBox, get it from the VirtualBox [(download page)](https://www.virtualbox.org/wiki/Downloads)
2. Install the ExtensionPack, get it from the VirtualBox [(download page)](https://www.virtualbox.org/wiki/Downloads)
3. Create the following symbolic link: `ln -s /usr/lib/virtualbox/libvboxjxpcom.so /usr/lib/libvboxjxpcom.so`

IMPORTANT: The home folder must be writeable for the user executing the NodeController, i.e. tomcat6

## Build and install the project

The entire project can be build with `mvn package`. This creates all .war-files in the target-directories of every subproject.

To install the applications, move the .war-files to the Tomcat webapps directory.

## Project configuration

### NodeController
Edit the `configuration.properties` file located in `src/node/src/main/webapp/WEB-INF` and fill in appropriate values.

`vbox.home` depends on where VirtualBox is installed. On Mac OS X, this is probably `/Applications/VirtualBox.app/Contents/MacOS`, on Linux hosts something like `/usr/lib/virtualbox`

### ManagementServer
Edit the `configuration.properties` file located in `src/management/src/main/webapp/WEB-INF` and fill in appropriate values.

### WebInterface
Edit the `configuration.properties` file located in `src/web/src/main/webapp/WEB-INF` and fill in appropriate values.

## License
Copyright (c) 2011 Lennart Diedrich, Jan Dillmann, Ingo Jakobs, Samuel Vogel

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.