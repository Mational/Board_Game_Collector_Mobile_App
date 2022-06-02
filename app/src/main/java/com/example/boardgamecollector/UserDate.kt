package com.example.boardgamecollector

class UserDate {
    var userName: String? = null
    var date: String? = null
    var games: String? = null
    var expansions: String? = null

    constructor(){}

    constructor(username:String?, date:String?, games: String?, expansions: String?) {
        this.userName = username
        this.date = date
        this.games = games
        this.expansions = expansions
    }

    constructor(line:String?) {
        if(line!=null){
            val tokens=line.split(";")
            if(tokens.size==4){
                userName = tokens[0]
                date = tokens[1]
                games = tokens[2]
                expansions = tokens[3]
            }
        }
    }

    fun getUN(): String {
        return "$userName"
    }

    fun getLSD(): String {
        return "$date"
    }

    fun getGamesNumber(): String {
        return "$games"
    }

    fun getExpansionsNumber(): String {
        return "$expansions"
    }

    fun toCSV():String {
        return "$userName;$date;$games;$expansions\n"
    }
}