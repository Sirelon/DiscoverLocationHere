package com.sirelon.discover.location.feature.places.categories.api

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
class PlacesResponse(
    @SerializedName("results")
    val result: PlacesResult
)

@Keep
class PlacesResult(
    @SerializedName("items")
    val items: List<PlaceItemResponse>
)

@Keep
data class PlaceItemResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("icon")
    val icon: String,
    @SerializedName("position")
    val position: List<Double>,
    @SerializedName("openingHours")
    val openningHoursResponse: OpenningHoursResponse?
)

data class OpenningHoursResponse(
    @SerializedName("text")
    val text: String,
    @SerializedName("isOpen")
    val isOpen: Boolean
)