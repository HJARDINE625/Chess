package model;

import chess.ChessGame;

//Note, specifically because of how I implemented ChessGame, I may need to modify the ToString method here to make it return the right thing...
//Otherwise I may need to change how ChessGame and ChessPiece work...

record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame implementation) {}
