package com.example.tx_ku.core.network

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

data class LoginRequest(
    val email: String,
    val passKey: String
)

data class LoginApiEnvelope(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: LoginData?
)

data class LoginData(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("avatar_url") val avatarUrl: String
)

data class TokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String
)

/** Gson 对泛型 [ApiResponse] 的 data 字段易退化为 Map，刷新接口使用专用壳体。 */
data class RefreshTokenApiEnvelope(
    val code: Int,
    val message: String,
    val data: TokenResponse?
)

interface AuthApiService {
    /** 与 [com.example.tx_ku.core.network.ApiConstants] 的 `…/api/v1/` 基址拼接，勿再使用根路径 `/auth/…`。 */
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginApiEnvelope

    /** 同步 [Call]，供 [TokenAuthenticator] 在拦截链中执行。 */
    @FormUrlEncoded
    @POST("auth/refresh")
    fun refreshToken(@Field("refresh_token") refreshToken: String): Call<RefreshTokenApiEnvelope>
}
