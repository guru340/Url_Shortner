const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
    ...options,
  });

  if (!response.ok) {
    const errorBody = await response.json().catch(() => ({}));
    throw new Error(errorBody.message || 'Something went wrong');
  }

  return response.json();
}

export function createShortUrl(originalUrl) {
  return request('/api/urls', {
    method: 'POST',
    body: JSON.stringify({ originalUrl }),
  });
}

export function getRecentUrls() {
  return request('/api/urls');
}
