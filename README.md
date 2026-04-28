# TP 1: Pipeline de Órdenes con Apache Kafka + Spring Boot

## 👥 Integrantes del Equipo

- **Badami, Antonella**
- **Biondi, Fabricio**
- **Perez Lopez, Agostina**
- **Vargas, Benjamin**

---

## 📋 Descripción del Proyecto

Sistema basado en eventos implementado con **Apache Kafka** y **Spring Boot**, compuesto por 4 microservicios que conforman un pipeline de procesamiento de órdenes. 

El sistema demuestra conceptos clave de Kafka:
- **Event Streaming**: Flujo de eventos entre servicios
- **Pub/Sub Pattern**: Producers y Consumers desacoplados
- **Consumer Groups**: Fan-out con múltiples consumidores
- **Dead Letter Queue (DLQ)**: Manejo de errores y mensajes inválidos
- **Tolerancia a fallos**: Recuperación automática de caídas

---

## 📁 Estructura del Proyecto

```
TP1/
├── docker-compose.yml          # Orquestación de servicios (Kafka, UI, microservicios)
├── .env.example                # Variables de entorno de ejemplo
├── .env                        # Variables de entorno (local, no versionado)
│
├── api/                        # Order API - Producer
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
│
├── validator/                  # Order Validator - Consumer + Producer
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
│
├── order-notifications/        # Order Notifications - Consumer
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
│
└── order-audit/                # Order Audit - Consumer
    ├── src/
    ├── pom.xml
    └── Dockerfile
```

---

## 🚀 Requisitos Previos

- **Docker** (versión 20.10+)
- **Docker Compose** (versión 1.29+)
- **Postman** (opcional, para pruebas HTTP)
- **Git**


## ⚙️ Variables de Entorno

El archivo `.env.example` contiene todas las variables necesarias:

```bash
# Kafka Configuration
KAFKA_PORT=9092                    # Puerto interno de Kafka
KAFKA_EXTERNAL_PORT=29092          # Puerto externo (host machine)
KAFKA_UI_PORT=8080                 # Puerto de Kafka UI
KAFKA_NODE_ID=1                    # ID del nodo Kafka
KAFKA_CLUSTER_ID=MkU3OEVBNTcwNTJENDM2Qk
KAFKA_RETENTION_HOURS=168          # Retención de 7 días
KAFKA_PARTITIONS=3                 # 3 particiones por topic
KAFKA_REPLICATION_FACTOR=1         # Factor de replicación
```

---

## 🐳 Levantando el Sistema con Docker Compose

### Paso 1: Clonar el repositorio y configurar variables de entorno

```bash
git clone <repository-url>
cd TP1

# Copiar variables de entorno de ejemplo
cp .env.example .env
```

### Paso 2: Levantar todos los servicios

```bash
# Levanta todos los contenedores en background
docker-compose up -d

# Ver logs en tiempo real
docker-compose logs -f
```

### Paso 3: Verificar que todo esté corriendo

```bash
# Ver estado de los servicios
docker-compose ps

# Ver logs específicos del broker (para verificar que Kafka está listo)
docker-compose logs broker | grep -i "started"

# Ver logs del init-topics (creación de topics)
docker-compose logs init-topics
```

**Esperado:**
```
✓ broker        - Healthy (despues de ~30 seg)
✓ kafka-ui      - Corriendo
✓ order-api     - Corriendo en puerto 8081
✓ order-validator - Corriendo en puerto 8082
✓ order-audit   - Corriendo en puerto 8083
✓ order-notifications - Corriendo en puerto 8084
```

### Paso 4: Acceder a Kafka UI

Abrir en el navegador:
```
http://localhost:8080
```

En Kafka UI puedes ver:
- **Clusters**: Estado del cluster `local`
- **Topics**: `orders.created.v1`, `orders.validated.v1`, `orders.validation.dlq.v1`
- **Messages**: Mensajes en cada topic
- **Consumer Groups**: `order-audit-group`, `order-notifications-group`, `order-validator-group`

---

## 📊 Descripción de Servicios

### 1. **Order API** (Puerto 8081)
**Rol**: Producer de eventos

