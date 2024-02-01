package com.tatara.journalapp

import java.sql.Timestamp

data class Journal(
    val title: String,
    val description: String,
    val img: String,

    val userId: String,
    val date: com.google.firebase.Timestamp,
    val username: String
)
