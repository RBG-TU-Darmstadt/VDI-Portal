## Developer Guide
1. check out this project
2. install Eclipse
    install Maven for Eclipse: m2e, m2e-wtp
    install tomcat
3. import SourceCode:
    File -> Import -> Maven -> Maven Projects, choose the folder from step 1, in Advanced select Naming template: `[groupId].[artifactId]`
4. install PostgreSQL, instructions see below
5. add vboxjxpcom.jar to tomcat shared.loader
    in the servers view tab double click the Server -> Open launch configuration -> Tab Classpath -> Users Entries -> Add external jar -> select the vboxjxpcom.jar
6. configure NodeController, see below
7. configure ManagementServer, see below
8. configure WebInterface, see below

## PostgreSQL Setup

1. install PostgreSQL Server
2. create Database
    * Database name: `VDIPortal`
3. edit `hibernate.cfg.xml` located in `src/management/src/main/resources`, depending on your configuration set properties
    * connection.url
    * connection.username
    * connection.password


## VirtualBox Setup

1. install the latest VirtualBox, get it from the VirtualBox download page
2. install the ExtensionPack, get it from the VirtualBox download page
3. create the following symbolic link: `ln -s /usr/lib/virtualbox/libvboxjxpcom.so /usr/lib/libvboxjxpcom.so

IMPORTANT: The home folder must be writeable for the user executing the NodeController, i.e. tomcat6

