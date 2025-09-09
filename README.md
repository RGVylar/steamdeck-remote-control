# SteamDeck Remote Control

[![Java](https://img.shields.io/badge/Java-17-red?logo=openjdk)]()
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-green?logo=springboot)]()
[![Apache Kafka](https://img.shields.io/badge/Kafka-3.7-black?logo=apachekafka)]()
[![Frontend](https://img.shields.io/badge/Frontend-HTML%2FCSS%2FJS-blue?logo=javascript)]()
[![License](https://img.shields.io/badge/license-MIT-lightgrey.svg)]()
![GitHub last commit](https://img.shields.io/github/last-commit/rgvylar/steamdeck-remote-control)
![GitHub repo size](https://img.shields.io/github/repo-size/rgvylar/steamdeck-remote-control)


Controla tu **PC o Steam Deck** desde el móvil:
- Mueve el ratón (modo relativo o absoluto).
- Haz clic izquierdo/derecho.
- Haz scroll.
- Envía texto desde el teclado del móvil (con Enter).
- Atajos rápidos (ESC, CTRL+L).
- Selección de dispositivo remoto.
- Botones multimedia: 🔊 Subir volumen, 🔉 Bajar volumen, 🔇 Silencio.

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

- **Java 17+**
- **Maven**
- **Kafka o Redpanda** local
- Navegador en el móvil (Chrome/Firefox)

---

## 🔧 Configuración rápida

1. Clona el repo:
   ```bash
   git clone https://github.com/tuusuario/steamdeck-remote-control.git
   cd steamdeck-remote-control
   ```

2. Levanta Kafka (ejemplo con Redpanda):
   ```bash
   docker run -p 9092:9092 -p 9644:9644 docker.redpanda.com/redpandadata/redpanda:latest \
     redpanda start --overprovisioned --smp 1 --memory 1G --reserve-memory 0M --node-id 0 \
     --check=false --kafka-addr PLAINTEXT://0.0.0.0:9092 \
     --advertise-kafka-addr PLAINTEXT://localhost:9092
   ```

3. Arranca el backend:
   ```bash
   mvn spring-boot:run
   ```

4. Abre el **frontend** en el móvil/PC:
   ```
   http://<IP-PC>:8080
   ```

---

## Uso

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

---

## Seguridad

- API Key opcional por cabecera `X-API-KEY`.
- Ejecutar en LAN segura, no exponer sin auth.

---

## Ejemplos curl

```bash
# Mover ratón
curl -X POST http://localhost:8080/api/v1/commands \
  -H "Content-Type: application/json" \
  -d '{"type":"MOUSE","action":"MOVE","payload":{"x":960,"y":540,"mode":"ABSOLUTE"},"target":"local"}'

# Escribir texto
curl -X POST http://localhost:8080/api/v1/commands \
  -H "Content-Type: application/json" \
  -d '{"type":"TEXT_INPUT","action":"TYPE","payload":{"text":"hola mundo"},"target":"local"}'
```

---

## Roadmap

- [x] Movimiento ratón (relativo/absoluto).
- [x] Click izquierdo/derecho.
- [x] Scroll.
- [x] Enviar texto.
- [x] Atajos (ESC, CTRL+L).
- [x] Botones multimedia (volumen/mute).
- [x] Selector de dispositivo.
- [ ] Macros.
- [ ] WebSocket para entrada en tiempo real.
- [ ] UI móvil más avanzada (PWA).

---

## 📜 Licencia

MIT © 2025 — Proyecto educativo y personal.
