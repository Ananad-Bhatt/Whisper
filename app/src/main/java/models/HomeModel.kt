package models

data class HomeModel (
    val title: String,
    val img: Int, // Resource ID for the image
    val subTitle: String,
    val post: Int,
    val score: Int,
)