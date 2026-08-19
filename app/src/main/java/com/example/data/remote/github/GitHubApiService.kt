package com.example.data.remote.github

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path

@JsonClass(generateAdapter = true)
data class GitHubRepoResponse(
    val id: Long,
    val name: String,
    @Json(name = "full_name") val fullName: String,
    val private: Boolean,
    val description: String?,
    @Json(name = "default_branch") val defaultBranch: String,
    @Json(name = "stargazers_count") val starsCount: Int,
    @Json(name = "forks_count") val forksCount: Int,
    @Json(name = "updated_at") val updatedAt: String?
)

@JsonClass(generateAdapter = true)
data class GitHubCommitResponse(
    val sha: String,
    val commit: CommitDetail,
    val author: GitHubUserDetail?
)

@JsonClass(generateAdapter = true)
data class CommitDetail(
    val message: String,
    val author: CommitAuthorDetail
)

@JsonClass(generateAdapter = true)
data class CommitAuthorDetail(
    val name: String,
    val email: String,
    val date: String
)

@JsonClass(generateAdapter = true)
data class GitHubUserDetail(
    val login: String,
    @Json(name = "avatar_url") val avatarUrl: String?
)

@JsonClass(generateAdapter = true)
data class GitHubContentItem(
    val name: String,
    val path: String,
    val sha: String,
    val size: Long?,
    val type: String, // "file", "dir"
    val content: String?, // base64 encoded content
    val encoding: String?
)

@JsonClass(generateAdapter = true)
data class GitHubPutFileRequest(
    val message: String,
    val content: String, // Base64 string
    val sha: String? = null,
    val branch: String? = null
)

@JsonClass(generateAdapter = true)
data class GitHubPutFileResponse(
    val content: GitHubContentItem?,
    val commit: CommitResponseSummary?
)

@JsonClass(generateAdapter = true)
data class CommitResponseSummary(
    val sha: String,
    val message: String?
)

@JsonClass(generateAdapter = true)
data class GitHubBranchItem(
    val name: String,
    val protected: Boolean?
)

@JsonClass(generateAdapter = true)
data class CreateRepoRequest(
    val name: String,
    val description: String? = null,
    val private: Boolean = false,
    @Json(name = "auto_init") val autoInit: Boolean = true
)

@JsonClass(generateAdapter = true)
data class DeleteFileRequest(
    val message: String,
    val sha: String,
    val branch: String? = null
)

interface GitHubApiService {

    @GET("user")
    suspend fun getUserInfo(
        @Header("Authorization") authHeader: String,
        @Header("User-Agent") userAgent: String = "Sasa-AI-Agent"
    ): Response<GitHubUserDetail>

    @GET("user/repos")
    suspend fun listUserRepos(
        @Header("Authorization") authHeader: String,
        @Header("User-Agent") userAgent: String = "Sasa-AI-Agent"
    ): Response<List<GitHubRepoResponse>>

    @retrofit2.http.POST("user/repos")
    suspend fun createRepo(
        @Header("Authorization") authHeader: String,
        @Body body: CreateRepoRequest,
        @Header("User-Agent") userAgent: String = "Sasa-AI-Agent"
    ): Response<GitHubRepoResponse>

    @retrofit2.http.DELETE("repos/{owner}/{repo}")
    suspend fun deleteRepo(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Header("Authorization") authHeader: String,
        @Header("User-Agent") userAgent: String = "Sasa-AI-Agent"
    ): Response<ResponseBody>

    @GET("repos/{owner}/{repo}")
    suspend fun getRepo(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Header("Authorization") authHeader: String,
        @Header("User-Agent") userAgent: String = "Sasa-AI-Agent"
    ): Response<GitHubRepoResponse>

    @GET("repos/{owner}/{repo}/commits")
    suspend fun getCommits(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Header("Authorization") authHeader: String,
        @Header("User-Agent") userAgent: String = "Sasa-AI-Agent"
    ): Response<List<GitHubCommitResponse>>

    @GET("repos/{owner}/{repo}/contents/{path}")
    suspend fun getSingleContent(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("path") path: String = "",
        @Header("Authorization") authHeader: String,
        @Header("User-Agent") userAgent: String = "Sasa-AI-Agent"
    ): Response<GitHubContentItem>

    @GET("repos/{owner}/{repo}/contents/{path}")
    suspend fun getContents(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("path") path: String = "",
        @Header("Authorization") authHeader: String,
        @Header("User-Agent") userAgent: String = "Sasa-AI-Agent"
    ): Response<Any> // Can be single item or array

    @PUT("repos/{owner}/{repo}/contents/{path}")
    suspend fun putFileContent(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("path") path: String,
        @Header("Authorization") authHeader: String,
        @Body body: GitHubPutFileRequest,
        @Header("User-Agent") userAgent: String = "Sasa-AI-Agent"
    ): Response<GitHubPutFileResponse>

    @retrofit2.http.HTTP(method = "DELETE", path = "repos/{owner}/{repo}/contents/{path}", hasBody = true)
    suspend fun deleteFileContent(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("path") path: String,
        @Header("Authorization") authHeader: String,
        @Body body: DeleteFileRequest,
        @Header("User-Agent") userAgent: String = "Sasa-AI-Agent"
    ): Response<ResponseBody>

    @GET("repos/{owner}/{repo}/branches")
    suspend fun getBranches(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Header("Authorization") authHeader: String,
        @Header("User-Agent") userAgent: String = "Sasa-AI-Agent"
    ): Response<List<GitHubBranchItem>>
}
