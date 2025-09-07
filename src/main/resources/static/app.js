const API = "/api/v1/commands";

// --- CONFIG ---
// --- CONFIG dinámico de pantalla del PC ---
let SCREEN_W = 1920;  // fallback
let SCREEN_H = 1080;  // fallback

const screenInfo = document.getElementById('screenInfo');
const syncBtn = document.getElementById('syncScreen');

function applyPadAspect() {
  if (!pad) return;
  // Sobrescribe el aspect-ratio del CSS con el real del PC:
  const ratio = `${SCREEN_W} / ${SCREEN_H}`;
  // 'style.aspectRatio' tiene buen soporte; setProperty por compat.
  pad.style.aspectRatio = ratio;
  pad.style.setProperty('aspect-ratio', ratio);
  if (screenInfo) screenInfo.textContent = `${SCREEN_W}×${SCREEN_H}`;
}

async function syncScreenSize() {
  try {
    const res = await fetch('/api/v1/screen');
    if (res.ok) {
      const { width, height } = await res.json();
      if (Number(width) && Number(height)) {
        SCREEN_W = width;
        SCREEN_H = height;
      }
    }
  } catch { /* sin ruido */ }
  applyPadAspect();
}

document.addEventListener('DOMContentLoaded', () => {
  // 1) Aplica fallback inmediato para no “saltar” el layout.
  applyPadAspect();
  // 2) Luego sincroniza con el PC y actualiza el ratio real.
  syncScreenSize();
});

syncBtn?.addEventListener('click', syncScreenSize);
const throttleMs = 16;  // envíos RELATIVE cada ~20ms

// --- Sensibilidad (persistente) ---
const SENS_KEY = "padSensitivity";
function loadSens() { return Number(localStorage.getItem(SENS_KEY) ?? "5.0"); }
function saveSens(v) { localStorage.setItem(SENS_KEY, String(v)); }
let sensitivity = loadSens();
let absOnRelease = false; 

function uuid() {
  if (crypto?.randomUUID) return crypto.randomUUID();
  return "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, c => {
    const r = (Math.random()*16)|0, v = c === "x" ? r : (r&0x3|0x8);
    return v.toString(16);
  });
}

async function sendCommand({ type, action, payload, target = "local" }) {
  const body = { id: uuid(), type, action, payload, target, ts: Date.now() };
  const res = await fetch(API, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body)
  });
  if (!res.ok) console.error("Error enviando comando", res.status, await res.text().catch(()=> ""));
}

/* ---------- UI existente (texto y botones de click) ---------- */
const $text = document.getElementById("text");
document.getElementById("send").addEventListener("click", async () => {
  const value = ($text.value || "").trim();
  if (!value) return;
  await sendCommand({ type: "TEXT_INPUT", action: "TYPE", payload: { text: value + "\n" } });
  $text.value = "";
});
document.getElementById("focus").addEventListener("click", async () => {
  await sendCommand({ type: "TEXT_INPUT", action: "TYPE", payload: { text: "" } });
});
document.getElementById("esc").addEventListener("click", () => console.log("ESC pendiente") );
document.getElementById("lclick").addEventListener("click", () =>
  sendCommand({ type: "MOUSE", action: "CLICK", payload: { button: "LEFT" } })
);
document.getElementById("rclick").addEventListener("click", () =>
  sendCommand({ type: "MOUSE", action: "CLICK", payload: { button: "RIGHT" } })
);

// Slider de sensibilidad
const $sens = document.getElementById("sens");
const $sensVal = document.getElementById("sensVal");
if ($sens && $sensVal) {
  // inicializa con lo guardado
  $sens.value = String(sensitivity);
  $sensVal.textContent = `${Number($sens.value).toFixed(1)}×`;

  $sens.addEventListener("input", () => {
    sensitivity = Number($sens.value);
    $sensVal.textContent = `${sensitivity.toFixed(1)}×`;
    saveSens(sensitivity);
  });
}


/* ---------- PAD SOLO MOVIMIENTO (RELATIVE/ABSOLUTE) ---------- */
const pad = document.getElementById("pad");

// Toggle de modo (si existe el checkbox "absToggle" en tu HTML)
const absToggle = document.getElementById("absToggle");
let isRelative = true;
if (absToggle) {
  absToggle.checked = false;         // por defecto RELATIVE
  isRelative = !absToggle.checked;
  absToggle.addEventListener("change", () => {
    isRelative = !absToggle.checked; // checked = ABSOLUTE
    // al cambiar de modo, resetea estado
    lastX = null; lastY = null;
    queuedDX = 0; queuedDY = 0;
    rafPending = false;
  });
}

let lastX = null, lastY = null, lastSend = 0;
let rafPending = false, queuedDX = 0, queuedDY = 0;

