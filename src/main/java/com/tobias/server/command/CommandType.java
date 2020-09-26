package com.tobias.server.command;

public enum CommandType {
    GAME_SETCARD,
    GAME_CLIENTDRAWCARD,
    GAME_OPPONENTDRAWCARD,
    GAME_UNO,
    GAME_FORGOTUNO,
    GAME_PLAYERDISCONNECT,
    GAME_START,
    GAME_SETDECKCOUNT,
    GAME_SETOPPONENTPLAYERCARDCOUNT,
    GAME_CLIENTLAYCARD,
    GAME_OPPONENTLAYCARD,
    GAME_SETNEXTTURN,
    GAME_SKIPTURN,
    GAME_SETCOLOR,
    GAME_CLIENTSETCOLOR,
    GAME_FINISHED,


    CLIENT_CONNECT,
    CLIENT_CONNECTED,
    CLIENT_POLL,
    CLIENT_READY,
    CLIENT_REGISTERID,
    CLIENT_DISCONNECT,
    CLIENT_SETUSERNAME,


    WORKER_UNKNOWNCOMMAND
}