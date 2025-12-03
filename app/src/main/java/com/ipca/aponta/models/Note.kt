package com.ipca.aponta.models

data class Note(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val userId: String = "",
    val colorIndex: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val sharedWith: List<String> = emptyList(),
    val pendingShares: List<String> = emptyList()
)