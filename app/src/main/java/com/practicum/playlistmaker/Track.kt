package com.practicum.playlistmaker

data class Track(
    val trackId: Int,           // Идентификатор трека
    val trackName: String,      // Название композиции
    val artistName: String,     // Имя исполнителя
    val trackTimeMillis: Int,   // Продолжительность трека
    val artworkUrl100: String,  // Ссылка на изображение обложки
)