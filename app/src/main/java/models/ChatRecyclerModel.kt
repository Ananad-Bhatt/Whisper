package models

data class ChatRecyclerModel(
    val title: String,
    val img: String, // Resource ID for the image
    val subTitle: String,
    val key:String,
    val uid:String,
    val fcm:String
)