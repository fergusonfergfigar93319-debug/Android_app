package com.example.tx_ku.core.network

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import retrofit2.http.GET

/**
 * 用户画像聚合接口，与《元流同频_后端接口联调手册》**§3.2 `GET /profiles/me`** 对齐。
 *
 * 说明：口语中的「拉用户 me」对应本接口（非 `GET /users/me`）；Retrofit 基址已含 `…/api/v1/`，此处仅写 **`profiles/me`**。
 */
data class ProfileMeEnvelope(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: ProfileMeData?
)

data class ProfileMeData(
    @SerializedName("profile") val profile: ProfileDto?,
    @SerializedName("buddyCard") val buddyCard: BuddyCardDto?,
    /** 与 [com.example.tx_ku.core.model.AgentTuning] 字段名一致时可直接反序列化；否则由 Repository 容错忽略 */
    @SerializedName("agentTuning") val agentTuning: JsonElement? = null
)

data class ProfileDto(
    @SerializedName("userId") val userId: String? = null,
    @SerializedName("nickname") val nickname: String? = null,
    @SerializedName("avatarUrl") val avatarUrl: String? = null,
    @SerializedName("bio") val bio: String? = null,
    @SerializedName("cityOrRegion") val cityOrRegion: String? = null,
    @SerializedName("preferredGames") val preferredGames: List<String>? = null,
    @SerializedName("rank") val rank: String? = null,
    @SerializedName("activeTime") val activeTime: List<String>? = null,
    @SerializedName("mainRoles") val mainRoles: List<String>? = null,
    @SerializedName("playStyle") val playStyle: String? = null,
    @SerializedName("target") val target: String? = null,
    @SerializedName("voicePref") val voicePref: String? = null,
    @SerializedName("noGos") val noGos: List<String>? = null,
    @SerializedName("personalityArchetype") val personalityArchetype: String? = null,
    @SerializedName("agentVoicePref") val agentVoicePref: String? = null,
    @SerializedName("agentVisualTheme") val agentVisualTheme: String? = null,
    @SerializedName("favoriteEsportsHint") val favoriteEsportsHint: String? = null,
    @SerializedName("proPersonaStyle") val proPersonaStyle: String? = null
)

data class BuddyCardDto(
    @SerializedName("cardId") val cardId: String? = null,
    @SerializedName("userId") val userId: String? = null,
    @SerializedName("tags") val tags: List<String>? = null,
    @SerializedName("declaration") val declaration: String? = null,
    @SerializedName("rules") val rules: List<String>? = null,
    @SerializedName("proPersonaLabel") val proPersonaLabel: String? = null,
    @SerializedName("favoriteEsportsHint") val favoriteEsportsHint: String? = null
)

interface ProfileApiService {
    @GET("profiles/me")
    suspend fun getMyProfile(): ProfileMeEnvelope
}
