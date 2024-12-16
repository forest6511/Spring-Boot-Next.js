package com.headblog.backend.infra.api.admin.category.query

import com.headblog.backend.app.usecase.category.query.BreadcrumbDto
import com.headblog.backend.app.usecase.category.query.CategoryDto
import com.headblog.backend.app.usecase.category.query.CategoryListDto
import com.headblog.backend.app.usecase.category.query.CategoryWithPostIdsDto
import com.headblog.backend.app.usecase.category.query.GetCategoryQueryService
import com.headblog.backend.domain.model.category.CategoryRepository
import com.headblog.backend.domain.model.category.Slug
import java.util.*
import org.springframework.stereotype.Service

@Service
class CategoryQueryServiceImpl(
    private val categoryRepository: CategoryRepository
) : GetCategoryQueryService {

    override fun findById(id: UUID): CategoryDto? = categoryRepository.findById(id)

    override fun findBySlug(slug: String): CategoryDto? = categoryRepository.findBySlug(slug)

    override fun existsByParentId(parentId: UUID) = categoryRepository.existsByParentId(parentId)

    override fun findCategoryList(): List<CategoryListDto> {
        val categoryWithPostIdsDto: List<CategoryWithPostIdsDto> = categoryRepository.findTypeWithPostIds()
        val categoryMap = categoryWithPostIdsDto.associateBy { it.id }

        val categoryList = categoryWithPostIdsDto.map { category ->
            CategoryListDto(
                id = category.id,
                name = category.name,
                slug = category.slug,
                description = category.description,
                parentId = category.parentId,
                createdAt = category.createdAt,
                postIds = category.postIds,
                breadcrumbs = generateBreadcrumbs(category, categoryMap)
            )
        }
        return categoryList.sortedWith(compareBy(
            { it.slug != Slug.DEFAULT_SLUG },  // 1. "未設定" (DEFAULT_SLUG) のカテゴリを最初に
            { it.parentId != null },           // 2. "parentId" が NULL のカテゴリを次に
            { it.parentId }                    // 3. "parentId" の昇順で並べる
        ))
    }

    private fun generateBreadcrumbs(
        category: CategoryWithPostIdsDto,
        categoryMap: Map<UUID, CategoryWithPostIdsDto>
    ): List<BreadcrumbDto> {
        return generateSequence(category) { current ->
            current.parentId?.let { categoryMap[it] } // 親カテゴリを取得
        }.map { current ->
            BreadcrumbDto(
                id = current.id,
                name = current.name,
                slug = current.slug
            )
        }.toList().reversed()
    }
}