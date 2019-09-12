package com.sirelon.discover.location.feature.places.categories.api

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
class CategoriesResponse(
    @SerializedName("items")
    val categories: List<CategoryItemResponse>
)

@Keep
data class CategoryItemResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("icon")
    val icon: String,
    @SerializedName("within")
    val within: List<String>
)