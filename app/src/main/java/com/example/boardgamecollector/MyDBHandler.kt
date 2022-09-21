package com.example.boardgamecollector

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class MyDBHandler(
    context: Context, name: String?,
    factory: SQLiteDatabase.CursorFactory?, version: Int): SQLiteOpenHelper(context,
DATABASE_NAME, factory, DATABASE_VERSION) {
                      companion object {
                          private val DATABASE_VERSION = 1
                          private val DATABASE_NAME = "boardgamesDB.db"
                          val TABLE_BOARDGAMES = "boardgames"
                          val TABLE_RANKHISTORY = "rankhistory"
                          val COLUMN_ID = "bggID"
                          val COLUMN_GAME_TYPE = "gameType"
                          val COLUMN_ORIGINAL_NAME = "originalName"
                          val COLUMN_RELEASE_YEAR = "releaseYear"
                          val COLUMN_CURRENT_RANK = "currentRank"
                          val COLUMN_GAME_THUMBNAIL = "gameThumbnail"
                          val COLUMN_GAME_DESCRIPTION = "gameDescription"
                          val COLUMN_GAME_MIN_PLAYERS = "gameMinPlayers"
                          val COLUMN_GAME_MAX_PLAYERS = "gameMaxPlayers"
                          val COLUMN_RANK = "rank"
                          val COLUMN_DATE = "date"
                          val FK_NAME = "FK_bggID"
                      }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_BOARDGAMES_TABLE = ("CREATE TABLE $TABLE_BOARDGAMES" +
                "(" +
                "$COLUMN_ID INTEGER NOT NULL UNIQUE PRIMARY KEY," +
                "$COLUMN_GAME_TYPE TEXT," +
                "$COLUMN_ORIGINAL_NAME TEXT," +
                "$COLUMN_RELEASE_YEAR TEXT," +
                "$COLUMN_CURRENT_RANK INTEGER," +
                "$COLUMN_GAME_THUMBNAIL TEXT," +
                "$COLUMN_GAME_DESCRIPTION TEXT," +
                "$COLUMN_GAME_MIN_PLAYERS INTEGER," +
                "$COLUMN_GAME_MAX_PLAYERS INTEGER" +
                ")"
                )

        val CREATE_RANKHISTORY_TABLE = ("CREATE TABLE IF NOT EXISTS $TABLE_RANKHISTORY" +
                "(" +
                "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_ID INTEGER," +
                "$COLUMN_RANK INTEGER," +
                "$COLUMN_DATE DATE," +
                "CONSTRAINT $FK_NAME FOREIGN KEY ($COLUMN_ID) REFERENCES $TABLE_BOARDGAMES($COLUMN_ID)" +
                ")"
                )

        db.execSQL(CREATE_RANKHISTORY_TABLE)
        db.execSQL(CREATE_BOARDGAMES_TABLE)

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int,
    newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BOARDGAMES")
        onCreate(db)
    }

    fun addGame(game: Game) {
        val values = ContentValues()
        values.put(COLUMN_ID, game.bggId)
        values.put(COLUMN_GAME_TYPE, game.gameType)
        values.put(COLUMN_ORIGINAL_NAME, game.originalTitle)
        values.put(COLUMN_RELEASE_YEAR, game.releaseYear)
        values.put(COLUMN_CURRENT_RANK, game.currentRank)
        values.put(COLUMN_GAME_THUMBNAIL, game.gameThumbnail)
        values.put(COLUMN_GAME_DESCRIPTION, game.gameDescription)
        values.put(COLUMN_GAME_MIN_PLAYERS, game.gameMinPlayers)
        values.put(COLUMN_GAME_MAX_PLAYERS, game.gameMaxPlayers)
        val db = this.writableDatabase
        db.insert(TABLE_BOARDGAMES, null, values)
        db.close()
    }

    fun deleteTable() {
        val db = this.writableDatabase
        db.delete("boardgames", null, null)
        db.close()
    }

    fun addRankGame(game: Game) {
        val values = ContentValues()
        values.put(COLUMN_ID, game.bggId)
        values.put(COLUMN_RANK, game.currentRank)
        val dateDD = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        values.put(COLUMN_DATE, dateDD)
        val db = this.writableDatabase
        db.insert(TABLE_RANKHISTORY, null, values)
        db.close()
    }

    fun sortGames(type: String, direction: String, sortColumn: String): MutableList<Game> {
        val query = "SELECT * FROM $TABLE_BOARDGAMES WHERE $COLUMN_GAME_TYPE LIKE '$type'"+
                "ORDER BY $sortColumn $direction"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)

        var games: MutableList<Game> = mutableListOf()

        var id = 0
        var gType: String? = null
        var oName: String? = null
        var rYear: String? = null
        var cRank = 0
        var gThumbnail: String? = null
        var gDescription: String? = null
        var gMinPlayers = 0
        var gMaxPlayers = 0

        if(cursor.moveToFirst()) {
            while(!cursor.isAfterLast) {
                id = cursor.getInt(0)
                gType = cursor.getString(1)
                oName = cursor.getString(2)
                rYear = cursor.getString(3)
                cRank = cursor.getInt(4)
                gThumbnail = cursor.getString(5)
                gDescription = cursor.getString(6)
                gMinPlayers = cursor.getInt(7)
                gMaxPlayers = cursor.getInt(8)
                val game = Game(id, gType, oName, rYear, cRank, gThumbnail, gDescription, gMinPlayers, gMaxPlayers)
                games.add(game)
                cursor.moveToNext()
            }
        }
        db.close()
        return games
    }

    fun deleteDuplicatesRanks() {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_RANKHISTORY WHERE id NOT IN (SELECT MIN(id) FROM $TABLE_RANKHISTORY GROUP BY $COLUMN_ID, $COLUMN_RANK, $COLUMN_DATE)")
        db.close()
    }

    fun findGames(type: String): MutableList<Game> {
        val query = "SELECT * FROM $TABLE_BOARDGAMES WHERE $COLUMN_GAME_TYPE LIKE '$type'"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)

        var games: MutableList<Game> = mutableListOf()

        var id = 0
        var gType: String? = null
        var oName: String? = null
        var rYear: String? = null
        var cRank = 0
        var gThumbnail: String? = null
        var gDescription: String? = null
        var gMinPlayers = 0
        var gMaxPlayers = 0

        if(cursor.moveToFirst()) {
            while(!cursor.isAfterLast) {
                id = cursor.getInt(0)
                gType = cursor.getString(1)
                oName = cursor.getString(2)
                rYear = cursor.getString(3)
                cRank = cursor.getInt(4)
                gThumbnail = cursor.getString(5)
                gDescription = cursor.getString(6)
                gMinPlayers = cursor.getInt(7)
                gMaxPlayers = cursor.getInt(8)
                val game = Game(id, gType, oName, rYear, cRank, gThumbnail, gDescription, gMinPlayers, gMaxPlayers)
                games.add(game)
                cursor.moveToNext()
            }
        }
        db.close()
        return games
    }

    fun findRankOfGame(gameID: Int): MutableList<Rank> {
        val query = "SELECT * FROM $TABLE_RANKHISTORY WHERE $COLUMN_ID = $gameID ORDER BY $COLUMN_DATE DESC"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)

        var ranks: MutableList<Rank> = mutableListOf()

        var rank = 0
        var date: String? = null

        if(cursor.moveToFirst()) {
            while(!cursor.isAfterLast) {
                rank = cursor.getInt(2)
                date = cursor.getString(3)
                val gameRank = Rank(rank, date)
                ranks.add(gameRank)
                cursor.moveToNext()
            }
        }
        db.close()
        return ranks
    }
}