package models


class ChatModel {
    var USER_KEY: String? = null
    var MESSAGE: String? = null
    var TIMESTAMP: Long? = null

    constructor(USER_KEY: String?, MESSAGE: String?, TIMESTAMP: Long?) {
        this.USER_KEY = USER_KEY
        this.MESSAGE = MESSAGE
        this.TIMESTAMP = TIMESTAMP
    }

    constructor(USER_KEY: String?, MESSAGE: String?) {
        this.USER_KEY = USER_KEY
        this.MESSAGE = MESSAGE
    }

    constructor()

}
