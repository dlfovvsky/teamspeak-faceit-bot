package com.deelef.teamspeak.tsclient

/**
 * Created by adam on 16.03.2017.
 */
class CommandExecutor {
    static void execute(Command command){
        if(!command.command()){
            throw new RuntimeException("Error occured while executing: " +command )
        }
    }
}
