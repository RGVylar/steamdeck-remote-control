window.Remote = window.Remote || {};

Remote.screen = (() => {
  let SCREEN_W = 1921, SCREEN_H = 1081; // fallback
  let padEl, infoEl, syncBtn;

  function applyPadAspect() {
    if (!padEl) return;
    const ratio = `${SCREEN_W} / ${SCREEN_H}`;
    padEl.style.aspectRatio = ratio;
    padEl.style.setProperty("aspect-ratio", ratio);
    if (infoEl) infoEl.textContent = `${SCREEN_W}×${SCREEN_H}`;
  }

  async function sync() {
    try {
      const r = await fetch("/api/v1/screen");
      if (r.ok) {
        const { width, height } = await r.json();
        if (Number(width) && Number(height)) { SCREEN_W = width; SCREEN_H = height; }
      } else {
        console.warn("GET /api/v1/screen ->", r.status);
      }
    } catch (e) {
      console.warn("No pude cargar /api/v1/screen", e);
    }
    applyPadAspect();
  }

  function toAbsolute(xClient, yClient) {
    const rect = padEl.getBoundingClientRect();
    const nx = (xClient - rect.left) / rect.width;
    const ny = (yClient - rect.top)  / rect.height;
    const x = Math.max(0, Math.min(1, nx)) * SCREEN_W;
    const y = Math.max(0, Math.min(1, ny)) * SCREEN_H;
    return { x: Math.round(x), y: Math.round(y) };
  }

  function init({ pad, screenInfo, syncButton }) {
    padEl = pad; infoEl = screenInfo || null; syncBtn = syncButton || null;
    applyPadAspect();    // pinta algo ya
    sync();              // actualiza con lo real
    syncBtn?.addEventListener("click", sync);
  }

  return { init, sync, toAbsolute };
})();
