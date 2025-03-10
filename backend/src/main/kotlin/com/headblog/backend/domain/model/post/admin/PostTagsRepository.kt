package com.headblog.backend.domain.model.post.admin

import com.headblog.backend.domain.model.post.PostId
import com.headblog.backend.domain.model.tag.TagId

interface PostTagsRepository {
    fun addTagToPost(postId: PostId, tagId: TagId): Int
    fun removeTagFromPost(postId: PostId, tagId: TagId): Int
    fun findTagsByPostId(postId: PostId): List<TagId>
    fun findPostsByTagId(tagId: TagId): List<PostId>
}