package dev.theolm.freestyle_libre_alarm.data.local.database

import android.content.Context

object LibreDatabaseProvider {
    fun getDatabase(context: Context): LibreDatabase {
        return LibreDatabase.getDatabase(context)
    }
}