# SteamDeck Remote Control

[![Java](https://img.shields.io/badge/Java-17-red?logo=openjdk)]()
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-green?logo=springboot)]()
[![Apache Kafka](https://img.shields.io/badge/Kafka-3.7-cyan?logo=apachekafka)]()
[![Frontend](https://img.shields.io/badge/Frontend-HTML%2FCSS%2FJS-blue?logo=javascript)]()
[![License](https://img.shields.io/badge/license-MIT-lightgrey.svg)]()
![GitHub last commit](https://img.shields.io/github/last-commit/rgvylar/steamdeck-remote-control)
![GitHub repo size](https://img.shields.io/github/repo-size/rgvylar/steamdeck-remote-control)

Controla tu **PC o Steam Deck** desde el móvil, incluso varios dispositivos a la vez:

- **QR automático** al arrancar → escanea y entra directo al control remoto.
- **Configuración inicial** (Device ID, broker Kafka, puerto).
- **Mover el ratón** (modo relativo o absoluto).
- **Clic izquierdo/derecho** y **scroll**.
- **Enviar texto** desde el móvil (con Enter).
- **Atajos rápidos** (ESC, CTRL+L).
- **Selector de dispositivo remoto** (elige a qué equipo mandar comandos).
- **Botones multimedia**: 🔊 Subir volumen, 🔉 Bajar volumen, 🔇 Silencio, ⏯ Play/Pause, ⏮ Anterior, ⏭ Siguiente.
- **Salir**: cierra la app de forma segura desde el móvil.

---

## Arquitectura

```
Móvil (web UI) -> REST API (Spring Boot) -> Kafka (topic: commands) -> Consumer -> InputExecutor (Robot)
```

- **Controller**: recibe comandos vía `/api/v1/commands`.
- **Service**: valida y los publica en Kafka.
- **Consumer**: escucha `commands` y los ejecuta en el sistema (teclas/ratón/volumen).
- **Frontend**: página web ligera servida por Spring Boot.

---

## Requisitos

- **Java 17+** (solo si usas el JAR directo).
- **Maven** (para compilar desde código).
- **Kafka o Redpanda** corriendo en al menos un PC de la LAN  
  (el resto de PCs/Decks solo necesitan la app, se conectan al broker).
- Navegador en el móvil (Chrome/Firefox).

---

## 🔧 Configuración rápida

1. Descarga la última release desde [Releases](https://github.com/RGVylar/steamdeck-remote-control/releases):
   - `Steamdeck.Remote.Control.Installer-<versión>.msi` → instalador con acceso directo e icono.
   - `Steamdeck.Remote.Control.Portable.zip` → versión portable con `.exe` listo para usar.
   - `steamdeck-remote-control-<versión>.jar` → ejecutable independiente (requiere Java 17+).
   - `docker-compose.yml` → para levantar **Redpanda** (Kafka) rápidamente.

2. Levanta Redpanda si no tienes un broker en tu red:
   ```bash
   docker compose -f docker-compose.yml up -d
   ```

3. Arranca la aplicación (instalador, portable o JAR).  
   Al abrir por primera vez:
   - Configura **Device ID**, **Kafka bootstrap server** y **puerto**.  
   - Se abrirá el navegador en `/qr` con un QR listo para escanear desde el móvil.


---

## Uso

- **Selector de dispositivo**: en el móvil eliges qué equipo recibe los comandos (`portatil`, `steamdeck`, `sobremesa`).
- **Mover ratón**: desliza en el trackpad.
- **Click**: botones 🖱️ L / 🖱️ R.
- **Scroll**: botones ▲ / ▼.
- **Teclado**: escribe en la caja de texto → "Enviar + Enter".
- **Atajos**:
  - `CTRL+L` → enfocar barra navegador.
  - `ESC` → salir/atrás.
- **Multimedia**:
  - 🔊 Subir volumen
  - 🔉 Bajar volumen
  - 🔇 Silenciar
  - ⏯ Play/Pause
  - ⏮ Anterior
  - ⏭ Siguiente
- **Salir**: botón ✖ cierra la app remotamente.

---

## Seguridad

- API Key opcional por cabecera `X-API-KEY`.
- Ejecutar en LAN segura, no exponer sin auth.

---

## Roadmap

- [x] Movimiento ratón (relativo/absoluto).
- [x] Click izquierdo/derecho.
- [x] Scroll.
- [x] Enviar texto.
- [x] Atajos (ESC, CTRL+L).
- [x] Botones multimedia (volumen/mute/play/pause/next/prev).
- [x] Selector de dispositivo.
- [x] QR automático.
- [x] Instalador MSI y portable.
- [ ] Macros.
- [ ] WebSocket para entrada en tiempo real.
- [ ] UI móvil más avanzada (PWA).

---

## 📜 Licencia

MIT © 2025 — Proyecto educativo y personal.
