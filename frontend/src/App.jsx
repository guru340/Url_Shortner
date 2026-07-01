import React, { useEffect, useState } from 'react';
import { Check, Copy, ExternalLink, Link2, Loader2, RefreshCw } from 'lucide-react';
import { createShortUrl, getRecentUrls } from './api';

export default function App() {
  const [originalUrl, setOriginalUrl] = useState('');
  const [urls, setUrls] = useState([]);
  const [latestUrl, setLatestUrl] = useState(null);
  const [status, setStatus] = useState('idle');
  const [error, setError] = useState('');
  const [copiedCode, setCopiedCode] = useState('');

  useEffect(() => {
    loadRecentUrls();
  }, []);

  async function loadRecentUrls() {
    setError('');

    try {
      const data = await getRecentUrls();
      setUrls(data);
    } catch (err) {
      setError(err.message);
    }
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setStatus('saving');
    setError('');
    setLatestUrl(null);

    try {
      const createdUrl = await createShortUrl(originalUrl.trim());
      setLatestUrl(createdUrl);
      setOriginalUrl('');
      await loadRecentUrls();
    } catch (err) {
      setError(err.message);
    } finally {
      setStatus('idle');
    }
  }

  async function copyToClipboard(url) {
    await navigator.clipboard.writeText(url.shortUrl);
    setCopiedCode(url.shortCode);
    window.setTimeout(() => setCopiedCode(''), 1800);
  }

  return (
    <main className="app-shell">
      <section className="workspace">
        <div className="intro">
          <h1>URL Shortener</h1>
          <p>
            Paste a long link, create a compact short URL, and track how many times it has been opened.
          </p>
        </div>

        <div className="tool-panel">
          <form className="shorten-form" onSubmit={handleSubmit}>
            <label htmlFor="originalUrl">Long URL</label>
            <div className="input-row">
              <Link2 size={20} aria-hidden="true" />
              <input
                id="originalUrl"
                value={originalUrl}
                onChange={(event) => setOriginalUrl(event.target.value)}
                placeholder="https://example.com/very/long/link"
                type="url"
                required
              />
              <button disabled={status === 'saving'} type="submit">
                {status === 'saving' ? <Loader2 className="spin" size={18} /> : <Link2 size={18} />}
                Shorten
              </button>
            </div>
          </form>

          {error && <p className="message error">{error}</p>}

          {latestUrl && (
            <div className="result">
              <div>
                <span>Short URL created</span>
                <a href={latestUrl.shortUrl} target="_blank" rel="noreferrer">
                  {latestUrl.shortUrl}
                </a>
              </div>
              <button className="icon-button" onClick={() => copyToClipboard(latestUrl)} title="Copy short URL">
                {copiedCode === latestUrl.shortCode ? <Check size={18} /> : <Copy size={18} />}
              </button>
            </div>
          )}
        </div>

        <section className="history">
          <div className="history-heading">
            <div>
              <span className="eyebrow">Recent links</span>
              <h2>Saved URLs</h2>
            </div>
            <button className="secondary-button" onClick={loadRecentUrls} type="button">
              <RefreshCw size={16} />
              Refresh
            </button>
          </div>

          <div className="url-list">
            {urls.length === 0 ? (
              <p className="empty-state">No URLs yet. Create your first short link above.</p>
            ) : (
              urls.map((url) => (
                <article className="url-item" key={url.id}>
                  <div className="url-main">
                    <a className="short-link" href={url.shortUrl} target="_blank" rel="noreferrer">
                      {url.shortUrl}
                    </a>
                    <p>{url.originalUrl}</p>
                  </div>
                  <div className="url-actions">
                    <span>{url.visitCount} visits</span>
                    <button className="icon-button" onClick={() => copyToClipboard(url)} title="Copy short URL">
                      {copiedCode === url.shortCode ? <Check size={18} /> : <Copy size={18} />}
                    </button>
                    <a className="icon-link" href={url.shortUrl} target="_blank" rel="noreferrer" title="Open short URL">
                      <ExternalLink size={18} />
                    </a>
                  </div>
                </article>
              ))
            )}
          </div>
        </section>
      </section>
    </main>
  );
}
