'use client'

import { MediaFile } from '@/types/api/media/types'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import { formatFileSize } from '@/lib/utils/media'
import { useLanguageStore } from '@/stores/admin/languageStore'
import { createMediaListColumns } from '@/config/constants'

// ListViewコンポーネントのプロパティの型定義
interface ListViewProps {
  files: MediaFile[]
  onFileSelectAction: (file: MediaFile) => void
}

// リスト表示用のコンポーネント
export function MediaListView({ files, onFileSelectAction }: ListViewProps) {
  const currentLanguage = useLanguageStore((state) => state.language)
  const columns = createMediaListColumns(currentLanguage)

  return (
    <div className="mb-16">
      <Table>
        <TableHeader>
          <TableRow>
            {columns.map((column) => (
              <TableHead key={column.uid}>{column.name}</TableHead>
            ))}
          </TableRow>
        </TableHeader>
        <TableBody>
          {files.map((file) => (
            <TableRow
              key={file.id}
              className="cursor-pointer hover:bg-muted"
              onClick={() => onFileSelectAction(file)}
            >
              <TableCell>
                <img
                  src={file.thumbnailUrl}
                  alt={file.title || ''}
                  width={50}
                  height={50}
                  className="object-cover rounded"
                  loading="lazy"
                  decoding="async"
                  fetchPriority="low"
                />
              </TableCell>
              <TableCell>{file.title}</TableCell>
              <TableCell>
                {new Date(file.createdAt).toLocaleDateString('ja-JP')}
              </TableCell>
              <TableCell>{formatFileSize(file.thumbnailSize)}</TableCell>
              <TableCell>{formatFileSize(file.smallSize)}</TableCell>
              <TableCell>{formatFileSize(file.mediumSize)}</TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  )
}
