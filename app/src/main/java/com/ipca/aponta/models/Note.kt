package com.ipca.aponta.models

data class Note(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val userId: String = "",
    val sharedWith: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis(),
    val colorIndex: Int = 0
)