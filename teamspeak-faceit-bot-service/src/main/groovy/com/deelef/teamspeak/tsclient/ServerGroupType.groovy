package com.deelef.teamspeak.tsclient

/**
 * Created by gontareka on 2017-03-12.
 */
public enum ServerGroupType {
    STEAM,FACEIT,

    FACEIT_LVL {

        @Override
        public String withLevel(String lvl) {
            return "FACEIT_LVL_" + lvl;
        }
    };

    public String withLevel(String value){
        return null;
    }

}
