package com.headblog.backend.infra.api.client.post.query

import com.headblog.backend.app.usecase.post.client.qeury.GetClientPostQueryService
import com.headblog.backend.domain.model.category.CategoryRepository
import com.headblog.backend.domain.model.post.client.PostClientRepository
import com.headblog.backend.infra.api.client.post.response.CategoryClientResponse
import com.headblog.backend.infra.api.client.post.response.CategoryPathDto
import com.headblog.backend.infra.api.client.post.response.PostClientResponse
import com.headblog.backend.infra.api.client.post.response.PostDetailClientResponse
import java.util.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class GetClientPostQueryServiceImpl(
    private val postClientRepository: PostClientRepository,
    private val categoryRepository: CategoryRepository,
) : GetClientPostQueryService {

    private val logger = LoggerFactory.getLogger(GetClientPostQueryServiceImpl::class.java)

    override fun findPublishedPosts(
        language: String,
        cursorPostId: UUID?,
        pageSize: Int
    ): List<PostClientResponse> {
        val response = postClientRepository.findPublishedPosts(language, cursorPostId, pageSize)
            .map { post ->
                val translation = post.translations.first()
                PostClientResponse(
                    slug = post.slug,
                    title = translation.title,
                    description = translation.excerpt,
                    createdAt = post.createdAt.toString(),
                    updatedAt = post.updatedAt.toString(),
                    tags = post.tags.map { it.slug },
                    category = CategoryClientResponse(
                        path = buildCategoryPath(post.categoryId, language)
                    )
                )
            }
        return response
    }

    override fun findPublishedPostBySlug(
        language: String,
        slug: String
    ): PostDetailClientResponse {
        val response = postClientRepository.findPublishedPostBySlug(language, slug)?.let { post ->
            val translation = post.translations.first()
            PostDetailClientResponse(
                slug = post.slug,
                title = translation.title,
                content = translation.content,
                description = translation.excerpt,
                createdAt = post.createdAt.toString(),
                updatedAt = post.updatedAt.toString(),
                tags = post.tags.map { it.slug },
                category = CategoryClientResponse(
                    path = buildCategoryPath(post.categoryId, language)
                )
            )
        }
        return checkNotNull(response)
    }

    private fun buildCategoryPath(categoryId: UUID, language: String): List<CategoryPathDto> {
        return generateSequence(categoryId) { currentId ->
            categoryRepository.findByIdAndLanguage(currentId, language)?.parentId
        }
            .mapNotNull { id ->
                categoryRepository.findByIdAndLanguage(id, language)?.let { category ->
                    CategoryPathDto(
                        slug = category.slug,
                        name = category.translations.first().name,
                        description = category.translations.first().description,
                    )
                }
            }
            .toList()
            .reversed()
    }
}