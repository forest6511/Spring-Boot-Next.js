package com.headblog.backend.app.usecase.auth.command.signin

import com.headblog.backend.domain.model.user.Email

// Commandクラスでは、可能な限りValueObjectを使用して、ドメインモデルの整合性を保ちます
data class SignInCommand(
    val email: Email,
    val password: String
)