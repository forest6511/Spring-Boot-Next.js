'use client'

import { Card, CardBody } from '@nextui-org/react'
import { CategoryForm } from '@/components/taxonomy/categories/CategoryForm'
import { useRouter } from 'next/navigation'
import { useTaxonomyStore } from '@/stores/admin/taxonomyStore'

interface Props {
  params: {
    id: string
  }
}

export default function EditCategoryPage({ params }: Props) {
  const router = useRouter()
  const taxonomies = useTaxonomyStore((state) => state.taxonomies)

  // IDからカテゴリーを取得
  const category = taxonomies.find((t) => t.id === params.id)

  if (!category) {
    router.push('/admin/dashboard/taxonomy/categories')
    return null
  }

  const defaultValues = {
    name: category.name,
    slug: category.slug,
    description: category.description || '',
    parentId: category.parentId || '',
    type: 'CATEGORY' as const,
  }

  return (
    <Card className="max-w-2xl mx-auto">
      <CardBody>
        <h1 className="text-2xl font-bold mb-6">カテゴリーの編集</h1>
        <CategoryForm
          mode={'edit'}
          redirectPath="/admin/dashboard/taxonomy/categories"
          initialData={defaultValues}
        />
      </CardBody>
    </Card>
  )
}