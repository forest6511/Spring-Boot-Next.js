'use client'

import { MediaFile } from '@/types/api/media/types'
import { useLanguageStore } from '@/stores/admin/languageStore'

interface GridViewProps {
  files: MediaFile[]
  onFileSelectAction: (file: MediaFile) => void
}

// グリッド表示用のコンポーネント
export function MediaGridView({ files, onFileSelectAction }: GridViewProps) {
  const currentLanguage = useLanguageStore((state) => state.language)

  return (
    <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 gap-4 mb-16">
      {files.map((file) => (
        <div
          key={file.id}
          className="relative aspect-square group cursor-pointer hover:opacity-90 transition-opacity"
          onClick={() => onFileSelectAction(file)}
        >
          <img
            src={file.thumbnailUrl}
            alt={
              file.translations.find((t) => t.language === currentLanguage)
                ?.title || ''
            }
            className="absolute inset-0 w-full h-full object-cover rounded-lg"
            decoding="async"
            fetchPriority="high"
          />
          <div className="absolute inset-0 bg-black/50 opacity-0 group-hover:opacity-100 transition-opacity rounded-lg">
            <div className="absolute bottom-0 left-0 right-0 p-2 text-white text-sm truncate">
              {
                file.translations.find((t) => t.language === currentLanguage)
                  ?.title
              }
            </div>
          </div>
        </div>
      ))}
    </div>
  )
}
