package com.practicum.playlistmaker

data class Track(
    val trackId: Int,               // Идентификатор трека
    val trackName: String,          // Название композиции
    val artistName: String,         // Имя исполнителя
    val trackTimeMillis: Int,       // Продолжительность трека
    val artworkUrl100: String,      // Ссылка на изображение обложки
    val collectionName: String,     // Название альбома
    val releaseDate: String,        // Год релиза
    val primaryGenreName: String,   // Жанр трека
    val country: String             // Страна исполнителя
)