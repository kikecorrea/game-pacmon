# Hanzen's Journal #
## Weekly Journal ##

## Feb 13 - Feb 18 ##
  * Finished 18 android tutorials and 15 ios tutorials

## Feb 20- Feb 24 ##
  * finish all xmlvm tutorials
  * created mockup story board for pac-mon game
  * spent 1 day trying to fix patch for submission of tutorials
  * had a meeting with my team mate qui about the architecture and design of our game.
  * research a little bit about the architecture we will use for our multiplayer feature
  * read about http://www.skiller-games.com/developers.aspx about developing multiplayer games

## Feb27 - March 2 ##
  * researched on implementation of web server, and what architecture to use like UDP or tcp/ip
  * started working on web server

## March2 - March 9 ##
  * finish implementing the web server using UDP socket
  * Can pass simple data back and forth from client to server


## March 12 - March16 ##
  * Help out implementing the layout for maze
  * Researhed on how to port box2d game engine to android. Box2d is written in C, while android is in java so it needs somekind of interface generator
  * Implemented simple app for android that sends and receive data to server


## March 19 - March 23 ##
  * Move the game engine into server, Ran into problems with multithread, synchronization and deadlock. Had problems with the latency from sending data and updating the view.
  * Tried to optimize the movement. There is little bit lag between sending the data and player movement
  * Research on input prediction and lag compensation to improve player's movement

## March 26 - March 30 ##
  * Prepare for presentation
  * Change some code so that I can control the pacman using keyboard. Will be use for Demo
  * Optimize server to send data 40 update per second

## April 2 - April 6 ##
  * Still working on multiplayer server,
  * Research on lag compensation, interpolation, early prediction for multiplayer option

## April 9 - April 14 ##
  * Implemented lag compensation, interpolation,
  * Added circular que for receiving data for client side
  * Implemented multiple client connection for server side

## April 16- April 20 ##
  * Researched  UDP Broadcast

## April 23 - April  27 ##
  * Implemented UDP Broadcast, now client can connect to server without knowing configuration settings
  * Tried implementing push down maze from server (Still in progress)

## April 30 - May 4 ##
  * Refactor code
  * Move server to client side, now client can host as a server or client

## May 7 - May 11 ##
  * Add documentation on code
  * Add manual ip configuration option for server feature
  * refactoring code to reduce garbage collector

## May 14 - May 18 ##
  * Final clean up of code
  * add more documentation