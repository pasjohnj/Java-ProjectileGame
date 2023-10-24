Java-ProjectileGame
===================

Created as a final project for WCC class CPS261: Advanced Java Concepts

The purpose of this project was to demonstrate all the concepts the student had learned to that point.  This project includes a 
number of self-designed classes, inheritance, threading, Swing, networking and some basic physics.  
It's based off of the old QBasic game, Gorillas.  

This repo contains both the "networked version" from class, which also contains the networking software utilized in class, and
an "unnetworked version", which does not require a network to play on.  The networked version could actually be played between
any two computers that had both the networking software and game itself.  As it requires two separate screens to play on, I 
refactored the code to be played on a single computer, and requiring no networking software.  The class that has the "main" method
in the networked version is "MyMain.java" and the class in the unnetworked version is "Gorillas.java".  
