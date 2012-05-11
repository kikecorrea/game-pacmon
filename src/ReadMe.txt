------------------------
FILE STRUCTURE OF PACMON
------------------------

There are 3 packages in PacMon source
1. pacmon: this contains codes for the single player and basic structures of the app

2. multipacmon: this contains all the files multiplayer client will need to run

3. clientmultiserver: this is the server running on computer that allows 2 clients to connect to play (old codes but it is there)



I. pacmon package: notes:
	1. MenuActivity: this is the Activity that will run when the app is started. This is the menu and allows user to select SinglePlayer, Multiplayer, About, Options and Exit the app.
	
	2. SinglePlayer:
		- LevelSelectActivity  will start when user clicks on SinglePlayer and user can choose the level.
		- GameActivity is started when level is selected. It will initialize GameSurfaceView, GameEngine and SoundEngine.
		
	Maze, Monster, Pacmon are the models classes.
	
	3. GamePrefsActivity: is the options menu
	
II. multipacmon package:
	1. There are MGameActivity, MGameEngine and MGameSurfaceView which have been modified from pacmon package to work with multiplayer.
	
	2. Receiving and Sending are threads for receiving/sending data from/to server.
	
	3. CircularQue is the data structure storage of data from server/client
	
	4. 
	
III. clientmultiserver:
	1. 