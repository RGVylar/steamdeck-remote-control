window.Remote = window.Remote || {};

Remote.ui = (() => {
  const SENS_KEY = "padSensitivity";
  let sensitivity = Number(localStorage.getItem(SENS_KEY) ?? "5.0");
  let sensInput, sensLabel, absToggle, textArea;

  function getSensitivity() { return sensitivity || 1.0; }
  function isRelative() { return !(absToggle && absToggle.checked); }

  function init({ sens, sensVal, absT, text, sendBtn, focusBtn, escBtn, lclickBtn, rclickBtn }) {
    sensInput = sens || null;
    sensLabel = sensVal || null;
    absToggle = absT || null;
    textArea = text || null;

    if (sensInput && sensLabel) {
      sensInput.value = String(sensitivity);
      sensLabel.textContent = `${sensitivity.toFixed(1)}×`;
      sensInput.addEventListener("input", () => {
        sensitivity = Number(sensInput.value);
        sensLabel.textContent = `${sensitivity.toFixed(1)}×`;
        localStorage.setItem(SENS_KEY, String(sensitivity));
      });
    }
    
    sendBtn?.addEventListener("click", async () => {
      const v = (textArea?.value || "").trim();
      if (!v) return;
      await Remote.api.sendCommand({ type: "TEXT_INPUT", action: "TYPE", payload: { text: v + "\n" } });
      textArea.value = "";
    });

    focusBtn?.addEventListener("click", async () => {
      await Remote.api.sendCommand({ type: "TEXT_INPUT", action: "TYPE", payload: { text: "" } });
    });

    escBtn?.addEventListener("click", async () => {
      // Implementa cuando tengas KEYBOARD.PRESS ESCAPE en el backend
      // await Remote.api.sendCommand({ type:"KEYBOARD", action:"PRESS", payload:{ key:"ESC" }});
      console.log("ESC pendiente");
    });

    lclickBtn?.addEventListener("click", () =>
      Remote.api.sendCommand({ type: "MOUSE", action: "CLICK", payload: { button: "LEFT" } })
    );
    rclickBtn?.addEventListener("click", () =>
      Remote.api.sendCommand({ type: "MOUSE", action: "CLICK", payload: { button: "RIGHT" } })
    );
  }

  return { init, getSensitivity, isRelative };
})();
