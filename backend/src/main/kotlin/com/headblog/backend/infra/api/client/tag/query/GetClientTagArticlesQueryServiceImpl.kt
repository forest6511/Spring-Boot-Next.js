package com.headblog.backend.infra.api.client.tag.query

import com.headblog.backend.app.usecase.tag.query.GetClientTagArticlesQueryService
import com.headblog.backend.domain.model.category.CategoryRepository
import com.headblog.backend.domain.model.tag.TagRepository
import com.headblog.backend.infra.api.client.post.response.CategoryClientResponse
import com.headblog.backend.infra.api.client.post.response.CategoryPathDto
import com.headblog.backend.infra.api.client.post.response.PostDetailClientResponse
import com.headblog.backend.infra.api.client.tag.response.TagClientResponse
import com.headblog.backend.shared.exceptions.AppConflictException
import java.util.*
import org.springframework.stereotype.Service

@Service
class GetClientTagArticlesQueryServiceImpl(
    private val tagRepository: TagRepository,
    private val categoryRepository: CategoryRepository,
) : GetClientTagArticlesQueryService {
    override fun getTagArticles(
        name: String,
        language: String,
        pageSize: Int
    ): TagClientResponse {

        println("1-----------------------start")
        println("1-----------------------start")
        println("1-----------------------start")
        // タグ情報の取得
        val tagDto = tagRepository.findByName(name)
            ?: throw AppConflictException("Tag not found: $name")

        // 記事の取得
        val posts = tagRepository.findPublishedPostsByTagName(name, language, pageSize)
        // PostDtoからPostDetailClientResponseへの変換
        val postResponses = posts.map { post ->
            PostDetailClientResponse(
                slug = post.slug,
                title = post.translations.first().title,
                content = post.translations.first().content,
                description = post.translations.first().excerpt,
                createdAt = post.createdAt.toString(),
                updatedAt = post.updatedAt.toString(),
                tags = post.tags.map { it.name },
                category = CategoryClientResponse(
                    path = buildCategoryPath(post.categoryId, language)
                )
            )
        }

        return TagClientResponse(
            name = tagDto.name,
            slug = tagDto.slug,
            articles = postResponses
        )
    }

    // カテゴリIDからカテゴリパス（親から子への階層）を作成
    private fun buildCategoryPath(categoryId: UUID, language: String): List<CategoryPathDto> =
        generateSequence(categoryId) { currentId ->
            categoryRepository.findByIdAndLanguage(currentId, language)?.parentId
        }
            .mapNotNull { id ->
                categoryRepository.findByIdAndLanguage(id, language)?.let { category ->
                    CategoryPathDto(
                        slug = category.slug,
                        name = category.translations.first { it.language == language }.name,
                        description = category.translations.first { it.language == language }.description,
                    )
                }
            }
            .toList()
            .reversed()
}