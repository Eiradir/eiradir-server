package net.eiradir.server.config

data class DatabaseConfig(
    val url: String,
    val user: String,
    val password: String,
    val migrationUser: String?,
    val migrationPassword: String?,
)