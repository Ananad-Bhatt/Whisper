package models


class ChatModel {
    private var USER_KEY: String? = null
    private var MESSAGE: String? = null
    private var TIMESTAMP: Long? = null

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
