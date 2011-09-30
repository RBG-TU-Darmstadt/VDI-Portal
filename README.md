## Developer Guide
1. check out this project
2. install VirtualBox, see below
3. install Eclipse

    install Maven for Eclipse: [m2e](http://download.eclipse.org/technology/m2e/milestones/1.0), [m2e-wtp](http://download.jboss.org/jbosstools/updates/m2eclipse-wtp)

    install tomcat, Windows -> Preferences -> Servers -> Runtime Environments -> Add 
4. import SourceCode:

    File -> Import -> Maven -> Maven Projects, choose the folder from step 1, in Advanced select Naming template: `[groupId].[artifactId]`
5. install PostgreSQL, instructions see below
6. add vboxjxpcom.jar to tomcat shared.loader

    in the servers view tab double click the Server -> Open launch configuration -> Tab Classpath -> Users Entries -> Add external jar -> select the vboxjxpcom.jar
7. configure NodeController, see below
8. configure ManagementServer, see below
9. configure WebInterface, see below

## PostgreSQL Setup

1. install PostgreSQL Server
2. create Database
    * Database name: `VDIPortal`
3. edit `hibernate.cfg.xml` located in `src/management/src/main/resources`, depending on your configuration set properties
    * connection.url
    * connection.username
    * connection.password


## VirtualBox Setup

1. install the latest VirtualBox, get it from the VirtualBox [download page](https://www.virtualbox.org/wiki/Downloads)
2. install the ExtensionPack, get it from the VirtualBox [download page](https://www.virtualbox.org/wiki/Downloads)
3. create the following symbolic link: `ln -s /usr/lib/virtualbox/libvboxjxpcom.so /usr/lib/libvboxjxpcom.so

IMPORTANT: The home folder must be writeable for the user executing the NodeController, i.e. tomcat6

