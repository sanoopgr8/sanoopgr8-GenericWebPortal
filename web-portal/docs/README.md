# Documentation Index

Welcome to the Web Portal documentation. This directory contains all project documentation organized by category.

## 📂 Directory Structure

```
docs/
├── architecture/          # System architecture and design
│   └── architecture.md   # System architecture diagram
├── setup/                # Installation and configuration guides
│   └── LIQUIBASE_GUIDE.md  # Database migration guide
├── features/             # Feature-specific documentation
│   └── SIGNUP_IMPLEMENTATION.md  # User signup and verification
└── CLEANUP_SUMMARY.md    # Project cleanup history
```

## 📖 Documentation Categories

### Architecture
Documentation related to system design, architecture diagrams, and technical decisions.

- **[System Architecture](architecture/architecture.md)** - Complete system architecture with diagrams

### Setup Guides
Step-by-step guides for setting up and configuring the application.

- **[Liquibase Setup](setup/LIQUIBASE_GUIDE.md)** - Database migration management with Liquibase

### Features
Detailed documentation for each feature of the application.

- **[Signup & Email Verification](features/SIGNUP_IMPLEMENTATION.md)** - User registration and email verification system

### General
General project documentation and maintenance records.

- **[Cleanup Summary](CLEANUP_SUMMARY.md)** - Record of project cleanup activities

## 🔍 Quick Links

### For Developers
- [Getting Started](../README.md#-quick-start)
- [Project Structure](../README.md#-project-structure)
- [Tech Stack](../README.md#️-tech-stack)

### For System Administrators
- [Database Setup](setup/LIQUIBASE_GUIDE.md)
- [Configuration](../README.md#-configuration)

### For Architects
- [System Architecture](architecture/architecture.md)
- [Technology Decisions](setup/LIQUIBASE_GUIDE.md#why-use-liquibase)

## 📝 Documentation Standards

When adding new documentation:

1. **Choose the right category**:
   - `architecture/` - Design and architecture documents
   - `setup/` - Installation and configuration guides
   - `features/` - Feature-specific documentation

2. **Use clear naming**:
   - Use descriptive names in UPPERCASE with underscores
   - Example: `FEATURE_NAME_GUIDE.md`

3. **Include**:
   - Clear title and description
   - Table of contents for long documents
   - Code examples where applicable
   - Diagrams for visual concepts

4. **Update this index** when adding new documents

## 🔄 Keeping Documentation Updated

- Update documentation when features change
- Review documentation during code reviews
- Archive outdated documentation in `docs/archive/`
- Keep the main README.md in sync with docs

## 📧 Questions?

If you can't find what you're looking for, check the main [README](../README.md) or contact the development team.
