window.Remote = window.Remote || {};

Remote.pad = (() => {
  let padEl, lastX = null, lastY = null, lastSend = 0;
  let rafPending = false, queuedDX = 0, queuedDY = 0;
  const throttleMs = 16;

  // --- Tap detection ---
  let tapStartTime = 0;
  let movedTooMuch = false;
  const TAP_MAX_DURATION = 250; // ms
  const TAP_MOVE_TOLERANCE = 10; // px

  function reset() {
    lastX = null; lastY = null;
    queuedDX = 0; queuedDY = 0;
    rafPending = false;
    tapStartTime = 0;
    movedTooMuch = false;
  }

  function sendRelative(dx, dy) {
    const now = performance.now();
    if (now - lastSend < throttleMs) {
      queuedDX += dx; queuedDY += dy;
      if (!rafPending) {
        rafPending = true;
        requestAnimationFrame(() => {
          rafPending = false;
          const s = Remote.ui.getSensitivity();
          const sdx = Math.round(queuedDX * s);
          const sdy = Math.round(queuedDY * s);
          queuedDX = 0; queuedDY = 0;
          if (sdx === 0 && sdy === 0) return;
          lastSend = performance.now();
          Remote.api.sendCommand({ type:"MOUSE", action:"MOVE", payload:{ mode:"RELATIVE", dx: sdx, dy: sdy }});
        });
      }
      return;
    }
    lastSend = now;
    const s = Remote.ui.getSensitivity();
    const sdx = Math.round(dx * s);
    const sdy = Math.round(dy * s);
    if (sdx === 0 && sdy === 0) return;
    Remote.api.sendCommand({ type:"MOUSE", action:"MOVE", payload:{ mode:"RELATIVE", dx: sdx, dy: sdy }});
  }

  function pointerDown(e){
    padEl.setPointerCapture?.(e.pointerId);
    lastX=e.clientX;
    lastY=e.clientY;
    tapStartTime = Date.now();
    movedTooMuch = false;     
  }
  function pointerMove (e){
    if (!Remote.ui.isRelative()) return;
    if (lastX==null||lastY==null){ lastX=e.clientX; lastY=e.clientY; return; }
    const dx=e.clientX-lastX, dy=e.clientY-lastY;

    if (Math.abs(dx) > TAP_MOVE_TOLERANCE || Math.abs(dy) > TAP_MOVE_TOLERANCE) {
      movedTooMuch = true;
    }

    lastX=e.clientX; lastY=e.clientY;
    sendRelative(dx,dy);
  }

  function pointerUp(e){
    const tapDuration = Date.now() - tapStartTime;
    if (tapDuration <= TAP_MAX_DURATION && !movedTooMuch) {
      Remote.api.sendCommand({
        type:"MOUSE", action:"CLICK", payload:{ button:"LEFT" }
      });
    } else if (!Remote.ui.isRelative()) {
      const { x,y } = Remote.screen.toAbsolute(e.clientX, e.clientY);
      Remote.api.sendCommand({ type:"MOUSE", action:"MOVE", payload:{ mode:"ABSOLUTE", x, y }});
    }
    reset();
  }

  function touchStart(e){
    const t=e.touches[0];
    lastX=t.clientX;
    lastY=t.clientY;
    tapStartTime = Date.now();
    movedTooMuch = false;
  }

  function touchMove (e){
    if (!Remote.ui.isRelative()) return;
    const t=e.touches[0]; if(!t) return;
    if (lastX==null||lastY==null){ lastX=t.clientX; lastY=t.clientY; return; }
    const dx=t.clientX-lastX, dy=t.clientY-lastY;

    if (Math.abs(dx) > TAP_MOVE_TOLERANCE || Math.abs(dy) > TAP_MOVE_TOLERANCE) {
      movedTooMuch = true;
    }

    lastX=t.clientX; lastY=t.clientY;
    sendRelative(dx,dy);
    e.preventDefault();
  }

  function touchEnd(e){
    const tapDuration = Date.now() - tapStartTime;
    if (tapDuration <= TAP_MAX_DURATION && !movedTooMuch) {
      Remote.api.sendCommand({
        type:"MOUSE", action:"CLICK", payload:{ button:"LEFT" }
      });
    } else if (!Remote.ui.isRelative()) {
      const t=e.changedTouches[0]; if(!t) return;
      const { x,y } = Remote.screen.toAbsolute(t.clientX, t.clientY);
      Remote.api.sendCommand({ type:"MOUSE", action:"MOVE", payload:{ mode:"ABSOLUTE", x, y }});
    }
    reset();
  }

  function init({ pad }) {
    padEl = pad;
    padEl.addEventListener("contextmenu", e => e.preventDefault());
    padEl.addEventListener("gesturestart", e => e.preventDefault?.());
    padEl.addEventListener("dragstart", e => e.preventDefault());

    padEl.addEventListener("pointerdown", pointerDown);
    padEl.addEventListener("pointermove", pointerMove);
    padEl.addEventListener("pointerup",   pointerUp);

    padEl.addEventListener("touchstart",  touchStart, { passive:false });
    padEl.addEventListener("touchmove",   touchMove,  { passive:false });
    padEl.addEventListener("touchend",    touchEnd,   { passive:false });

    // Evita saltos al girar el móvil
    window.addEventListener("resize", reset);
    window.addEventListener("orientationchange", reset);
  }

  return { init, reset };
})();
