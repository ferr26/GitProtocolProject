# GitProtocol on P2P Networks

## What is the Git Protocol?

It is a system allows the users to create a new repository in a specific folder, add new files to be tracked by the system, apply the changing on the local repository (commit function), push the changing in the network and pull the changing from the network.

## Additional Features
In addition to the basic operations defined by the project, I have been developed:

 1. Clone Repository.
 2. Delete Repository
 3. Show Local History, to view the commits made in the local repository without the push in the network
 4. Show Remote History, to view the commits made in the remote Repository
 5. Show File in Directory, to view a file present in Directory
 6. Show file in Repository, to view a file presente in Remote Repository
 7. Git Config, where it is possible to set the Author of the Repository.
 8. Show local Repository, to view a local Repository
 
##  Technologies

 - Eclipse 
 - Apache Maven 
 - Tom P2P
 - Java 
 - JUnit 
 - Docker.

## Project Structure

**The main folder of the project has various packages:**

**In the it.p2p.git package there are:**

 - GitProtocol, the API that defines the basic operations to be developed
 - GitProtocolExtends, instead defines the additional operations that have been developed
 
**In the it.p2p.git.entity package there are:**
 - Commit, which represents the class of changes to the file in the local repository
 - Repository, the object that represents the Repository
 - FileIndex, to index the file

**In the it.p2p.git.impl package there is:**

 -  GitProtocolImpl, implements the GitProtocol interface using the Tom P2P library
 
 **In the it.p2p.git.menu package there is:**
 
 - GitProtocolMenu, the menu with which you can interact to use the Git features
 
**In the it.p2p.git.ultil package there are:**

 - ManageData, represents the class used to manipulate the Date and Time.
 - ManageFile, represents the class used to manage files (to try locally just set the boolean PREFIXLOCAL variable equal to true).
 
**The test folder containing the GitProtocolUnitTest class, in which the Test Cases have been implemented.**


##  Build your app in a Docker container

The first step is to build the Container docker, and it is possible through the following command:

 1. docker build --no-cache -t gitprotocolproject .`

Second step is to start the Master Peer in interactive mode (-i) and with two (-e) environment variables:

 2. docker run -i --name MASTER-PEER -e MASTERIP="127.0.0.1" -e ID=0 gitprotocol`

So after starting the Master, it is necessary to obtain the IP address of the Container through the commands:

 - Check the docker : `docker ps`
 - Check the IP address:  `docker inspect <container ID>`
 
Now after this short procedure it is possible to start the other peers by changing the ID
 - docker run -i --name PEER-1 -e MASTERIP="172.17.0.2" -e ID=1 gitprotocolproject`

### Project Developed By:
***Rosa Ferraioli, matr. 0522500721**.* 
