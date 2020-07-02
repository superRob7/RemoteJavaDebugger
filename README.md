- [Overview](#overview)
- [Quick Installation Guide](#quick-installation-guide)
- [Getting Started](#getting-started)
- [Connecting To A Target](#connecting-to-a-target)
  - [Local Connection](#local-connection)
  - [Attaching Connection](#attaching-connection)
  - [Listening Connection](#listening-connection)
- [Uploading A Script](#uploading-a-script)
- [Links](#links)

# Overview 
The premise of the prototype is to encapsulate the best available debugging methods and techniques into one tool. The prototype aims to harness the power and functionality of an IDE debugger, without requiring modification of the source code as well as eradicating irrelevant and unnecessary information, cluttering the user-interface. Essentially creating a hybrid debugging technique. Upon conclusion of the project, the finalised prototype offers the ability to:

* Connect to a target debuggee via three means: 
  * Launch  
     * The connection specifics are gathered through the UI and the target debugger’s compiled file is then selected by a file picker 
  * Socket Attach 
     * The connection specifics are gathered through the UI and used to attempt the remote connection 
  * Socket Listen 
     * The connection specifics are gathered through the UI and used to attempt the remote connection 
* View source code line numbers to assist script development 
* Upload XML Script files via a file picker 
* Execute script commands 
* Display relevant variable information  
* Log relevant debugger and debuggee events separately 

Fundamentally, the prototype can be broken down into independent sections that collectively encapsulate the debugger. The first section is communication, which lays the foundation for any debugging session as both parties must be able to communicate in order for information to be gathered. Secondly are script details, as the debugger must be able to internally comprehend the actions specified by the user’s script. Lastly is execution control, which is the capability to apply scripted command to the correct time of execution and ensure that consistent and reliable reads are taken from the debuggee.  

# Quick Installation Guide
The prototype is dependent on the JavaFX libraries which are no longer part of the standard Java JDK. For the debugger to be launched or for future development these libraries are fundamental. The debugger is also heavily dependent on the external ‘tools.jar’ library that facilitates the JDI interface.  The packages and classes included are as follows, listed classes are fundamental. 

<table>
  <tbody>
    <tr>
      <td align="center">Key Features<br>
        <span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
        <span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
        <span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
        <span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
        <span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
        <span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
        <span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>    
        <span>&nbsp;&nbsp;</span>
      </td>
      <td align="center">Examples<br>
        <span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
        <span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
        <span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
        <span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
        <span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>        
        <span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
        <span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
        <span>&nbsp;&nbsp;</span>
      </td>
      <td align="center">Supported Methods<br>
        <span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
        <span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
        <span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
        <span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>    
        <span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
        <span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
        <span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>     
        <span>&nbsp;&nbsp;</span>        
      </td>
    </tr>
  </tbody>
</table>

| Package Name |   Class/Resource Name    |
| :----------: | :----------------------: |
| application  |        Main.java         |
| Connection   |     Connection.java      |
|              |  ConnectionAttach.java   |
|              |  ConnectionLaunch.java   |
|              |  ConnectionListen.java   |
|              |  ConnectionManager.java  |
| Controllers  | ConnectionContoller.java |
|              |   MainController.java    |
| Debuggee     |      Debuggee.java       |
| Events       |   EventDispatcher.java   |
|              |    EventHandler.java     |
| Functions    |  ConnectionBundle.java   |
|              |   ConnectionEnums.java   |
|              | DebuggingStageEnums.java |
|              | InformationBuilder.java  |
|              |         Log.java         |
|              |  UserNotification.java   |
| script       |     BreakPoint.java      |
|              |       Script.java        |
|              |   XMLScriptReader.java   |
| xmlLayouts   |  connectionLayout.fxml   |
|              |     mainLayout.fxml      |

Two solutions are available for the JavaFX libraries for both aspects of the installation. The quickest solution is to use Java 8 as the version to execute or develop the debugger, because the necessary JavaFX libraries the debugger is dependent on are included as standard, requiring no additional alterations. 

The second solutions allow for any Java version to be used but requires that the external libraries are included in the launching arguments or build path for development. The JavaFX libraries can be downloaded separately and attached a long side any JDK version. The external tools.jar is only necessary for future development as it is already included in the executable version of the debugger, as the tools.jar file was explicitly included in the creation of the jar file. For future development the tools.jar file is only accessible through the JDK and Not JRE. It can be found in the ‘JDKDirectory/lib/tools.jar. 

# Getting Started
The scripted debugger can be executed as a stand alone application by executing the 'Debugger.jar' file found in the 'HelloWorld Example' folder. To continue development the 'Debugger' folder holds the source code, if you are not using Java 8 then you must add the JavaFX libraries to the build path of the project. 

For the debugger to be able to control the execution of the debuggee, you must compile the target with debugging information enabled. To insure this information is generated the '-g' command must be included within the compiler command to the class that contains the 'main' method. For example the following command would be executed within the file directory of the target program:
  * javac -g HelloWorld.java

# Connecting To A Target
A connection can be established in 3 ways:
* Local 
* Socket Attach
* Socket Listen

During a remote debugging session, one party must be the 'server' and the other the clint. These action are used to establish the connection between the debugger and debuggee (target program). When performing an attaching connection the debuggee **IS** the server. Meaning that this program will wait of the debugger to attach to the specified socket before executing. An example of the command used to launch the debugger in this state can be found in the 'attach.bat' file. 

A listening connection is the opposite of the attaching connector, this time the debugger is the server. Which requires the debuggee (target program) to connect to the socket the debugger is listening at for a connection. Once the connection is established the debugger can control the execution of the debuggee. 

## Local Connection 
When the debugger is launched it will automatically select the 'Local Connection' type from the 'Connection type' section. Insure the ‘Suspend’ toggle is selected from the connection details. The click the 'Connect' button, this will prompts a file picker allowing you to select the '**.class**' any where on the local machine. Once a the class file has been selected the connection will be attempted, a successful connection will cause the UI to load a new page.

## Attaching Connection
Select the 'Remote Connection' toggle from the 'Connection Type' section. This causes the UI to allow you to enter the IP address of the target and the port number, which identify the socket that will be used to establish the connection. When using an attaching connection as stated previously the debuggee is the server hosting the debugging session, meaning that it must be up and waiting before the debugger attempts to make the connection. To do this run the 'attach.bat' file and changing the 'HelloWorld' to the name of the target class. This file does not need to be executed but the command it contains **must** be executed. Do **not** select the 'server' when attempting this time of connection as this connection is reliant on the target hosting the session. Then click the connect button, a successful connection will cause the UI to load a new page.

## Listening Connection
Select the 'Remote Connection' toggle from the 'Connection Type' section. Then insure the 'server' toggle **is** selected, causing the UI to only allowing you to enter the port number. The IP input is uneatable as the debugger automatically uses the address of the local machine. The press the 'Connect' button. Once the button has been clicked the debugger will listen for a connection at the specified port. For the connection to be established the command contended within the 'listen.bat. file must be executed within a set period, if the debugger does not reserve a connection within the time limit then the connection will fail and you will have to reattempt the connection, a successful connection will cause the UI to load a new page..   

# Uploading A Script
Once a connection has been successful established, the debugger option are available to you. Navigate to the script panel, from this panel an  XML script can be uploaded to the debugger vie the file picker that is prompted when the 'Upload' button is clicked. The file must be of any XML extension and must include the name of the class to add the breakpoint as well as one line number for the breakpoint to be applied to. An example of the required script structure can be found within the 'HelloWorldScript.xml' file.

# Links
* Java 8
  * https://www.oracle.com/java/technologies/javase-jdk8-downloads.html 
* JavaFX
  * https://openjfx.io/

