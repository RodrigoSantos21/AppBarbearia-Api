namespace AppBarbearia.Application.Common;

public record ServiceDto(
    Guid    Id,
    string  Name,
    string  Description,
    decimal Price,
    int     DurationMinutes,
    bool    IsActive,
    DateTime CreatedAt,
    DateTime? UpdatedAt
);