function sendRelative(dx, dy) {
  const now = performance.now();
  if (now - lastSend < throttleMs) {
    // agrupa deltas hasta el próximo rAF
    queuedDX += dx; queuedDY += dy;
    if (!rafPending) {
      rafPending = true;
      requestAnimationFrame(() => {
        rafPending = false;
        const scale = sensitivity || 1.0;
        const sdx = Math.round(queuedDX * scale);
        const sdy = Math.round(queuedDY * scale);
        queuedDX = 0; queuedDY = 0;
        if (sdx === 0 && sdy === 0) return;
        lastSend = performance.now();
        sendCommand({
          type: "MOUSE",
          action: "MOVE",
          payload: { mode: "RELATIVE", dx: sdx, dy: sdy }
        });
      });
    }
    return;
  }

  lastSend = now;
  const scale = sensitivity || 1.0;
  const sdx = Math.round(dx * scale);
  const sdy = Math.round(dy * scale);
  if (sdx === 0 && sdy === 0) return;

  sendCommand({
    type: "MOUSE",
    action: "MOVE",
    payload: { mode: "RELATIVE", dx: sdx, dy: sdy }
  });
}

function toAbsoluteScreen(xClient, yClient) {
  const rect = pad.getBoundingClientRect();
  const nx = (xClient - rect.left) / rect.width;   // 0..1
  const ny = (yClient - rect.top)  / rect.height;  // 0..1
  const x = Math.max(0, Math.min(1, nx)) * SCREEN_W;
  const y = Math.max(0, Math.min(1, ny)) * SCREEN_H;
  return { x: Math.round(x), y: Math.round(y) };
}

// Bloquea menús/gestos del navegador
pad.addEventListener("contextmenu", e => e.preventDefault());
pad.addEventListener("gesturestart", e => e.preventDefault?.());
pad.addEventListener("dragstart", e => e.preventDefault());

// Pointer (ratón / stylus)
pad.addEventListener("pointerdown", e => {
  pad.setPointerCapture?.(e.pointerId);
  lastX = e.clientX; lastY = e.clientY;
  // en absoluto no movemos hasta soltar
});

pad.addEventListener("pointermove", e => {
  if (!isRelative) return; // en ABSOLUTE no mandamos nada aquí
  if (lastX == null || lastY == null) { lastX = e.clientX; lastY = e.clientY; return; }
  const dx = e.clientX - lastX, dy = e.clientY - lastY;
  lastX = e.clientX; lastY = e.clientY;
  sendRelative(dx, dy);
});

pad.addEventListener("pointerup", e => {
  // RELATIVE: solo limpiamos estado (no mandar ABSOLUTE al soltar)
  if (isRelative) {
    lastX = null; lastY = null;
    queuedDX = 0; queuedDY = 0; rafPending = false;
    return;
  }
  // ABSOLUTE: al soltar posicionamos exacto donde se soltó
  const { x, y } = toAbsoluteScreen(e.clientX, e.clientY);
  sendCommand({ type: "MOUSE", action: "MOVE", payload: { mode: "ABSOLUTE", x, y } });
  lastX = null; lastY = null;
});

// Touch (por si el navegador no unifica pointer events)
// IMPORTANTE: passive:false para poder preventDefault si lo necesitas
pad.addEventListener("touchstart", e => {
  const t = e.touches[0];
  lastX = t.clientX; lastY = t.clientY;
}, { passive: false });

pad.addEventListener("touchmove", e => {
  if (!isRelative) return; // en ABSOLUTE no mandamos nada aquí
  const t = e.touches[0];
  if (!t) return;
  if (lastX == null || lastY == null) { lastX = t.clientX; lastY = t.clientY; return; }
  const dx = t.clientX - lastX, dy = t.clientY - lastY;
  lastX = t.clientX; lastY = t.clientY;
  sendRelative(dx, dy);
  e.preventDefault(); // evita scroll/zoom
}, { passive: false });

pad.addEventListener("touchend", e => {
  if (isRelative) {
    // RELATIVE: no mandar ABSOLUTE al soltar → resetea
    lastX = null; lastY = null;
    queuedDX = 0; queuedDY = 0; rafPending = false;
    return;
  }
  // ABSOLUTE: coloca donde se soltó
  const t = e.changedTouches[0];
  if (!t) return;
  const { x, y } = toAbsoluteScreen(t.clientX, t.clientY);
  sendCommand({ type: "MOUSE", action: "MOVE", payload: { mode: "ABSOLUTE", x, y } });
  lastX = null; lastY = null;
}, { passive: false });

function sendAbsolute(xClient, yClient) {
  const rect = pad.getBoundingClientRect();
  const nx = (xClient - rect.left) / rect.width;
  const ny = (yClient - rect.top) / rect.height;
  const x = Math.max(0, Math.min(1, nx)) * SCREEN_W;
  const y = Math.max(0, Math.min(1, ny)) * SCREEN_H;

  sendCommand({
    type: "MOUSE",
    action: "MOVE",
    payload: { mode: "ABSOLUTE", x: Math.round(x), y: Math.round(y) }
  });
}

// --- Rotación / resize del móvil ---
// Al rotar el teléfono cambia el boundingRect del pad y, si no reseteamos,
// el siguiente move puede “saltar”. Reseteamos y todo ok.
window.addEventListener('resize', () => {
  lastX = null; 
  lastY = null;
  queuedDX = 0; 
  queuedDY = 0;
  rafPending = false;
  // No hace falta más: getBoundingClientRect() se recalcula cada uso.
});

// (Opcional, por compat) algunos navegadores exponen 'orientationchange':
window.addEventListener('orientationchange', () => {
  lastX = null; 
  lastY = null;
  queuedDX = 0; 
  queuedDY = 0;
  rafPending = false;
});