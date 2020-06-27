# Remote Java Debugger

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