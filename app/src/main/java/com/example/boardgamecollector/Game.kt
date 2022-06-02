package com.example.boardgamecollector

class Game {
    var bggId: Int = 0
    var gameType: String? = null
    var originalTitle: String? = null
    var releaseYear: String? = null
    var currentRank: Int = 0
    var gameThumbnail: String? = null
    var gameDescription: String? = null
    var gameMinPlayers: Int = 0
    var gameMaxPlayers: Int = 0

    constructor(id: Int, gType: String, oTitle: String, rYear: String, cRank: Int, gThumbnail: String,
    gDescription: String, gMinPlayers: Int, gMaxPlayers: Int) {
        this.bggId = id
        this.gameType = gType
        this.originalTitle = oTitle
        this.releaseYear = rYear
        this.currentRank = cRank
        this.gameThumbnail = gThumbnail
        this.gameDescription = gDescription
        this.gameMinPlayers = gMinPlayers
        this.gameMaxPlayers = gMaxPlayers
    }
}