- **Endpoint**: `POST /orders`
- **Descripción**: Recibe órdenes y las publica en el topic `orders.created.v1`
- **Topic Destino**: `orders.created.v1`

**Ejemplo de solicitud (Postman)**:
```json
POST http://localhost:8081/orders

{
  "orderId": "ORD-001",
  "customerId": "CUST-001",
  "amount": 150.50,
  "currency": "USD",
  "items": [
    {
      "productId": "PROD-001",
      "quantity": 2,
      "price": 75.25
    }
  ]
}
```

---

### 2. **Order Validator** (Puerto 8082)
**Rol**: Consumer → Producer (valida y enruta mensajes)

- **Consume de**: `orders.created.v1`
- **Validaciones**:
  - Monto > 0
  - Moneda en {ARS, USD, EUR}
  - Items no vacío

- **Produce a**: 
  - `orders.validated.v1` (si es válida)
  - `orders.validation.dlq.v1` (si es inválida)

**Consumer Group**: `order-validator-group`

---

### 3. **Order Notifications** (Puerto 8084)
**Rol**: Consumer (fan-out)

- **Consume de**: `orders.validated.v1`
- **Acción**: Registra notificaciones enviadas (logs)
- **Consumer Group**: `order-notifications-group`

---

### 4. **Order Audit** (Puerto 8083)
**Rol**: Consumer (fan-out)

- **Consume de**: `orders.validated.v1`
- **Acción**: Registra auditoría de órdenes procesadas (logs)
- **Consumer Group**: `order-audit-group`

---

## 📝 Topics de Kafka

| Topic | Particiones | Tipo | Descripción |
|-------|-------------|------|-------------|
| `orders.created.v1` | 3 | Event | Órdenes recibidas por el API |
| `orders.validated.v1` | 3 | Event | Órdenes validadas correctamente |
| `orders.validation.dlq.v1` | 3 | DLQ | Órdenes rechazadas (errores) |

---

## ✅ Pruebas del Sistema

### Test 1: Enviar orden válida

```bash
curl -X POST http://localhost:8081/orders \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "ORD-001",
    "customerId": "CUST-001",
    "amount": 100.00,
    "currency": "USD",
    "items": [{"productId": "PROD-001", "quantity": 1, "price": 100}]
  }'
```

**Verificar en Kafka UI**:
1. Topic `orders.created.v1`: Ver el mensaje publicado
2. Topic `orders.validated.v1`: Ver el mensaje procesado
3. Logs: Ver mensajes en `order-audit` y `order-notifications`

### Test 2: Enviar orden inválida (DLQ)

```bash
curl -X POST http://localhost:8081/orders \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "ORD-002",
    "customerId": "CUST-002",
    "amount": -50.00,
    "currency": "MXN",
    "items": []
  }'
```

**Verificar en Kafka UI**:
1. Topic `orders.validation.dlq.v1`: Debe aparecer el mensaje con el error

### Test 3: Tolerancia a fallos

```bash
# 1. Detener el servicio de notificaciones
docker-compose stop order-notifications

# 2. Enviar órdenes válidas
curl -X POST http://localhost:8081/orders ...

# 3. Ver que se acumulan en el topic (offsets)
# Ir a Kafka UI → orders.validated.v1 → Ver mensajes pendientes

# 4. Reiniciar el servicio
docker-compose start order-notifications

# 5. Ver que procesa automáticamente el backlog
docker-compose logs order-notifications -f
```

---

## 🛠️ Comandos Útiles

### Ejecutar comandos en Kafka
```bash
# Listar topics
docker exec kafka-broker kafka-topics.sh --list --bootstrap-server localhost:9092

# Consumir mensajes desde inicio de un topic
docker exec -it kafka-broker kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic orders.created.v1 \
  --from-beginning

# Ver información de un topic
docker exec kafka-broker kafka-topics.sh --describe --topic orders.created.v1 \
  --bootstrap-server localhost:9092
```

---

## 🔍 Monitoreo y Debugging

### Kafka UI - Recomendado
Acceder a `http://localhost:8080` para una interfaz visual completa

### Ver offsets por consumer group
```bash
docker exec kafka-broker kafka-consumer-groups.sh \
  --bootstrap-server localhost:9092 \
  --group order-notifications-group \
  --describe
```