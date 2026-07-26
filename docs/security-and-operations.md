# Security and operations

- Secrets belong only in deployment secret stores or ignored local `.env` files.
- Access tokens are never persisted to browser storage. Refresh tokens are hashed at rest and rotated/revoked server-side.
- Configure TLS, secure/SameSite cookies, a strict allowed-origin list, database least privilege, backups and alerting.
- Review audit events and notification attempts for incident response; mask email/phone values in user-facing logs.
- Rotate JWT/provider/database credentials through the deployment platform and invalidate all sessions after a credential compromise.
