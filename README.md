# AppBarbearia — C# CQS + MediatR + FluentValidation + EF Core

## Stack

| Biblioteca | Versão | Uso |
|---|---|---|
| MediatR | 12.x | Dispatcher de Commands e Queries |
| FluentValidation | 11.x | Validação declarativa de requests |
| Entity Framework Core | 8.x | ORM + migrations |
| SQL Server / LocalDB | — | Banco padrão (configurável) |

## Estrutura

```
AppBarbearia/
└── src/
    ├── AppBarbearia.Domain/          # Entidades + Interfaces (sem dependências externas)
    ├── AppBarbearia.Application/     # Commands, Queries, Validators, Pipeline
    ├── AppBarbearia.Infrastructure/  # EF Core DbContext, Repositórios concretos
    └── AppBarbearia.API/             # Controllers, Middleware, Program.cs
```

## Padrões aplicados

### CQS (Command Query Separation)
- **Commands** (`CreateProductCommand`, `UpdateProductCommand`, `DeleteProductCommand`): alteram estado, retornam `Result` ou `Result<T>` mínimo.
- **Queries** (`GetProductByIdQuery`, `GetAllProductsQuery`): leem estado, nunca alteram nada.

### MediatR Pipeline
```
Request → ValidationBehavior → Handler → Response
```
O `ValidationBehavior<TRequest, TResponse>` intercepta cada request, roda todos os validators registrados e lança `ValidationException` antes do handler ser chamado.

### Repository Pattern
- `IRepository<T>` — interface genérica com operações CRUD
- `Repository<T>` — implementação base com EF Core
- `IProductRepository` / `ProductRepository` — especialização com queries específicas

## Como rodar

### 1. Pré-requisitos
- .NET 8 SDK
- SQL Server ou LocalDB

### 2. Configurar connection string

Edite `src/AppBarbearia.API/appsettings.json`:
```json
{
  "ConnectionStrings": {
    "DefaultConnection": "Server=(localdb)\\mssqllocaldb;Database=AppBarbeariaDb;Trusted_Connection=True;"
  }
}
```

### 3. Criar o banco via migrations
```bash
cd src/AppBarbearia.API
dotnet ef migrations add InitialCreate --project ../AppBarbearia.Infrastructure
dotnet ef database update --project ../AppBarbearia.Infrastructure
```

### 4. Rodar
```bash
dotnet run --project src/AppBarbearia.API
```

Acesse: `https://localhost:5001/swagger`

## Endpoints

| Método | Rota | Descrição |
|---|---|---|
| GET | `/api/products` | Lista todos (query: `?onlyActive=true`) |
| GET | `/api/products/{id}` | Busca por ID |
| POST | `/api/products` | Cria novo produto |
| PUT | `/api/products/{id}` | Atualiza produto |
| DELETE | `/api/products/{id}` | Remove produto |

## Exemplo de request

```json
POST /api/products
{
  "name": "Notebook Pro",
  "description": "Notebook de alta performance",
  "price": 4999.90,
  "stockQuantity": 10
}
```

Resposta de validação (400):
```json
{
  "errors": [
    { "propertyName": "Price", "errorMessage": "Price must be greater than zero." }
  ]
}
```
