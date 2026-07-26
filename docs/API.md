# API map

All endpoints use the `/api/v1` prefix and return the standard
`ApiResponse<T>` envelope.

| Area | Endpoints |
| --- | --- |
| Authentication | `POST /auth/login`, `/auth/refresh`, `/auth/logout`, `/auth/customer-login` |
| Customers | `GET/POST /customers`, `GET/PUT/DELETE /customers/{id}`, `GET /customers/{id}/summary` |
| Policies | `GET/POST /policies`, `GET/DELETE /policies/{id}`, `PUT /policies/{id}/renew`, `GET /policies/dashboard-stats`, `/policies/expiring` |
| Vehicles | `GET/POST /vehicles`, `GET/DELETE /vehicles/{id}` |
| Follow-ups | `GET /followups/today`, `GET /followups/customer/{id}`, `POST /followups` |
| Notifications | `GET /notifications`, `GET /notifications/unread-count`, `POST /notifications/send/{id}` |
| Reports | `GET /reports/expiry-list`, `/reports/commission`, `/reports/export/excel` |
| Customer portal | `GET /portal/my-policies`, `/my-vehicles`, `/my-claims`, `/my-notifications`, `POST /portal/renewal-request` |

Interactive OpenAPI documentation is available at `/swagger-ui.html`.

