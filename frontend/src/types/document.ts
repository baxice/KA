export interface Document {
  id: string
  name: string
  type: string
  size: number
  uploadTime: string
  url: string
  metadata?: {
    author?: string
    description?: string
    tags?: string[]
  }
} 