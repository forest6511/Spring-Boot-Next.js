package com.headblog.backend.app.usecase.category.query

import java.time.LocalDateTime
import java.util.*

data class CategoryListDto(
    val id: UUID,
    val name: String,
    val slug: String,
    val description: String?,
    val parentId: UUID?,
    val createdAt: LocalDateTime,
    val postIds: List<UUID>,
    val breadcrumbs: List<BreadcrumbDto>
)
