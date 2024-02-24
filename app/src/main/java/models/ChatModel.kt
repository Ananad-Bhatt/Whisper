package models


class ChatModel {
    var SENDER_KEY: String? = null
    var MESSAGE: String? = null
    var TIMESTAMP: Long? = null

    constructor(SENDER_KEY: String?, MESSAGE: String?, TIMESTAMP: Long?) {
        this.SENDER_KEY = SENDER_KEY
        this.MESSAGE = MESSAGE
        this.TIMESTAMP = TIMESTAMP
    }

    constructor(SENDER_KEY: String?, MESSAGE: String?) {
        this.SENDER_KEY = SENDER_KEY
        this.MESSAGE = MESSAGE
    }

    constructor()

}
