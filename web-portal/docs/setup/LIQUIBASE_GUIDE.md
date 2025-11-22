# Liquibase Integration

## What is Liquibase?

Liquibase is a database schema change management tool that allows you to track, version, and deploy database changes in a structured and automated way.

## Why Use Liquibase?

1. **Version Control**: Database changes are tracked in version control alongside code
2. **Reproducibility**: Same migrations run the same way across all environments
3. **Rollback Support**: Ability to rollback changes if needed
4. **Team Collaboration**: Multiple developers can work on database changes without conflicts
5. **Audit Trail**: Complete history of all database changes

## Implementation

### Dependencies Added

```xml
<dependency>
    <groupId>org.liquibase</groupId>
    <artifactId>liquibase-core</artifactId>
</dependency>
```

### Configuration

**application.properties:**
```properties
spring.jpa.hibernate.ddl-auto=none
spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.xml
spring.liquibase.enabled=true
```

- `ddl-auto=none`: Disabled Hibernate's automatic schema generation
- Liquibase now manages all database schema changes

### Directory Structure

```
src/main/resources/
└── db/
    └── changelog/
        ├── db.changelog-master.xml
        └── changes/
            └── 001-create-users-table.xml
```

### Changelog Files

**db.changelog-master.xml**: Master file that includes all changesets
**001-create-users-table.xml**: First changeset that creates the users table

## How It Works

1. On application startup, Liquibase checks the database for a `databasechangelog` table
2. If it doesn't exist, Liquibase creates it
3. Liquibase reads the master changelog file
4. For each changeset, it checks if it has been applied (by looking in `databasechangelog`)
5. If not applied, it executes the changeset and records it

## Adding New Migrations

To add a new database change:

1. Create a new XML file in `db/changelog/changes/` (e.g., `002-add-user-roles.xml`)
2. Add the changeset with a unique ID
3. Include the file in `db.changelog-master.xml`
4. Restart the application - Liquibase will apply the new changeset

### Example New Changeset

```xml
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog
        xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
        http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-4.20.xsd">

    <changeSet id="002-add-user-roles" author="developer">
        <addColumn tableName="users">
            <column name="role" type="VARCHAR(50)" defaultValue="USER">
                <constraints nullable="false"/>
            </column>
        </addColumn>
    </changeSet>

</databaseChangeLog>
```

## Current Schema

The current `users` table schema managed by Liquibase:

- `id` (BIGSERIAL, PRIMARY KEY)
- `first_name` (VARCHAR(255), NOT NULL)
- `last_name` (VARCHAR(255), NOT NULL)
- `email` (VARCHAR(255), NOT NULL, UNIQUE)
- `password` (VARCHAR(255), NOT NULL)
- `verified` (BOOLEAN, DEFAULT false)
- `verification_token` (VARCHAR(255))
- `token_created_at` (TIMESTAMP)

Indexes:
- `idx_users_email` on `email`
- `idx_users_verification_token` on `verification_token`

## Rollback Support

Each changeset includes a rollback section. To rollback the last change:

```bash
mvn liquibase:rollback -Dliquibase.rollbackCount=1
```

## Best Practices

1. **Never modify existing changesets** - Once applied, they should be immutable
2. **Use descriptive IDs** - Format: `XXX-description` (e.g., `001-create-users-table`)
3. **Include rollback** - Always define how to undo a change
4. **Test migrations** - Test on a copy of production data before deploying
5. **Use preconditions** - Prevent errors by checking conditions before applying changes

## Database Tables Created by Liquibase

- `databasechangelog`: Tracks which changesets have been applied
- `databasechangeloglock`: Prevents concurrent migrations

## Advantages Over Hibernate DDL Auto

| Feature | Hibernate DDL Auto | Liquibase |
|---------|-------------------|-----------|
| Version Control | ❌ | ✅ |
| Rollback Support | ❌ | ✅ |
| Production Safe | ❌ | ✅ |
| Data Migrations | ❌ | ✅ |
| Team Collaboration | ❌ | ✅ |
| Audit Trail | ❌ | ✅ |
