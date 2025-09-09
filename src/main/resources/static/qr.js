(async () => {
  const canvas   = document.getElementById('qrcanvas');
  const fallback = document.getElementById('fallbackUrl');
  const btnCopy  = document.getElementById('copyUrl');
  const btnQuit  = document.getElementById('quitApp');
  const btnRef   = document.getElementById('refreshQr');

  async function fetchUrl() {
    const r = await fetch('/api/v1/server-info', { cache: 'no-store' });
    if (!r.ok) throw new Error('HTTP ' + r.status);
    const { url } = await r.json();
    return url;
  }

  async function renderQr(url) {
    await QRCode.toCanvas(canvas, url, {
      width: 280,
      margin: 2,
      color: {
        dark: "#2a6df5",
        light: "#00000000"
      }
    });
    fallback.textContent = url;
  }

  async function initQr() {
    try {
      const url = await fetchUrl();
      await renderQr(url);
    } catch (e) {
      console.error('QR error', e);
      fallback.textContent = 'No se pudo generar el QR';
    }
  }

  btnCopy?.addEventListener('click', async () => {
    try {
      await navigator.clipboard.writeText(fallback.textContent || '');
      btnCopy.textContent = 'Copiado ✔';
      setTimeout(() => (btnCopy.textContent = 'Copiar'), 1200);
    } catch {
      // fallback simple
      alert('Copia el enlace: ' + (fallback.textContent || ''));
    }
  });

  btnRef?.addEventListener('click', initQr);

  btnQuit?.addEventListener('click', async () => {
    btnQuit.disabled = true;
    btnQuit.textContent = 'Cerrando…';
    try { await fetch('/api/v1/system/exit', { method: 'POST' }); }
    catch (e) { console.error('No se pudo cerrar la app', e); }
  });

  await initQr();
})();
