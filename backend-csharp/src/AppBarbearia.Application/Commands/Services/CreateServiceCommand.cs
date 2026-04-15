using MediatR;
using AppBarbearia.Application.Common;

namespace AppBarbearia.Application.Commands.Services;

public record CreateServiceCommand(
    string  Name,
    string  Description,
    decimal Price,
    int     DurationMinutes
) : IRequest<Result<Guid>>;
