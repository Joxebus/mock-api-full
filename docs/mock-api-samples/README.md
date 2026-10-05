# Sample API configurations

Five generic APIs for local testing. Each has **3 operations** with **4 methods**
(`GET` list → 200, `POST` create → 201 + `Location`, `PUT` update → 200, `DELETE` → 204).
Every response includes an `X-Mock-Source: mock-api` header.

| API | Operations | Secured |
|---|---|---|
| `users-api` | `users`, `roles`, `sessions` | no |
| `products-api` | `products`, `categories`, `inventory` | no |
| `orders-api` | `orders`, `payments`, `shipments` | **yes** — `Authorization: Bearer test-token-123` |
| `blog-api` | `posts`, `comments`, `tags` | no |
| `tasks-api` | `projects`, `tasks`, `labels` | no |

## Load them

With the backend running on `:8080`:

```bash
./samples/load-samples.sh                         # or: ./samples/load-samples.sh http://host:port
```

Re-running is safe; `POST /config` overwrites configurations with the same name.
You can also paste any file into the UI's **Create API → JSON** tab.

## Call the mocks

Mocks are served at `/api/{apiName}/{operation}`. The backend requires
`Content-Type: application/json` on every mock request (otherwise it returns **415**).

```bash
curl -H 'Content-Type: application/json' http://localhost:8080/api/users-api/users
curl -X POST -H 'Content-Type: application/json' http://localhost:8080/api/products-api/products
curl -X DELETE -H 'Content-Type: application/json' http://localhost:8080/api/tasks-api/labels

# Secured API: 401 without the header, 200 with it
curl -H 'Content-Type: application/json' -H 'Authorization: Bearer test-token-123' \
  http://localhost:8080/api/orders-api/orders
```

Useful negative cases: an unconfigured method (e.g. `PATCH`) returns **405**, an
unknown operation returns **404**.

## Remove them

```bash
for n in users-api products-api orders-api blog-api tasks-api; do
  curl -X DELETE http://localhost:8080/config/$n; echo
done
```
