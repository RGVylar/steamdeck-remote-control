// main.js
(function bootstrap() {
  const pad        = document.getElementById("pad");
  const sens       = document.getElementById("sens");
  const sensVal    = document.getElementById("sensVal");
  const absToggle  = document.getElementById("absToggle");
  const text       = document.getElementById("text");
  const sendBtn    = document.getElementById("send");
  const focusBtn   = document.getElementById("focus");
  const escBtn     = document.getElementById("esc");
  const lclickBtn  = document.getElementById("lclick");
  const rclickBtn  = document.getElementById("rclick");
  const screenInfo = document.getElementById("screenInfo");
  const syncBtn    = document.getElementById("syncScreen");

  // 1) ratio fallback inmediato + sync con el PC
  Remote.screen.init({ pad, screenInfo, syncButton: syncBtn });

  // 2) UI (sensibilidad/botones)
  Remote.ui.init({
    sens, sensVal, absT: absToggle,
    text, sendBtn, focusBtn, escBtn, lclickBtn, rclickBtn
  });

  // 3) Gestos del pad
  Remote.pad.init({ pad });
})();
