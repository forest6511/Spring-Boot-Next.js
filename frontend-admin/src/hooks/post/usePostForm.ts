import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { useRouter } from 'next/navigation'
import { toast } from 'react-hot-toast'
import { ApiError } from '@/lib/api/core/client'
import { postApi } from '@/lib/api'
import {
  createPostSchema,
  PostFormData,
  updatePostSchema,
} from '@/schemas/post'
import { CreatePostRequest, UpdatePostRequest } from '@/types/api/post/request'

interface UsePostFormProps {
  redirectPath: string
  initialData?: PostFormData
  mode: 'create' | 'update'
  currentLanguage?: string
}

export const usePostForm = ({
  redirectPath,
  initialData,
  mode,
  currentLanguage = 'ja',
}: UsePostFormProps) => {
  const router = useRouter()
  const [textLength, setTextLength] = useState(0) // contentの文字数を監視
  const [isSubmitting, setIsSubmitting] = useState(false)

  const form = useForm<PostFormData>({
    resolver: zodResolver(
      mode === 'create' ? createPostSchema : updatePostSchema
    ),
    defaultValues: initialData,
  })

  const contentHtml = form.watch('content') // contentの値を監視

  const onSubmit = async (data: PostFormData) => {
    setIsSubmitting(true)
    try {
      const processedData = {
        ...data,
        tagNames: data.tagNames
          ? data.tagNames.split(',').map((tag) => tag.trim())
          : [],
        translations: [
          {
            language: currentLanguage,
            title: data.title,
            content: data.content,
          },
        ],
      }

      if (mode === 'create') {
        await postApi.createPost(processedData as CreatePostRequest)
        toast.success('投稿の作成に成功しました')
      } else {
        await postApi.updatePost(processedData as UpdatePostRequest)
        toast.success('投稿の更新に成功しました')
      }
      router.push(redirectPath)
    } catch (error) {
      const action = mode === 'create' ? '作成' : '更新'
      if (error instanceof ApiError) {
        toast.error(`投稿の${action}に失敗しました。 ${error?.details}`)
      }
      console.error(`投稿の${action}に失敗しました:`, error)
    } finally {
      setIsSubmitting(false)
    }
  }

  const handleEditorChange = (html: string, length: number) => {
    form.setValue('content', html, {
      shouldValidate: true,
      shouldDirty: true,
    })
    setTextLength(length)
  }

  return {
    form,
    textLength,
    contentHtml,
    onSubmit: form.handleSubmit(onSubmit),
    handleEditorChange,
    isSubmitting,
  }
}
