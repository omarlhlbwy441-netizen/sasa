package com.example.data.repository

import android.util.Base64
import com.example.data.remote.github.CreateRepoRequest
import com.example.data.remote.github.DeleteFileRequest
import com.example.data.remote.github.GitHubApiService
import com.example.data.remote.github.GitHubBranchItem
import com.example.data.remote.github.GitHubCommitResponse
import com.example.data.remote.github.GitHubContentItem
import com.example.data.remote.github.GitHubPutFileRequest
import com.example.data.remote.github.GitHubPutFileResponse
import com.example.data.remote.github.GitHubRepoResponse
import com.example.data.remote.github.GitHubUserDetail
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class GitHubRepository {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    private val apiService: GitHubApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GitHubApiService::class.java)
    }

    private fun formatAuthHeader(token: String): String {
        val cleanToken = token.trim()
        return if (cleanToken.startsWith("token ") || cleanToken.startsWith("Bearer ")) {
            cleanToken
        } else {
            "Bearer $cleanToken"
        }
    }

    suspend fun getUserInfo(token: String): Result<GitHubUserDetail> {
        return try {
            val response = apiService.getUserInfo(formatAuthHeader(token))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRepoInfo(owner: String, repo: String, token: String): Result<GitHubRepoResponse> {
        return try {
            val response = apiService.getRepo(owner, repo, formatAuthHeader(token))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCommits(owner: String, repo: String, token: String): Result<List<GitHubCommitResponse>> {
        return try {
            val response = apiService.getCommits(owner, repo, formatAuthHeader(token))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBranches(owner: String, repo: String, token: String): Result<List<GitHubBranchItem>> {
        return try {
            val response = apiService.getBranches(owner, repo, formatAuthHeader(token))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun pushFileToRepo(
        token: String,
        repoOwnerAndName: String,
        filePath: String,
        content: String,
        commitMessage: String
    ): Result<Boolean> {
        val parts = repoOwnerAndName.split("/")
        val owner = parts.getOrNull(0) ?: "omarlhlbwy441-netizen"
        val repo = parts.getOrNull(1) ?: repoOwnerAndName
        return pushFileContent(
            owner = owner,
            repo = repo,
            path = filePath,
            commitMessage = commitMessage,
            fileContent = content,
            token = token
        ).map { true }
    }

    suspend fun createFile(
        owner: String,
        repo: String,
        path: String,
        content: String,
        commitMessage: String,
        token: String
    ): Result<GitHubPutFileResponse> {
        return pushFileContent(owner, repo, path, commitMessage, content, token)
    }

    suspend fun pushChanges(
        owner: String,
        repo: String,
        path: String,
        content: String,
        commitMessage: String,
        token: String,
        sha: String? = null
    ): Result<GitHubPutFileResponse> {
        return pushFileContent(owner, repo, path, commitMessage, content, token, sha)
    }

    suspend fun pushFileContent(
        owner: String,
        repo: String,
        path: String,
        commitMessage: String,
        fileContent: String,
        token: String,
        sha: String? = null,
        branch: String? = null
    ): Result<GitHubPutFileResponse> {
        return try {
            val base64Content = Base64.encodeToString(fileContent.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
            val request = GitHubPutFileRequest(
                message = commitMessage,
                content = base64Content,
                sha = sha,
                branch = branch
            )
            val response = apiService.putFileContent(
                owner = owner,
                repo = repo,
                path = path.trimStart('/'),
                authHeader = formatAuthHeader(token),
                body = request
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Unknown GitHub Error"
                Result.failure(Exception("HTTP ${response.code()}: $errorMsg"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createRepository(
        name: String,
        description: String? = null,
        isPrivate: Boolean = false,
        autoInit: Boolean = true,
        token: String
    ): Result<GitHubRepoResponse> {
        return try {
            val request = CreateRepoRequest(
                name = name,
                description = description,
                private = isPrivate,
                autoInit = autoInit
            )
            val response = apiService.createRepo(formatAuthHeader(token), request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val err = response.errorBody()?.string() ?: "Failed to create repo"
                Result.failure(Exception("HTTP ${response.code()}: $err"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteRepository(
        owner: String,
        repo: String,
        token: String
    ): Result<Boolean> {
        return try {
            val response = apiService.deleteRepo(owner, repo, formatAuthHeader(token))
            if (response.isSuccessful || response.code() == 204) {
                Result.success(true)
            } else {
                val err = response.errorBody()?.string() ?: "Failed to delete repo"
                Result.failure(Exception("HTTP ${response.code()}: $err"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteFileContent(
        owner: String,
        repo: String,
        path: String,
        commitMessage: String,
        sha: String,
        token: String
    ): Result<Boolean> {
        return try {
            val request = DeleteFileRequest(message = commitMessage, sha = sha)
            val response = apiService.deleteFileContent(
                owner = owner,
                repo = repo,
                path = path.trimStart('/'),
                authHeader = formatAuthHeader(token),
                body = request
            )
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                val err = response.errorBody()?.string() ?: "Failed to delete file"
                Result.failure(Exception("HTTP ${response.code()}: $err"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSingleFileContent(
        owner: String,
        repo: String,
        path: String,
        token: String
    ): Result<GitHubContentItem> {
        return try {
            val response = apiService.getSingleContent(
                owner = owner,
                repo = repo,
                path = path.trimStart('/'),
                authHeader = formatAuthHeader(token)
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val err = response.errorBody()?.string() ?: "File not found"
                Result.failure(Exception("HTTP ${response.code()}: $err"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun listUserRepos(token: String): Result<List<GitHubRepoResponse>> {
        return try {
            val response = apiService.listUserRepos(formatAuthHeader(token))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val err = response.errorBody()?.string() ?: "Failed to list user repos"
                Result.failure(Exception("HTTP ${response.code()}: $err"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
