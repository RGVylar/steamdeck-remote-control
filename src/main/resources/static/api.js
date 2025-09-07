window.Remote = window.Remote || {};

Remote.api = (() => {
  const API = "/api/v1/commands";

  function uuid() {
    if (crypto?.randomUUID) return crypto.randomUUID();
    return "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, c => {
      const r = (Math.random() * 16) | 0, v = c === "x" ? r : (r & 0x3) | 0x8;
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

  return { sendCommand };
})();
