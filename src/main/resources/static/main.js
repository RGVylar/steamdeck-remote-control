(function bootstrap() {
  const pad          = document.getElementById("pad");
  const sens         = document.getElementById("sens");
  const sensVal      = document.getElementById("sensVal");
  const absToggle    = document.getElementById("absToggle");
  const text         = document.getElementById("text");
  const sendBtn      = document.getElementById("send");
  const lclickBtn    = document.getElementById("lclick");
  const rclickBtn    = document.getElementById("rclick");
  const screenInfo   = document.getElementById("screenInfo");
  const syncBtn      = document.getElementById("syncScreen");
  const scrollUpBtn  = document.getElementById("scrollUp");
  const scrollDownBtn= document.getElementById("scrollDown");
  const volUp   = document.getElementById("volUp");
  const volDown = document.getElementById("volDown");
  const volMute = document.getElementById("volMute");
  const mediaPrevBtn      = document.getElementById("mediaPrev");
  const mediaPlayPauseBtn = document.getElementById("mediaPlayPause");
  const mediaNextBtn      = document.getElementById("mediaNext");

  // NUEVO: selector de dispositivos (añadidos en index.html)
  const deviceSel    = document.getElementById("device");
  const refreshBtn   = document.getElementById("refreshDevices");
  const deviceName = document.getElementById("deviceName");
  const LS_KEY       = "selectedDevice";

  function updateDeviceSummary() {
    const txt = deviceSel?.selectedOptions?.[0]?.textContent || "—";
    if (deviceName) deviceName.textContent = txt;
  }

  // ---- Selector de dispositivo ----
  async function loadDevices() {
  const btn = refreshBtn;
    try {
      btn && (btn.disabled = true, btn.textContent = "⏳");
      let devices = [{ id: "none", name: "none" }];
      const r = await fetch(`/api/v1/devices?t=${Date.now()}`, { cache: "no-store" });
      
      if (r.ok) {
        const data = await r.json();
        if (Array.isArray(data) && data.length) devices = data;
      }

      // poblar <select>
      if (deviceSel) {
        deviceSel.innerHTML = "";
        for (const d of devices) {
          const opt = document.createElement("option");
          opt.value = d.id;
          opt.textContent = d.name || d.id;
          deviceSel.appendChild(opt);
        }
        // restaurar ultima selección si existe
        const prev = localStorage.getItem(LS_KEY);
        if (prev && [...deviceSel.options].some(o => o.value === prev)) {
          deviceSel.value = prev;
        } else if (deviceSel.options.length) {
          deviceSel.value = deviceSel.options[0].value;
        }
        localStorage.setItem(LS_KEY, deviceSel.value);
        updateDeviceSummary();  

        // UX: si solo hay "none", deshabilita el selector
        deviceSel.disabled = (devices.length === 1 && devices[0].id === "none");
      }
    } catch (_) {
    // silenciar
    } finally {
      btn && (btn.disabled = false, btn.textContent = "🔄");
    }
  }

  deviceSel?.addEventListener("change", () => {
    localStorage.setItem(LS_KEY, deviceSel.value);
    updateDeviceSummary();  
  });

 refreshBtn?.addEventListener("click", (e) => {
    e.preventDefault();
    loadDevices();
  });

  window.addEventListener("visibilitychange", () => {
  if (document.visibilityState === "visible") loadDevices();
  });
  window.addEventListener("pageshow", (e) => {
    if (e.persisted) loadDevices(); // Safari bfcache
  });

  // Monkey-patch: inyectar target en todos los comandos sin tocar el resto de módulos
  if (window.Remote?.api?.sendCommand) {
    const originalSend = window.Remote.api.sendCommand;
    window.Remote.api.sendCommand = (args = {}) => {
      const chosen = deviceSel?.value || localStorage.getItem(LS_KEY) || "local";
      return originalSend({ ...args, target: args.target ?? chosen });
    };
  }

  function sendSystem(action) {
    Remote.api.sendCommand({
      type: "SYSTEM",
      action,              // "VOLUME_UP" | "VOLUME_DOWN" | "TOGGLE_MUTE"
      payload: {},         // sin payload
    });
  }

  if (volUp)   volUp.addEventListener("click",   () => sendSystem("VOLUME_UP"));
  if (volDown) volDown.addEventListener("click", () => sendSystem("VOLUME_DOWN"));
  if (volMute) volMute.addEventListener("click", () => sendSystem("TOGGLE_MUTE"));

  if (mediaPlayPauseBtn)   mediaPlayPauseBtn.addEventListener("click",   () => sendSystem("PLAY_PAUSE"));
  if (mediaNextBtn) mediaNextBtn.addEventListener("click", () => sendSystem("NEXT"));
  if (mediaPrevBtn) mediaPrevBtn.addEventListener("click", () => sendSystem("PREV"));



  // Cargar lista al inicio
  loadDevices();
  setInterval(loadDevices, 15000);

  // 1) ratio fallback inmediato + sync con el PC
  Remote.screen.init({ pad, screenInfo, syncButton: syncBtn }); // 

  // 2) UI (sensibilidad/botones)
  Remote.ui.init({
    sens, sensVal, absT: absToggle,
    text, sendBtn,
    lclickBtn, rclickBtn, scrollUpBtn,
    scrollDownBtn
  }); // 

  // 3) Gestos del pad
  Remote.pad.init({ pad }); // 
})();
