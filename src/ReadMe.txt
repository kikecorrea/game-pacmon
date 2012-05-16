------------------------
FILE STRUCTURE OF PACMON
------------------------

There are 3 packages in PacMon source
1. pacmon: this contains codes for the single player and basic structures of the app
2. multipacmon: contains all the files for multiplayer client
3. clientmultiserver: phone hosts as the server
4. UDPServer folder: server running on a separate machine with two android device connecting to it


I. pacmon package: 

Classes:
MenuActivity 		   - handles the menu activity giving options for single player, multiplayer, options, exit
ClientOrServer 		 - handles dialog box for selecting client or server
LevelSelectActivity - sets the level for the game play
GameActivity 		   - handles the accelerometer input, initialized GameSurfaceView, GameEngine, SoundEngine
GameEngine 		     - handles the movement of pacmon, ghost, maze
GameSurfaceView 	  - handles the rendering of screen
Maze 				       - contains the layout of the maze
Monster 			      - contains the ghost code
Pacmon 				     - contains the pacmon code
SoundEngine 		    - handles the sound during game play
GamePrefsActivity 	- sets the preference for the game play
ShowSettingActivity - sets the settings for the game play


Game Logic:
	1. MenuActivity: this is the Activity that will run when the app is started. 
	This is the menu and allows user to select SinglePlayer, Multiplayer, About, Options and Exit the app.
	
	2. SinglePlayer:
		- LevelSelectActivity  will start when user clicks on SinglePlayer and user can choose the level.
		- GameActivity is started when level is selected. It will initialize GameSurfaceView, GameEngine and SoundEngine.
		- Maze, Monster, Pacmon are the models classes.
	
	3. GamePrefsActivity: is the options menu
	
	
II. multipacmon package:

Classes:
MGameActivity, MGameEngine, MGameSurfaceView - have been modified slightly from pacmon package to work w/ multiplayer
AutoDiscovery 			    - listens for UDP broadcast to get IP address of server
ClientConnectionSetup 	- communicates with server to send client info like port and ip address
CircularQue  		     	- data structure storage of data from server/client. Deals with lag compensation, and early prediction
Receiver 				       - handles the receiving data from server
Sender 					       - handles the sending data to server

Game Logic

	1. MultiPlayer 
		- MGameActivity will be invoke then it will initialize AutoDiscovery
		- After AutoDiscovery founds the IP address, it will send IP information to ClientConnectionSetUp
		- ClientConnectionSetup will establish connection to server and give the client's port and IP address
		- After receiving info from server, MGameAcitivty will invoke Sender,Receiver, MGameEngine, MGameSurfaceView and start the game
	
	
III. clientmultiserver:

Classes:
CMGameActivity, CMGameEngine, CMGameSurfaceView - have been modified slightly from pacmon package to work w/ client as a server
ServerAutoDiscovery 		- sends UDP broadcast, broadcasting it's own IP address until clients connects to it
ServerConnectionInfo		- sends information about the server i.e. port and IP address to client
ServerReceiving				 - handles the receiving data from client
ServerSending 			 	 - handles the sending data to client
ServerThread				    - parent thread of ServerAutoDiscovery, ServerConnectionInfo, ServerReceiving, ServerSending


Game Logic

	1. MultiPlayer - phone as the server
		- CMGameActivity wil be invoke then it will instantiate Server Thread
		- ServerThread will create child thread ServerAutoDiscovery
		- ServerAutoDiscovery will start broadcasting the server's IP address
		- After a clients connects to the server, ServerConnectionInfo will be invoke and send server's port to client
		- ServerThread will then instantiate ServerSending, ServerReceiving then will notify CMGameActivity to start G

	