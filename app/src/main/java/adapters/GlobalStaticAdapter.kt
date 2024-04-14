package adapters

import android.view.View
import android.view.ViewGroup
import project.social.whisper.R

class GlobalStaticAdapter {
    companion object{

        //Contact Name and Contact Number
        var contactName = ""
        var contactNumber = ""

        //USER PROFILE
            //User uid (From Email)
            var uid = ""

            //user Key (Inside Email)
            var key = ""

            //About
            var about = ""

            //Account type
            var accountType = "PUBLIC"

            //Image
            var imageUrl:String = (R.string.image_not_found).toString()

            //UserName
            var userName = ""

        //USER NOTIFICATION TOKEN
            var fcmToken = ""

        //ANOTHER USER
            //User uid (From Email)
            var uid2 = ""

            //user Key (Inside Email)
            var key2 = ""

            //About
            var about2 = ""

            //Account type
            var accountType2 = ""

            //Image
            var imageUrl2 = ""

            //UserName
            var userName2 = ""

        //ANOTHER USER NOTIFICATION TOKEN
            var fcmToken2 = ""

        //Location
        var lat = ""
        var long = ""

        //Alias
        var alias2 = ""

        //Disable user clicking
        fun setViewAndChildrenEnabled(view: View, enabled: Boolean) {
            view.isEnabled = enabled
            if (view is ViewGroup) {
                for (i in 0 until view.childCount) {
                    val child = view.getChildAt(i)
                    setViewAndChildrenEnabled(child, enabled)
                }
            }
        }

    }
}