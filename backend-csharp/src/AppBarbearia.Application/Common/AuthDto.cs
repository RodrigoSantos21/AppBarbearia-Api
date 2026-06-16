using AppBarbearia.Domain.Enums;

namespace AppBarbearia.Application.Common;

public record AuthResponseDto(
    string AccessToken,
    string TokenType,
    int ExpiresIn,
    UserDto User
);

public record UserDto(
    Guid   Id,
    string FullName,
    string Email,
    string Phone,
    string Role,
    bool   IsActive,
    DateTime CreatedAt
);
