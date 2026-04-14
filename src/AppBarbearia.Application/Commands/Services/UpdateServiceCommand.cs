using MediatR;
using AppBarbearia.Application.Common;

namespace AppBarbearia.Application.Commands.Services;

public record UpdateServiceCommand(
    Guid    Id,
    string  Name,
    string  Description,
    decimal Price,
    int     DurationMinutes
) : IRequest<Result>;
