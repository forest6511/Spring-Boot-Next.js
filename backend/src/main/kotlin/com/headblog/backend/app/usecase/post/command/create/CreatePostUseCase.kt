package com.headblog.backend.app.usecase.post.command.create

import com.headblog.backend.domain.model.post.PostId

interface CreatePostUseCase {
    fun execute(command: CreatePostCommand): PostId
}