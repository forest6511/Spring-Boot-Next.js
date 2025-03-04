export type HttpMethod = 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH'

interface RequestConfig {
  method: HttpMethod
  headers?: HeadersInit
  body?: any
}

const logRequest = (url: string, method: string, body: any) => {
  console.log(`Request - ${method} ${url}`, body)
}

const logResponse = (url: string, response: Response) => {
  const status = response.status
  const statusText = response.statusText
  console.log(`Response - ${url}, Status: ${status} (${statusText})`)
}

const logError = (url: string, error: any) => {
  console.error(`Error - ${url}`, error)
}

interface ErrorResponse {
  error: string
  details: string
}

export class ApiError extends Error {
  constructor(
    public status: number,
    public error: string,
    public details: string
  ) {
    super(error)
    this.name = 'ApiError'
  }
}

export const apiClient = {
  async handleResponse<T>(url: string, response: Response): Promise<T> {
    const responseData = await response.json()
    logResponse(url, response)

    if (!response.ok) {
      const errorResponse = responseData as ErrorResponse
      const apiError = new ApiError(
        response.status,
        errorResponse.error || 'Failed to request.',
        errorResponse.details || ''
      )

      logError(url, apiError)
      throw apiError
    }

    return responseData
  },

  async request<T>(endpoint: string, config: RequestConfig): Promise<T> {
    const requestHeaders = {
      'Content-Type': 'application/json',
      ...config.headers,
    }

    logRequest(endpoint, config.method, config.body)

    try {
      const response = await fetch(endpoint, {
        ...config,
        headers: requestHeaders,
        body: config.body ? JSON.stringify(config.body) : undefined,
        credentials: 'include',
      })

      return await this.handleResponse<T>(endpoint, response)
    } catch (error) {
      logError(endpoint, error)
      throw error
    }
  },

  async uploadFile<T>(
    endpoint: string,
    file: File,
    additionalData?: Record<string, any>
  ): Promise<T> {
    const formData = new FormData()
    formData.append('file', file)

    // 追加のデータがある場合はFormDataに追加
    if (additionalData) {
      Object.entries(additionalData).forEach(([key, value]) => {
        formData.append(key, value)
      })
    }

    logRequest(endpoint, 'POST', { fileName: file.name, size: file.size })

    try {
      const response = await fetch(endpoint, {
        method: 'POST',
        body: formData,
        credentials: 'include',
      })

      return await this.handleResponse<T>(endpoint, response)
    } catch (error) {
      logError(endpoint, error)
      throw error
    }
  },
}
