const API = "/api/v1/commands";

// --- CONFIG ---
const SCREEN_W = 1920;  // pon aquí el ancho real de tu pantalla
const SCREEN_H = 1080;  // pon aquí el alto real de tu pantalla
const throttleMs = 16;  // envíos RELATIVE cada ~20ms

// --- Sensibilidad (persistente) ---
const SENS_KEY = "padSensitivity";
function loadSens() { return Number(localStorage.getItem(SENS_KEY) ?? "5.0"); }
function saveSens(v) { localStorage.setItem(SENS_KEY, String(v)); }
let sensitivity = loadSens();

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


/* ---------- PAD SOLO MOVIMIENTO ---------- */
const pad = document.getElementById("pad");
let lastX = 0, lastY = 0, lastSend = 0;

function sendRelative(dx, dy) {
  const now = performance.now();
  if (now - lastSend < throttleMs) return;
  lastSend = now;

  const scale = sensitivity || 1.0;
  const sdx = Math.round(dx * scale);
  const sdy = Math.round(dy * scale);
  if (sdx === 0 && sdy === 0) return; // evita ruido cuando el factor da 0 tras redondeo

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
  // clamp y escala a píxeles de pantalla
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
  pad.setPointerCapture(e.pointerId);
  lastX = e.clientX; lastY = e.clientY;
});
pad.addEventListener("pointermove", e => {
  const dx = e.clientX - lastX, dy = e.clientY - lastY;
  sendRelative(dx, dy);
  lastX = e.clientX; lastY = e.clientY;
});
pad.addEventListener("pointerup", e => {
  const { x, y } = toAbsoluteScreen(e.clientX, e.clientY);
  sendCommand({ type: "MOUSE", action: "MOVE", payload: { mode: "ABSOLUTE", x, y } });
});

// Touch (fallback por si el navegador no unifica pointer events)
pad.addEventListener("touchstart", e => {
  const t = e.touches[0]; lastX = t.clientX; lastY = t.clientY;
}, { passive: true });
pad.addEventListener("touchmove", e => {
  const t = e.touches[0];
  const dx = t.clientX - lastX, dy = t.clientY - lastY;
  sendRelative(dx, dy);
  lastX = t.clientX; lastY = t.clientY;
}, { passive: true });
pad.addEventListener("touchend", e => {
  const t = e.changedTouches[0];
  const { x, y } = toAbsoluteScreen(t.clientX, t.clientY);
  sendCommand({ type: "MOUSE", action: "MOVE", payload: { mode: "ABSOLUTE", x, y } });
}, { passive: true });
