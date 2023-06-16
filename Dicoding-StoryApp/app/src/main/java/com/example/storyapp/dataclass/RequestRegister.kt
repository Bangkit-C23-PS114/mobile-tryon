package com.example.storyapp.dataclass

import android.os.Parcelable
import android.provider.ContactsContract.FullNameStyle
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


data class RequestRegister(
    var email: String,
    var password: String,
    var firstName: String,
    var lastName: String,
)
data class RequestLogin(
    var email: String,
    var password: String
)

data class ResponseMsg(
    var error: Boolean,
    var message: String
)


data class ResponseLogin(
    var message: String,
    var data: List<DataLogin>,
)

@Parcelize
data class DataLogin(
    @SerializedName("id") var id: Int,
    @SerializedName("firstName") var firstName: String,
    @SerializedName("lastName") var lastName: String,
    @SerializedName("fullName") var fullName: String,
    @SerializedName("email") var email: String,
    @SerializedName("password") var password: String
):Parcelable

data class LoginResult(
    var userId: String,
    var name: String,
    var token: String
)

data class ResponseStory(
    var error: String,
    var message: String,
    var listStory: List<ListStory>
)

@Parcelize
data class ListStory(
    var id: String,
    var name: String,
    var description: String,
    var photoUrl: String,
    var createdAt: String,
    var lat: Double,
    var lon: Double
) : Parcelable

